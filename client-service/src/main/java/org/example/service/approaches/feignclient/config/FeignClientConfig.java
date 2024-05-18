package org.example.service.approaches.feignclient.config;

import feign.RequestInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.service.approaches.feignclient.KeycloakForBookServiceFeignClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "org.example.service.approaches.feignclient.feigns")
@RequiredArgsConstructor
@Slf4j
public class FeignClientConfig {

    private final KeycloakForBookServiceFeignClient keycloakForBookServiceFeignClient;

    @Bean
    public RequestInterceptor requestInterceptor() {
        return keycloakForBookServiceFeignClient::addSecurityInfoToRequest;
    }
}
