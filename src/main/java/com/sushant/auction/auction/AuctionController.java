package com.sushant.auction.auction;

import com.sushant.auction.auction.dto.AuctionDto;
import com.sushant.auction.auction.dto.CreateAuctionRequest;
import com.sushant.auction.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auctions")
@RequiredArgsConstructor
public class AuctionController {

    private final AuctionService auctionService;

    @PostMapping
    public ResponseEntity<AuctionDto> createAuction(
            @RequestBody CreateAuctionRequest request,
            @AuthenticationPrincipal User user) {

        return ResponseEntity.ok(auctionService.createAuction(request, user));
    }
}