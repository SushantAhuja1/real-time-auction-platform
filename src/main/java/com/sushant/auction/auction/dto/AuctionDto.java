package com.sushant.auction.auction.dto;

import com.sushant.auction.auction.AuctionStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
public class AuctionDto {
    private Long id;
    private String title;
    private String description;
    private BigDecimal startingPrice;
    private BigDecimal currentHighestBid;
    private AuctionStatus status;
    private Instant startAt;
    private Instant closeAt;
}