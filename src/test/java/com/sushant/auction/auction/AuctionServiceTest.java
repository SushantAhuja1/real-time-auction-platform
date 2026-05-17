package com.sushant.auction.auction;

import com.sushant.auction.auction.dto.AuctionDto;
import com.sushant.auction.auction.dto.CreateAuctionRequest;
import com.sushant.auction.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuctionServiceTest {

    @Mock
    private AuctionRepository auctionRepository;

    @InjectMocks
    private AuctionService auctionService;

    @Test
    void createAuction_ShouldReturnAuctionDto_WhenDataIsValid() {
        // --- 1. ARRANGE ---
        User user = new User();
        user.setId(1L);

        CreateAuctionRequest request = new CreateAuctionRequest();
        request.setTitle("Test Auction");
        request.setStartingPrice(new BigDecimal("100.00"));
        request.setStartAt(Instant.now().plusSeconds(60));
        request.setCloseAt(Instant.now().plusSeconds(3600));

        // Fake the saved auction that the database would return
        Auction savedAuction = new Auction();
        savedAuction.setId(10L);
        savedAuction.setTitle("Test Auction");
        savedAuction.setStartingPrice(new BigDecimal("100.00"));
        savedAuction.setStatus(AuctionStatus.SCHEDULED);

        // Instruct our fake repository to return 'savedAuction' whenever save() is called
        when(auctionRepository.save(any(Auction.class))).thenReturn(savedAuction);

        // --- 2. ACT ---
        AuctionDto result = auctionService.createAuction(request, user);

        // --- 3. ASSERT ---
        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals("Test Auction", result.getTitle());
        assertEquals(AuctionStatus.SCHEDULED, result.getStatus());
    }
}