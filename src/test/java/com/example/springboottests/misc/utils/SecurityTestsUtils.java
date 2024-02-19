package com.example.springboottests.misc.utils;

import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.jwk.RsaJwkGenerator;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;

import java.util.UUID;

/**
 * Utility class for security-related operations in tests.
 *
 * @author Georgii Lvov
 */
public final class SecurityTestsUtils {
    public static final RsaJsonWebKey RSA_JSON_WEB_KEY;

    static {
        try {
            RSA_JSON_WEB_KEY = RsaJwkGenerator.generateJwk(2048);

            RSA_JSON_WEB_KEY.setKeyId("k1");
            RSA_JSON_WEB_KEY.setAlgorithm(AlgorithmIdentifiers.RSA_USING_SHA256);
            RSA_JSON_WEB_KEY.setUse("sig");
        } catch (Exception e) {
            throw new SecurityTestsUtilsException(e);
        }
    }

    private SecurityTestsUtils() {
    }

    /**
     * Generates a JSON Web Token (JWT) with the specified iss claim and custom operation claim.
     *
     * @param issuerUri     The iss claim value.
     * @param operation     The operation claim value.
     */
    public static String generateJwt(String issuerUri, String operation) {
        try {
            JwtClaims claims = new JwtClaims();

            claims.setJwtId(UUID.randomUUID().toString());
            claims.setExpirationTimeMinutesInTheFuture(10);
            claims.setIssuedAtToNow();
            claims.setIssuer(issuerUri);
            claims.setClaim("typ", "Bearer");
            claims.setClaim("clientId", "dummy_clientId");
            claims.setClaim("sub", "user_name");
            claims.setClaim("operation", operation);

            JsonWebSignature jws = new JsonWebSignature();

            jws.setKey(RSA_JSON_WEB_KEY.getPrivateKey());
            jws.setKeyIdHeaderValue(RSA_JSON_WEB_KEY.getKeyId());
            jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);
            jws.setHeader("typ", "JWT");

            jws.setPayload(claims.toJson());

            return jws.getCompactSerialization();
        } catch (Exception e) {
            throw new SecurityTestsUtilsException(e);
        }
    }

    private static class SecurityTestsUtilsException extends RuntimeException {
        private SecurityTestsUtilsException(Throwable cause) {
            super(cause);
        }
    }
}


