package com.sushant.auction.bid.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BidRequest {
    private Long auctionId;
    private BigDecimal amount;
}