package com.sushant.auction.auction;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "auctions",
indexes = {
        @Index(name = "idx_auction_status_close",columnList = "status, close_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Auction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false,precision = 12,scale = 2)
    private BigDecimal startingPrice;

    @Column(precision = 12,scale = 2)
    private BigDecimal currentHighestBid;

    @Column(name = "current_highest_bidder_id")
    private Long currentHighestBidderId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuctionStatus status;

    @Column(nullable = false)
    private Instant startAt;

    @Column(name = "close_at",nullable = false)
    private Instant closeAt;

    @Column(name = "original_close_at",nullable = false,updatable = false)
    private Instant originalCloseAt;

    @Column(name = "created_by",nullable = false)
    private Long createdBy;

    @Version
    private Long version;

    @Column(name = "created_at",updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
        if(this.currentHighestBid == null) {
            this.currentHighestBid = this.startingPrice;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }
}