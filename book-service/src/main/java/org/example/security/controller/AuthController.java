package org.example.security.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.security.dto.UserCredentials;
import org.example.security.service.JWTHandler;
import org.example.security.service.KeycloakService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final KeycloakService keycloakService;
    private final JWTHandler jwtHandler;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserCredentials user) {
        log.info("Attempt to log in (generate access-token) for username '{}'", user.getUsername());
        var accessToken = keycloakService.getAccessToken(user);
        if (accessToken.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(accessToken);
    }

    @PostMapping("/check-access-token/{accessToken}")
    public ResponseEntity<String> checkAccessTokenExpirationOrGenerateNew(@PathVariable String accessToken,
                                                                          @RequestBody UserCredentials user) {

        log.info("Checking access-token expiration for user '{}'", user.getUsername());
        var millisBeforeExpiration = jwtHandler.accessTokenTimeBeforeExpiration(accessToken);

        // If there are less than 65 seconds left, then we regenerate accessToken.
        // Therefore, it is recommended that client-services query this endpoint at least once per minute.
        if (millisBeforeExpiration <= 65_000) {
            log.info("Provided access-token for user '{}' will expire soon", user.getUsername());
            return login(user);
        }

        log.info("Provided access-token for user '{}' is valid", user.getUsername());

        return ResponseEntity.ok(accessToken);
    }
}
