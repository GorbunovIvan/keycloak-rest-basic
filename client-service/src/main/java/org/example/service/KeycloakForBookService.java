package org.example.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
public class KeycloakForBookService {

    @Value("${book-service.url}")
    private String bookServiceURL;

    @Value("${book-service.username}")
    private String bookServiceUsername;

    @Value("${book-service.password}")
    private String bookServicePassword;

    @Value("${book-service.checking-validity-of-access-token.scheduled.enabled}")
    private boolean checkingAccessTokenExpirationEnabled;

    private final RestClient restClient;

    private String accessToken = "";

    public KeycloakForBookService() {
        this.restClient = RestClient.builder().build();
    }

    public void updateAccessToken() {
        this.accessToken = getAccessToken();
    }

    private String getAccessToken() {

        var url = getURLForRetrievingAccessToken();
        var params = getUserCredentialsAsMap();

        ResponseEntity<String> response = restClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .body(params)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, resp) -> log.error(resp.toString()))
                .toEntity(String.class);

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

        var url = getURLForCheckingAccessTokenExpirationOrGeneratingNew();
        var params = getUserCredentialsAsMap();

        ResponseEntity<String> response = restClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .body(params)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, resp) -> log.error(resp.toString()))
                .toEntity(String.class);

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

        log.info("Access-token was regenerated for user '{}'", params.get("username"));
        this.accessToken = token;
    }

    private String getURLForRetrievingAccessToken() {
        return String.format("http://%s/auth/login", bookServiceURL);
    }

    private String getURLForCheckingAccessTokenExpirationOrGeneratingNew() {
        return String.format("http://%s/auth/check-access-token/%s", bookServiceURL, accessToken);
    }

    private Map<String, String> getUserCredentialsAsMap() {
        var params = new HashMap<String, String>();
        params.put("username", bookServiceUsername);
        params.put("password", bookServicePassword);
        return params;
    }

    public void addSecurityInfoToHttpHeaders(HttpHeaders httpHeaders) {
        if (this.accessToken.isEmpty()) {
            updateAccessToken();
        }
        httpHeaders.add("Authorization", "Bearer " + this.accessToken);
    }
}
