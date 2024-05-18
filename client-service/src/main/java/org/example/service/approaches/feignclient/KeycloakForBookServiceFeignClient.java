package org.example.service.approaches.feignclient;

import feign.RequestTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.service.approaches.feignclient.dto.UserCredentials;
import org.example.service.approaches.feignclient.feigns.auth.BookServiceAuthFeignClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeycloakForBookServiceFeignClient {

    private final BookServiceAuthFeignClient bookServiceAuthFeignClient;

    @Value("${book-service.username}")
    private String bookServiceUsername;

    @Value("${book-service.password}")
    private String bookServicePassword;

    @Value("${book-service.checking-validity-of-access-token.scheduled.enabled}")
    private boolean checkingAccessTokenExpirationEnabled;

    private String accessToken = "";

    public void updateAccessToken() {
        this.accessToken = getNewAccessToken();
    }

    private String getNewAccessToken() {

        var user = getUserCredentials();
        ResponseEntity<String> response = bookServiceAuthFeignClient.login(user);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            log.error("Failed to get access-token by username {}, response - {}\n{}", bookServiceUsername, response.getStatusCode(), response.getBody());
            return "";
        }

        String token = response.getBody();
        if (Objects.requireNonNullElse(token, "").isEmpty()) {
            log.error("Access token is empty");
            return "";
        }

        return token;
    }

    @Scheduled(fixedRateString = "${book-service.checking-validity-of-access-token.scheduled.fixedRate.milliseconds}")
    public void checkAccessTokenExpirationOrGenerateNew() {

        if (!checkingAccessTokenExpirationEnabled) {
            return;
        }

        if (accessToken.isEmpty()) {
            updateAccessToken();
            return;
        }

        var user = getUserCredentials();
        ResponseEntity<String> response = bookServiceAuthFeignClient.checkAccessTokenExpirationOrGenerateNew(this.accessToken, user);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            log.error("Failed to get check validity of access-token, response - {}\n{}", response.getStatusCode(), response.getBody());
            return;
        }

        var token = response.getBody();
        if (Objects.requireNonNullElse(token, "").isEmpty()) {
            log.error("Access-token is empty");
            this.accessToken = "";
            return;
        }

        if (token.equals(this.accessToken)) {
            return;
        }

        log.info("Access-token was regenerated for user '{}'", user.getUsername());
        this.accessToken = token;
    }

    private UserCredentials getUserCredentials() {
        var user = new UserCredentials();
        user.setUsername(bookServiceUsername);
        user.setPassword(bookServicePassword);
        return user;
    }

    public void addSecurityInfoToRequest(RequestTemplate requestTemplate) {
        if (!requestTemplate.feignTarget().name().equals("book-service")) {
            return;
        }
        if (this.accessToken.isEmpty()) {
            updateAccessToken();
        }
        requestTemplate.header("Authorization", "Bearer " + this.accessToken);
    }
}
