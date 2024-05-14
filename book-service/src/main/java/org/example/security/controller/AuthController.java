package org.example.security.controller;

import lombok.RequiredArgsConstructor;
import org.example.security.dto.UserCredentials;
import org.example.security.service.JWTHandler;
import org.example.security.service.KeycloakService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final KeycloakService keycloakService;
    private final JWTHandler jwtHandler;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserCredentials user) {
        var accessToken = keycloakService.getAccessToken(user);
        if (accessToken.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(accessToken);
    }

    @PostMapping("/check-access-token/{accessToken}")
    public ResponseEntity<String> checkAccessTokenExpirationOrGenerateNew(@PathVariable String accessToken,
                                                                          @RequestBody UserCredentials user) {
        var millisBeforeExpiration = jwtHandler.accessTokenTimeBeforeExpiration(accessToken);

        // If there are less than 5 seconds left, then we regenerate accessToken
        if (millisBeforeExpiration < 5_000) {
            return login(user);
        }
        return ResponseEntity.ok(accessToken);
    }
}
