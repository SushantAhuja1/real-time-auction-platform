package com.sushant.auction.auth; // Updated to match your actual package!

import com.sushant.auction.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private User dummyUser;

    @BeforeEach
    void setUp() {
        // We create a REAL JwtService for this test.
        // Because your SECRET_KEY is hardcoded in the class, it works right out of the box!
        jwtService = new JwtService();

        dummyUser = new User();
        dummyUser.setUsername("testuser");
    }

    @Test
    void generateToken_ShouldReturnValidString() {
        String token = jwtService.generateToken(dummyUser);

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void extractUsername_ShouldReturnCorrectUsername() {
        // 1. Generate a token
        String token = jwtService.generateToken(dummyUser);

        // 2. Extract the username from it
        String extractedUsername = jwtService.extractUsername(token);

        // 3. Verify it matches
        assertEquals("testuser", extractedUsername);
    }

    @Test
    void isTokenValid_ShouldReturnTrueForCorrectUser() {
        String token = jwtService.generateToken(dummyUser);

        assertTrue(jwtService.isTokenValid(token, dummyUser));
    }
}