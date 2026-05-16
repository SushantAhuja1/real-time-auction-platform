package com.sushant.auction.auction;

import com.sushant.auction.auction.dto.AuctionDto;
import com.sushant.auction.auction.dto.AuctionLeaderboardDto;
import com.sushant.auction.auction.dto.CreateAuctionRequest;
import com.sushant.auction.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auctions")
@RequiredArgsConstructor
public class AuctionController {

    private final AuctionService auctionService;
    private final RedisTemplate<String, Object> redisTemplate;

    @PostMapping
    public ResponseEntity<AuctionDto> createAuction(
            @RequestBody CreateAuctionRequest request,
            @AuthenticationPrincipal User user) {

        return ResponseEntity.ok(auctionService.createAuction(request, user));
    }

    @GetMapping("/leaderboard")
    public ResponseEntity<List<AuctionLeaderboardDto>> getLeaderboard() {
        // 1. Ask Redis for the Top 10 Auction IDs (sorted highest to lowest)
        // FIX: Change Set<String> to Set<Object>
        java.util.Set<Object> topAuctionIds = redisTemplate.opsForZSet().reverseRange("auction_leaderboard", 0, 9);

        if (topAuctionIds == null || topAuctionIds.isEmpty()) {
            return ResponseEntity.ok(java.util.List.of());
        }

        // 2. Fetch the actual details (Title, Price) for those IDs
        java.util.List<AuctionLeaderboardDto> leaderboard = new java.util.ArrayList<>();

        // FIX: Iterate over Object instead of String
        for (Object idObj : topAuctionIds) {
            String id = idObj.toString(); // Convert the Object back into a String

            AuctionLeaderboardDto dto = (AuctionLeaderboardDto) redisTemplate.opsForHash().get("auction_details", id);
            if (dto != null) {
                leaderboard.add(dto);
            }
        }

        return ResponseEntity.ok(leaderboard);
    }
}