package com.sushant.auction.auth;

import com.sushant.auction.auth.dto.AuthResponse;
import com.sushant.auction.auth.dto.LoginRequest;
import com.sushant.auction.auth.dto.RegisterRequest;
import com.sushant.auction.user.Role;
import com.sushant.auction.user.User;
import com.sushant.auction.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService  jwtService;
    private final AuthenticationManager  authenticationManager;

    public AuthResponse register(RegisterRequest  registerRequest) {
        //create-user-object
        User user = User
                .builder()
                .username(registerRequest.getUsername())
                .passwordHash(passwordEncoder.encode(registerRequest.getPassword()))
                .email(registerRequest.getEmail())
                .role(Role.BIDDER)
                .build();
        userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        return AuthResponse
                .builder()
                .token(jwtToken)
                .build();
    }

    public AuthResponse authenticate(LoginRequest  loginRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );
        var user = userRepository.findByUsername(loginRequest.getUsername()).orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        return AuthResponse
                .builder()
                .token(jwtToken)
                .build();
    }
}