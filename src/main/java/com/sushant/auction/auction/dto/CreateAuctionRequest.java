package com.sushant.auction.auction.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
public class CreateAuctionRequest {
    @NotBlank(message = "Title cannot be blank")
    private String title;
    private String description;
    @NotNull(message = "Starting price is required")
    @Positive(message = "Starting price must be greater than zero")
    private BigDecimal startingPrice;
    @NotNull(message = "Start time is required")
    private Instant startAt;
    @NotNull(message = "Close time is required")
    @Future(message = "Close time must be in the future")
    private Instant closeAt;
}