package com.example.astrafarma.User.controller;

import com.example.astrafarma.User.dto.SigninRequest;
import com.example.astrafarma.User.dto.UserRequestDto;
import com.example.astrafarma.User.domain.AuthenticationService;
import com.example.astrafarma.security.dto.JwtAuthenticationResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/signup")
    public ResponseEntity<JwtAuthenticationResponse> createUser(@Valid @RequestBody UserRequestDto dto) {
        JwtAuthenticationResponse response = authenticationService.signup(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtAuthenticationResponse> login(@Valid @RequestBody SigninRequest request) {
        JwtAuthenticationResponse response = authenticationService.login(request);
        return ResponseEntity.ok(response);
    }
}