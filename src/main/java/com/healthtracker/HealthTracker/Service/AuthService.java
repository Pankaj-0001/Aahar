package com.healthtracker.HealthTracker.Service;

import com.healthtracker.HealthTracker.Config.JWTutils;
import com.healthtracker.HealthTracker.DTO.CodeDTOs;
import com.healthtracker.HealthTracker.Entity.User;
import com.healthtracker.HealthTracker.Repository.UserRepo;
import com.healthtracker.HealthTracker.exception.BadRequestException;
import com.healthtracker.HealthTracker.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepo userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JWTutils jwtUtils;

    /**
     * Authenticate user and return JWT token
     */
    public CodeDTOs.AuthResponse login(CodeDTOs.LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateToken(authentication.getName());

        User user = userRepository.findByemail(loginRequest.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return new CodeDTOs.AuthResponse(jwt, user.getId(), user.getName(), user.getEmail());
    }

    /**
     * Register new user
     */
    public CodeDTOs.MessageResponse register(CodeDTOs.RegisterRequest registerRequest) {
        // Check if email already exists
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new BadRequestException("Error: Email is already in use!");
        }

        // Create new user
        User user = new User();
        user.setName(registerRequest.getName());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setAge(registerRequest.getAge());
        user.setGender(registerRequest.getGender());
        user.setHeight(registerRequest.getHeight());
        user.setWeight(registerRequest.getWeight());
        user.setActivityLevel(registerRequest.getActivityLevel());
        user.setGoal(registerRequest.getGoal());
        user.setCreatedAt(LocalDateTime.now());

        userRepository.save(user);

        return new CodeDTOs.MessageResponse("User registered successfully!");
    }
}
