package com.sushant.auction.auction;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface AuctionRepository extends JpaRepository<Auction,Long> {
    List<Auction> findByStatusAndCloseAtBefore(AuctionStatus status, Instant time);
    List<Auction> findByStatusAndStartAtBefore(AuctionStatus status, Instant time);
}