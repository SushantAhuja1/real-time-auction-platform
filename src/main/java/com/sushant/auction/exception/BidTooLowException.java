package com.sushant.auction.exception;

public class BidTooLowException extends RuntimeException {
    public BidTooLowException(String message) {
        super(message);
    }
}