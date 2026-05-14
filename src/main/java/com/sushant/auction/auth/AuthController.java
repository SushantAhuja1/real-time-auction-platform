package com.sushant.auction.auth;

import com.sushant.auction.auth.dto.AuthResponse;
import com.sushant.auction.auth.dto.LoginRequest;
import com.sushant.auction.auth.dto.RegisterRequest;
import com.sushant.auction.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest  registerRequest) {
        return ResponseEntity.ok(authService.register(registerRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.authenticate(loginRequest));
    }

    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(@org.springframework.security.core.annotation.AuthenticationPrincipal User user) {
        return ResponseEntity.ok(user);
    }
}