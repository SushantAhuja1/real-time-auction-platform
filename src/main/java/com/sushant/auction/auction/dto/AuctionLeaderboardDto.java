package com.sushant.auction.auction.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuctionLeaderboardDto {
    private Long auctionId;
    private String title;
    private BigDecimal currentHighestBid;
}