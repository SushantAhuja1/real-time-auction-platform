package com.sushant.auction.auction;

import com.sushant.auction.auction.dto.AuctionDto;
import com.sushant.auction.auction.dto.CreateAuctionRequest;
import com.sushant.auction.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuctionService {
    private final AuctionRepository auctionRepository;

    public AuctionDto createAuction(CreateAuctionRequest  createAuctionRequest, User creator) {
        AuctionStatus initialStatus = createAuctionRequest.getStartAt().isBefore(Instant.now())
                ?AuctionStatus.LIVE:AuctionStatus.SCHEDULED;
        Auction auction = Auction
                .builder()
                .title(createAuctionRequest.getTitle())
                .description(createAuctionRequest.getDescription())
                .startingPrice(createAuctionRequest.getStartingPrice())
                .currentHighestBid(createAuctionRequest.getStartingPrice())
                .status(initialStatus)
                .startAt(createAuctionRequest.getStartAt())
                .closeAt(createAuctionRequest.getCloseAt())
                .originalCloseAt(createAuctionRequest.getCloseAt())
                .createdBy(creator.getId())
                .build();

        Auction savedAuction = auctionRepository.save(auction);

        return AuctionDto.builder()
                .id(savedAuction.getId())
                .title(savedAuction.getTitle())
                .description(savedAuction.getDescription())
                .startingPrice(savedAuction.getStartingPrice())
                .currentHighestBid(savedAuction.getCurrentHighestBid())
                .status(savedAuction.getStatus())
                .startAt(savedAuction.getStartAt())
                .closeAt(savedAuction.getCloseAt())
                .build();

    }
}
