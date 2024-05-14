package org.example.security.interceptors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.security.service.JWTHandler;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
@Slf4j
public class SecurityInterceptor implements HandlerInterceptor {

    private final JWTHandler jwtHandler;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             @Nullable HttpServletResponse response,
                             @Nullable Object handler) {

        log.info("Request intercepted: {}", request.getRequestURL());

        var accessToken = retrieveAccessTokenFromRequest(request);

        var accessTokenVerifyingResult = jwtHandler.verifyAccessToken(accessToken);
        if (!accessTokenVerifyingResult) {
            if (response != null) {
                response.setStatus(401);
            }
            return false;
        }

        return true;
    }

    private String retrieveAccessTokenFromRequest(HttpServletRequest request) {

        var authorizationBearer = request.getHeader("Authorization");
        if (authorizationBearer == null || authorizationBearer.isEmpty()) {
            return "";
        }

        if (authorizationBearer.startsWith("Bearer ")) {
            authorizationBearer = authorizationBearer.substring(7);
        }

        return authorizationBearer;
    }
}
