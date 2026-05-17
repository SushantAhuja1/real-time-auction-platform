package com.sushant.auction.bid;

import com.sushant.auction.bid.dto.BidRequest;
import com.sushant.auction.bid.dto.BidResponse;
import com.sushant.auction.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bids")
@RequiredArgsConstructor
public class BidController {
    private final BidService bidService;
    @PostMapping
    public ResponseEntity<BidResponse> placeBid(
            @RequestBody BidRequest bidRequest,
            @AuthenticationPrincipal User user
            ) {
            return ResponseEntity.ok(bidService.placeBid(bidRequest, user));
    }

    @GetMapping("/auction/{auctionId}")
    public ResponseEntity<List<BidResponse>> getBidsForAuction(@PathVariable Long auctionId) {
        return ResponseEntity.ok(bidService.getBidsForAuction(auctionId));
    }

    @GetMapping("/my-bids")
    public ResponseEntity<List<BidResponse>> getMyBids(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(bidService.getMyBids(user.getId()));
    }
}