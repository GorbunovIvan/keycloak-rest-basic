package org.example.service.approaches.feignclient.feigns.auth;

import lombok.extern.slf4j.Slf4j;
import org.example.service.approaches.feignclient.dto.UserCredentials;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BookServiceAuthFeignClientFallback implements BookServiceAuthFeignClient {

    private final String errorMessage = "Remote books-service-auth is not available.";

    @Override
    public ResponseEntity<String> login(UserCredentials user) {
        log.error(errorMessage);
        return new ResponseEntity<>("", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> checkAccessTokenExpirationOrGenerateNew(String accessToken, UserCredentials user) {
        log.error(errorMessage);
        return new ResponseEntity<>("", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
