package com.sushant.auction.bid;

import com.sushant.auction.auction.Auction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BidRepository extends JpaRepository<Bid,Long> {
    //fetch-all-bids-for-a-specific-auction-newest-first
    List<Bid> findByAuctionIdOrderByCreatedAtDesc(Long auctionId);
    //fetch-all-bids-made-by-a-specific-bidder-newest-first
    List<Bid> findByBidderIdOrderByCreatedAtDesc(Long bidderId);
}