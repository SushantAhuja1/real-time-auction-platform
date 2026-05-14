package com.sushant.auction.bid;

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
}