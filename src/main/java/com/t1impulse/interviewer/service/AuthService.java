package com.t1impulse.interviewer.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.t1impulse.interviewer.dto.AuthRequest;
import com.t1impulse.interviewer.entity.Role;
import com.t1impulse.interviewer.entity.User;
import com.t1impulse.interviewer.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authManager;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepo;
    private final JwtService jwtService;

    @Transactional
    private String register(AuthRequest request) {
        if (userRepo.existsByUsername(request.username())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username is already taken");
        }

        User u = new User();
        u.setUsername(request.username());
        u.setPassword(passwordEncoder.encode(request.password()));
        u.setRole(Role.USER);
        userRepo.save(u);

        var userDetails = org.springframework.security.core.userdetails.User
                .withUsername(u.getUsername())
                .password(u.getPassword())
                .roles(u.getRole().toString())
                .build();

        String access = jwtService.generateAccessToken(userDetails);

        return access;
    }

    public String login(AuthRequest request) {
        try {
            Authentication auth = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username(), request.password()));
            UserDetails user = (UserDetails) auth.getPrincipal();

            String access = jwtService.generateAccessToken(user);

            return access;
        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        }
    }
}