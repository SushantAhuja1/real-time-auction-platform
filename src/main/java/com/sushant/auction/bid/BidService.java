package com.sushant.auction.bid;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import org.springframework.data.redis.core.RedisTemplate;
import com.sushant.auction.auction.dto.AuctionLeaderboardDto;
import com.sushant.auction.auction.Auction;
import com.sushant.auction.auction.AuctionRepository;
import com.sushant.auction.auction.AuctionStatus;
import com.sushant.auction.bid.dto.BidRequest;
import com.sushant.auction.bid.dto.BidResponse;
import com.sushant.auction.exception.AuctionClosedException;
import com.sushant.auction.exception.BidTooLowException;
import com.sushant.auction.exception.StaleAuctionException;
import com.sushant.auction.user.User;
import com.sushant.auction.websocket.BidBroadcaster;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BidService {
    private final BidRepository bidRepository;
    private final AuctionRepository auctionRepository;
    private final BidBroadcaster bidBroadcaster;
    private final RedisTemplate<String, Object> redisTemplate;

    @Transactional
    public BidResponse placeBid(BidRequest bidRequest, User bidder) {
        //find-auction-in-db
        Auction auction = auctionRepository.findById(bidRequest.getAuctionId()).orElseThrow(()-> new RuntimeException("auction not found"));
        if(auction.getStatus()!= AuctionStatus.LIVE) {
            saveRejectedBid(bidRequest,bidder,BidStatus.REJECTED_CLOSED,"Auction is not live");
            throw new AuctionClosedException("Auction is closed");
        }
        if(bidRequest.getAmount().compareTo(auction.getCurrentHighestBid())<=0) {
            saveRejectedBid(bidRequest,bidder,BidStatus.REJECTED_LOW,"Bid must be higher than current highest bid");
            throw new BidTooLowException("Bid must exceed current highest");
        }
        auction.setCurrentHighestBid(bidRequest.getAmount());
        auction.setCurrentHighestBidderId(bidder.getId());
        long secondsRemaining = Duration.between(Instant.now(), auction.getCloseAt()).getSeconds();
        if (secondsRemaining < 30 && secondsRemaining >= 0) {
            auction.setCloseAt(auction.getCloseAt().plusSeconds(120));
        }
        try{
            auctionRepository.saveAndFlush(auction);
        } catch(OptimisticLockingFailureException ex) {
            saveRejectedBid(bidRequest,bidder,BidStatus.REJECTED_STALE,"Outbid by current user");
            throw new StaleAuctionException("Another bid was accepted first. Reload and retry.");
        }

        Bid bid = Bid
                .builder()
                .auctionId(auction.getId())
                .bidderId(bidder.getId())
                .amount(bidRequest.getAmount())
                .status(BidStatus.ACCEPTED)
                .build();
        Bid savedBid = bidRepository.save(bid);
        bidBroadcaster.broadcastNewBid(auction.getId(), mapToResponse(savedBid));
        AuctionLeaderboardDto leaderboardDto = AuctionLeaderboardDto.builder()
                .auctionId(auction.getId())
                .title(auction.getTitle())
                .currentHighestBid(auction.getCurrentHighestBid())
                .build();

        // 1. Save the Auction details in a Redis Hash (so we know the Title)
        redisTemplate.opsForHash().put("auction_details", auction.getId().toString(), leaderboardDto);

        // 2. Update the Leaderboard ZSET (Sorted Set) with the new high score
        redisTemplate.opsForZSet().add("auction_leaderboard", auction.getId().toString(), bidRequest.getAmount().doubleValue());
        return mapToResponse(savedBid);
    }

    private BidResponse saveRejectedBid(BidRequest bidRequest, User bidder, BidStatus status, String reason) {
        Bid bid = Bid
                .builder()
                .auctionId(bidRequest.getAuctionId())
                .bidderId(bidder.getId())
                .amount(bidRequest.getAmount())
                .status(status)
                .rejectionReason(reason)
                .build();
        return mapToResponse(bidRepository.save(bid));
    }
    private BidResponse mapToResponse(Bid bid) {
        return BidResponse
                .builder()
                .id(bid.getId())
                .auctionId(bid.getAuctionId())
                .bidderId(bid.getBidderId())
                .amount(bid.getAmount())
                .status(bid.getStatus())
                .rejectionReason(bid.getRejectionReason())
                .createdAt(bid.getCreatedAt())
                .build();
    }

    @Transactional(readOnly = true)
    public List<BidResponse> getBidsForAuction(Long auctionId) {
        return bidRepository.findByAuctionIdOrderByCreatedAtDesc(auctionId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<BidResponse> getMyBids(Long bidderId) {
        return bidRepository.findByBidderIdOrderByCreatedAtDesc(bidderId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }
}