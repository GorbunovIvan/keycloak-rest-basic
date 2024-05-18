package org.example.service.approaches.feignclient.feigns.auth;

import org.example.service.approaches.feignclient.dto.UserCredentials;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Primary;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "book-service-auth",
        url = "http://${book-service.url}/auth",
        fallback = BookServiceAuthFeignClientFallback .class
)
@Primary
public interface BookServiceAuthFeignClient {

    @PostMapping("/login")
    ResponseEntity<String> login(@RequestBody UserCredentials user);

    @PostMapping("/check-access-token/{accessToken}")
    ResponseEntity<String> checkAccessTokenExpirationOrGenerateNew(@PathVariable String accessToken,
                                                                          @RequestBody UserCredentials user);
}
