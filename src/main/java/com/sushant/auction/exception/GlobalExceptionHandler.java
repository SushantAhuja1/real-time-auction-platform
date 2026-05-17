package com.sushant.auction.exception;

import com.sushant.auction.common.ApiError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(StaleAuctionException.class)
    public ResponseEntity<ApiError> handleStale(StaleAuctionException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiError("STALE_AUCTION", ex.getMessage()));
    }

    @ExceptionHandler(BidTooLowException.class)
    public ResponseEntity<ApiError> handleBidTooLow(BidTooLowException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiError("BID_TOO_LOW", ex.getMessage()));
    }

    @ExceptionHandler(AuctionClosedException.class)
    public ResponseEntity<ApiError> handleAuctionClosed(AuctionClosedException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiError("AUCTION_CLOSED", ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGenric(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiError("INTERNAL_ERROR", "Internal Server Error"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
}