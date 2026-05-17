package com.sushant.auction.bid.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BidRequest {
    @NotNull(message = "Auction ID cannot be null")
    private Long auctionId;
    @NotNull(message = "Bid amount is required")
    @Positive(message = "Bid amount must be greater than zero")
    private BigDecimal amount;
}