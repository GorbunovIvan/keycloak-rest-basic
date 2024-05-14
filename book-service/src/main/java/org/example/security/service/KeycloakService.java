package org.example.security.service;

import lombok.extern.slf4j.Slf4j;
import org.example.security.dto.UserCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class KeycloakService {

    @Value("${keycloak.url}")
    private String kcURL;

    @Value("${keycloak.realm}")
    private String kcRealm;

    @Value("${keycloak.client_id}")
    private String kcClientId;

    @Value("${keycloak.client_secret}")
    private String kcClientSecret;

    @Value("${keycloak.grant_type}")
    private String kcGrantType;

    private final RestClient restClient;

    public KeycloakService() {
        this.restClient = RestClient.builder().build();
    }

    public String getAccessToken(UserCredentials user) {

        var url = getURLForRetrievingAccessToken();

        var params = new HashMap<String, String>();
        params.put("client_id", kcClientId);
        params.put("client_secret", kcClientSecret);
        params.put("grant_type", kcGrantType);
        params.put("username", user.getUsername());
        params.put("password", user.getPassword());

        var formDataString = params.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8))
                .collect(Collectors.joining("&"));

        ResponseEntity<Map<String, String>> response = restClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(formDataString)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, resp) -> log.error("{}. {}", resp, resp.getStatusText()))
                .toEntity(new ParameterizedTypeReference<>() {});

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            log.error("Failed to get access-token by username {}, response - {}\n{}", user.getUsername(), response.getStatusCode(), response.getBody());
            return "";
        }

        var responseBody = response.getBody();

        var accessToken = responseBody.get("access_token");
        if (Objects.requireNonNullElse(accessToken, "").isEmpty()) {
            log.error("Access token is empty");
            return "";
        }

        return accessToken;
    }

    public String getPublicKey() {

        var url = getURLForRetrievingPublicKey();

        ResponseEntity<Map<String, String>> response = restClient.get()
                .uri(url)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, resp) -> log.error("{}. {}", resp, resp.getStatusText()))
                .toEntity(new ParameterizedTypeReference<>() {});

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            log.error("Failed to get public key, response - {}\n{}", response.getStatusCode(), response.getBody());
            return "";
        }

        var responseBody = response.getBody();

        var publicKey = responseBody.get("public_key");
        if (Objects.requireNonNullElse(publicKey, "").isEmpty()) {
            log.error("Public key is empty");
            return "";
        }

        return publicKey;
    }

    private String getURLForRetrievingAccessToken() {
        return String.format("http://%s/realms/%s/protocol/openid-connect/token", kcURL, kcRealm);
    }

    private String getURLForRetrievingPublicKey() {
        return String.format("http://%s/realms/%s", kcURL, kcRealm);
    }
}
