package com.aahar.Aahar.Service;

import com.aahar.Aahar.Config.JWTutils;
import com.aahar.Aahar.DTO.CodeDTOs;
import com.aahar.Aahar.Entity.User;
import com.aahar.Aahar.Repository.UserRepo;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GoogleAuthService {

    private final UserRepo userRepo;
    private final JWTutils jwtUtils;

    @Value("${google.client-id}")
    private String googleClientId;

    public CodeDTOs.AuthResponse handleGoogleLogin(String idToken) throws Exception {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList(googleClientId))
                .build();

        GoogleIdToken googleIdToken = verifier.verify(idToken);
        if (googleIdToken == null) {
            throw new RuntimeException("Invalid Google token");
        }

        GoogleIdToken.Payload payload = googleIdToken.getPayload();
        String email = payload.getEmail();
        String name = (String) payload.get("name");
        String googleId = payload.getSubject();

        Optional<User> existing = userRepo.findByemail(email);

        User user;
        if (existing.isPresent()) {
            user = existing.get();
            // Link googleId if they previously registered with email/password
            if (user.getGoogleId() == null) {
                user.setGoogleId(googleId);
                userRepo.save(user);
            }
        } else {
            // New user — create partial account
            user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setPassword("");   // no password for OAuth users
            user.setGoogleId(googleId);
            user.setProfileComplete(false);
            user.setCreatedAt(LocalDateTime.now());
            userRepo.save(user);
        }

        String jwt = jwtUtils.generateToken(email);
        return new CodeDTOs.AuthResponse(jwt, user.getId(), user.getName(), user.getEmail(), user.isProfileComplete());
    }
}