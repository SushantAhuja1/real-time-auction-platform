package com.sushant.auction.bid;

import com.sushant.auction.auction.Auction;
import com.sushant.auction.auction.AuctionRepository;
import com.sushant.auction.auction.AuctionStatus;
import com.sushant.auction.bid.dto.BidRequest;
import com.sushant.auction.exception.AuctionClosedException;
import com.sushant.auction.user.User;
import com.sushant.auction.websocket.BidBroadcaster;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class) // Tells JUnit to enable Mockito
public class BidServiceTest {

    // @Mock creates "fake" versions of our dependencies so we don't need a real database
    @Mock
    private BidRepository bidRepository;
    @Mock
    private AuctionRepository auctionRepository;
    @Mock
    private BidBroadcaster bidBroadcaster;
    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    // @InjectMocks creates a REAL BidService but injects our "fake" mocks into it
    @InjectMocks
    private BidService bidService;

    @Test
    void placeBid_ShouldThrowException_WhenAuctionIsClosed() {
        // --- 1. ARRANGE (Set up the fake data) ---

        // Create a fake user
        User user = new User();
        user.setId(1L);

        // Create a fake bid request
        BidRequest request = new BidRequest();
        request.setAuctionId(100L);
        request.setAmount(new BigDecimal("500.00"));

        // Create a fake CLOSED auction
        Auction closedAuction = new Auction();
        closedAuction.setId(100L);
        closedAuction.setStatus(AuctionStatus.CLOSED); // Notice it is CLOSED

        // Tell our fake repository: "When someone asks for auction ID 100, return this closed auction"
        when(auctionRepository.findById(100L)).thenReturn(Optional.of(closedAuction));
        // ⭐ ADD THESE TWO LINES:
        // Teach the fake bidRepository to return a dummy Bid instead of null when saving the rejected bid
        Bid dummyBid = new Bid();
        dummyBid.setId(999L);
        when(bidRepository.save(any(Bid.class))).thenReturn(dummyBid);

        // --- 2. ACT & 3. ASSERT (Run the code and verify the result) ---

        // We assert that calling placeBid will throw an AuctionClosedException
        AuctionClosedException exception = assertThrows(AuctionClosedException.class, () -> {
            bidService.placeBid(request, user);
        });

        // Verify the exception message is exactly what we expect
        assertEquals("Auction is closed", exception.getMessage());
    }
}