package com.sushant.auction.bid;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name="bids",
indexes = {
        @Index(name = "idx_bid_auction_created",columnList = "auction_id, created_at DESC"),
        @Index(name = "idx_bid_bidder",columnList = "bidder_id, created_at DESC")
})
@Setter @Getter @NoArgsConstructor @AllArgsConstructor @Builder
public class Bid {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "auction_id",nullable = false)
    private Long auctionId;

    @Column(name = "bidder_id",nullable = false)
    private Long bidderId;

    @Column(nullable = false,precision = 12,scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BidStatus status;

    @Column(name = "rejection_reason",length = 100)
    private String rejectionReason;

    @Column(name = "created_at",nullable = false,updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }
}