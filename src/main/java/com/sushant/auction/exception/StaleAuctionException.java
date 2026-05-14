package com.sushant.auction.exception;

public class StaleAuctionException extends RuntimeException {
    public StaleAuctionException(String message) {
        super(message);
    }
}