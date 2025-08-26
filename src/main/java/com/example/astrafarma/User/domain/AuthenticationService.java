package com.example.astrafarma.User.domain;

import com.example.astrafarma.Mail.events.UserRegisteredEvent;
import com.example.astrafarma.User.dto.UserRequestDto;
import com.example.astrafarma.User.dto.SigninRequest;
import com.example.astrafarma.User.repository.UserRepository;
import com.example.astrafarma.exception.UserAlreadyExistsException;
import com.example.astrafarma.exception.UserNotFoundException;
import com.example.astrafarma.security.JwtService;
import com.example.astrafarma.security.dto.JwtAuthenticationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthenticationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    public JwtAuthenticationResponse signup(UserRequestDto dto) {
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("Ya existe un usuario con ese email.");
        }

        User user = new User();
        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setGender(dto.getGender());
        user.setBirthday(dto.getBirthday());
        user.setUserRole(UserRole.USER);
        user.setVerified(false);
        user.setVerificationToken(UUID.randomUUID().toString());

        userRepository.save(user);

        String jwt = jwtService.generateToken(user);
        eventPublisher.publishEvent(new UserRegisteredEvent(this, user));

        JwtAuthenticationResponse response = new JwtAuthenticationResponse();
        response.setToken(jwt);
        return response;
    }

    public JwtAuthenticationResponse login(SigninRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));

        String jwt = jwtService.generateToken(user);

        JwtAuthenticationResponse response = new JwtAuthenticationResponse();
        response.setToken(jwt);
        return response;
    }
}