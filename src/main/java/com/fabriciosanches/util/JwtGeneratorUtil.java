package com.fabriciosanches.util;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Utility class for generating JWT tokens for testing and local development.
 *
 * USAGE EXAMPLE (Development):
 * java -cp target/order-service-1.0.0.jar com.fabriciosanches.util.JwtGeneratorUtil \
 *   --secret "change-me-in-production-32chars!" \
 *   --scopes "orders:read,orders:write,payments:read,payments:write"
 */
public class JwtGeneratorUtil {

    private static final String DEFAULT_SECRET = "change-me-in-production-32chars!";
    private static final String DEFAULT_ISSUER = "order-service";
    private static final String DEFAULT_SUBJECT = "local-user";
    private static final long DEFAULT_EXPIRATION_SECONDS = 3600; // 1 hour

    public static void main(String[] args) {
        String secret = DEFAULT_SECRET;
        String scopes = "orders:read,orders:write,payments:read,payments:write";
        String issuer = DEFAULT_ISSUER;
        String subject = DEFAULT_SUBJECT;
        long expirationSeconds = DEFAULT_EXPIRATION_SECONDS;

        // Simple argument parsing
        for (int i = 0; i < args.length; i++) {
            if ("--secret".equals(args[i]) && i + 1 < args.length) {
                secret = args[++i];
            } else if ("--scopes".equals(args[i]) && i + 1 < args.length) {
                scopes = args[++i];
            } else if ("--issuer".equals(args[i]) && i + 1 < args.length) {
                issuer = args[++i];
            } else if ("--subject".equals(args[i]) && i + 1 < args.length) {
                subject = args[++i];
            } else if ("--expiration".equals(args[i]) && i + 1 < args.length) {
                expirationSeconds = Long.parseLong(args[++i]);
            } else if ("--help".equals(args[i]) || "-h".equals(args[i])) {
                printHelp();
                return;
            }
        }

        try {
            String token = generateToken(secret, issuer, subject, scopes.split(","), expirationSeconds);
            System.out.println("\n✓ JWT Token gerado com sucesso!\n");
            System.out.println("Token:");
            System.out.println(token);
            System.out.println("\nCurl Example:");
            System.out.println("curl -X GET 'http://localhost:8080/api/v1/orders?customerId=1' \\");
            System.out.println("  -H 'Authorization: Bearer " + token + "'");
            System.out.println("\nSwagger: Copie o token acima (sem 'Bearer ') no campo de autenticação do Swagger.");
        } catch (Exception e) {
            System.err.println("✗ Erro ao gerar JWT: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static String generateToken(String secret, String issuer, String subject,
                                       String[] scopes, long expirationSeconds) throws Exception {
        if (secret == null || secret.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalArgumentException("Secret must be at least 32 bytes for HS256");
        }

        SecretKeySpec key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        JWSSigner signer = new MACSigner(key);

        long now = System.currentTimeMillis();
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject(subject)
                .issuer(issuer)
                .issueTime(new Date(now))
                .expirationTime(new Date(now + (expirationSeconds * 1000)))
                .claim("scope", String.join(" ", scopes))
                .build();

        SignedJWT jwt = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claims);
        jwt.sign(signer);
        return jwt.serialize();
    }

    private static void printHelp() {
        System.out.println("""
                JWT Token Generator for Order Service
                
                Usage:
                  java com.fabriciosanches.util.JwtGeneratorUtil [OPTIONS]
                
                Options:
                  --secret <value>           JWT secret (min 32 bytes) [default: change-me-in-production-32chars!]
                  --scopes <value>           Comma-separated scopes [default: orders:read,orders:write,payments:read,payments:write]
                  --issuer <value>           Token issuer [default: order-service]
                  --subject <value>          Token subject [default: local-user]
                  --expiration <seconds>     Token expiration in seconds [default: 3600]
                  --help, -h                 Show this help message
                
                Examples:
                  # Generate token with default values
                  java com.fabriciosanches.util.JwtGeneratorUtil
                  
                  # Generate token with custom scopes
                  java com.fabriciosanches.util.JwtGeneratorUtil --scopes "orders:read"
                  
                  # Generate token with custom secret
                  java com.fabriciosanches.util.JwtGeneratorUtil --secret "my-custom-secret-32chars-minimum!!"
                """);
    }
}

