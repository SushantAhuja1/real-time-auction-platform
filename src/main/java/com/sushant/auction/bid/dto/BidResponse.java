package com.sushant.auction.bid.dto;

import com.sushant.auction.bid.BidStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
public class BidResponse {
    private Long id;
    private Long auctionId;
    private Long bidderId;
    private BigDecimal amount;
    private BidStatus  status;
    private String rejectionReason;
    private Instant createdAt;
}