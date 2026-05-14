package com.sushant.auction.auction.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
public class CreateAuctionRequest {
    private String title;
    private String description;
    private BigDecimal startingPrice;
    private Instant startAt;
    private Instant closeAt;
}