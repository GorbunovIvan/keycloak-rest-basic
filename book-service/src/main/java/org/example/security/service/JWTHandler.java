package org.example.security.service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.text.ParseException;
import java.util.Base64;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class JWTHandler {

    private final KeycloakService keycloakService;

    public boolean verifyAccessToken(String accessToken) {

        if (accessToken.isEmpty()) {
            log.error("Unauthorized - authorization header missing");
            return false;
        }

        try {
            JWSVerifier verifier = getJWTVerifier();
            return verifyJWT(accessToken, verifier);
        } catch (ParseException | JOSEException | InvalidKeySpecException | NoSuchAlgorithmException e) {
            log.error("Failed to parse verify access-token. {}", e.getMessage());
            return false;
        }
    }

    public Long accessTokenTimeBeforeExpiration(String accessToken) {

        if (accessToken.isEmpty()) {
            return 0L;
        }

        try {
            SignedJWT signedJWT = SignedJWT.parse(accessToken);
            return millisBeforeExpiration(signedJWT);
        } catch (ParseException e) {
            log.error(e.getMessage());
            return 0L;
        }
    }

    private JWSVerifier getJWTVerifier() throws NoSuchAlgorithmException, InvalidKeySpecException {

        // Getting public-key
        var publicKey = keycloakService.getPublicKey();

        // Parsing public-key
        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKey);
        RSAPublicKey publicKeyRSA = (RSAPublicKey) KeyFactory.getInstance("RSA")
                .generatePublic(new X509EncodedKeySpec(publicKeyBytes));

        // Initiating JWT verifier
        return new RSASSAVerifier(publicKeyRSA);
    }

    private boolean verifyJWT(String accessToken, JWSVerifier verifier) throws JOSEException, ParseException {

        // Parsing access-token
        SignedJWT signedJWT = SignedJWT.parse(accessToken);

        // Verify the token signature
        if (!signedJWT.verify(verifier)) {
            log.error("Failed to verify access-token");
            return false;
        }

        // Verifying the expiration date
        var millisBeforeExpiration = millisBeforeExpiration(signedJWT);
        return millisBeforeExpiration > 0;
    }

    private Long millisBeforeExpiration(SignedJWT signedJWT) throws ParseException {
        var claims = signedJWT.getJWTClaimsSet();
        Date expirationTime = claims.getExpirationTime();
        Date now = new Date();
        if (!expirationTime.before(now)) {
            return expirationTime.getTime() - now.getTime();
        }
        return 0L;
    }
}
