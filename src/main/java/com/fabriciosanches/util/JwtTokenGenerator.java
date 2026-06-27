package com.fabriciosanches.util;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;

/**
 * Utilitário para gerar JWTs válidos para o Order Service.
 *
 * Este gerador cria tokens JWT assinados com HMAC-SHA256, incluindo os escopos
 * necessários para autenticação e autorização na API:
 * - orders:write
 * - orders:read
 * - payments:write
 * - payments:read
 */
public class JwtTokenGenerator {

    private static final String DEFAULT_SECRET = "change-me-in-production-32chars!"; // 32 bytes (mínimo para HS256)
    private static final String ISSUER = "order-service";
    private static final String SUBJECT = "order-service-client";
    private static final long TOKEN_EXPIRY_SECONDS = 18000; // 5 hora

    /**
     * Gera um JWT com todos os escopos (orders:write, orders:read, payments:write, payments:read)
     *
     * @return Token JWT válido como String
     * @throws JOSEException Se houver erro na assinatura do token
     */
    public static String generateFullAccessToken() throws JOSEException {
        return generateToken(Arrays.asList("orders:write", "orders:read",
                                           "payments:write", "payments:read"));
    }

    /**
     * Gera um JWT com apenas escopo de escrita para pedidos
     *
     * @return Token JWT válido como String
     * @throws JOSEException Se houver erro na assinatura do token
     */
    public static String generateOrdersWriteToken() throws JOSEException {
        return generateToken(Arrays.asList("orders:write"));
    }

    /**
     * Gera um JWT com apenas escopo de leitura para pedidos
     *
     * @return Token JWT válido como String
     * @throws JOSEException Se houver erro na assinatura do token
     */
    public static String generateOrdersReadToken() throws JOSEException {
        return generateToken(Arrays.asList("orders:read"));
    }

    /**
     * Gera um JWT com apenas escopo de escrita para pagamentos
     *
     * @return Token JWT válido como String
     * @throws JOSEException Se houver erro na assinatura do token
     */
    public static String generatePaymentsWriteToken() throws JOSEException {
        return generateToken(Arrays.asList("payments:write"));
    }

    /**
     * Gera um JWT com apenas escopo de leitura para pagamentos
     *
     * @return Token JWT válido como String
     * @throws JOSEException Se houver erro na assinatura do token
     */
    public static String generatePaymentsReadToken() throws JOSEException {
        return generateToken(Arrays.asList("payments:read"));
    }

    /**
     * Gera um JWT com escopos customizados
     *
     * @param scopes Lista de escopos (ex: "SCOPE_orders:write", "SCOPE_payments:read")
     * @return Token JWT válido como String
     * @throws JOSEException Se houver erro na assinatura do token
     */
    public static String generateToken(java.util.List<String> scopes) throws JOSEException {
        String secret = System.getenv().getOrDefault("JWT_SECRET", DEFAULT_SECRET);
        byte[] secretBytes = secret.getBytes(StandardCharsets.UTF_8);
        JWSSigner signer = new MACSigner(secretBytes);

        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(TOKEN_EXPIRY_SECONDS);

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .issuer(ISSUER)
                .subject(SUBJECT)
                .audience(java.util.Collections.singletonList("order-service-api"))
                .issueTime(new Date(now.toEpochMilli()))
                .expirationTime(new Date(expiresAt.toEpochMilli()))
                .claim("scope", String.join(" ", scopes))
                .build();

        SignedJWT signedJWT = new SignedJWT(
                new JWSHeader(JWSAlgorithm.HS256),
                claimsSet
        );

        signedJWT.sign(signer);
        return signedJWT.serialize();
    }

    /**
     * Método principal para demonstração de geração de JWT
     */
    public static void main(String[] args) throws JOSEException {
        System.out.println("╔════════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║             JWT Token Generator - Order Service                              ║");
        System.out.println("╚════════════════════════════════════════════════════════════════════════════════╝");
        System.out.println();

        // Token com acesso completo (todos os escopos)
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("1. Token com ACESSO COMPLETO (todos os escopos)");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        String fullAccessToken = generateFullAccessToken();
        System.out.println("Escopos: orders:write, orders:read, payments:write, payments:read");
        System.out.println();
        System.out.println("Token JWT:");
        System.out.println(fullAccessToken);
        System.out.println();
        System.out.println("Usar no header Authorization:");
        System.out.println("Authorization: Bearer " + fullAccessToken);
        System.out.println();

        // Token para operações de pedidos (escrita)
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("2. Token para CRIAR/MODIFICAR PEDIDOS (orders:write)");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        String ordersWriteToken = generateOrdersWriteToken();
        System.out.println("Escopo: orders:write");
        System.out.println();
        System.out.println("Token JWT:");
        System.out.println(ordersWriteToken);
        System.out.println();
        System.out.println("Usar no header Authorization:");
        System.out.println("Authorization: Bearer " + ordersWriteToken);
        System.out.println();

        // Token para operações de pedidos (leitura)
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("3. Token para CONSULTAR PEDIDOS (orders:read)");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        String ordersReadToken = generateOrdersReadToken();
        System.out.println("Escopo: orders:read");
        System.out.println();
        System.out.println("Token JWT:");
        System.out.println(ordersReadToken);
        System.out.println();
        System.out.println("Usar no header Authorization:");
        System.out.println("Authorization: Bearer " + ordersReadToken);
        System.out.println();

        // Token para operações de pagamentos (escrita)
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("4. Token para PROCESSAR PAGAMENTOS (payments:write)");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        String paymentsWriteToken = generatePaymentsWriteToken();
        System.out.println("Escopo: payments:write");
        System.out.println();
        System.out.println("Token JWT:");
        System.out.println(paymentsWriteToken);
        System.out.println();
        System.out.println("Usar no header Authorization:");
        System.out.println("Authorization: Bearer " + paymentsWriteToken);
        System.out.println();

        // Token para operações de pagamentos (leitura)
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("5. Token para CONSULTAR PAGAMENTOS (payments:read)");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        String paymentsReadToken = generatePaymentsReadToken();
        System.out.println("Escopo: payments:read");
        System.out.println();
        System.out.println("Token JWT:");
        System.out.println(paymentsReadToken);
        System.out.println();
        System.out.println("Usar no header Authorization:");
        System.out.println("Authorization: Bearer " + paymentsReadToken);
        System.out.println();

        System.out.println("╔════════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                           Configuração                                        ║");
        System.out.println("╠════════════════════════════════════════════════════════════════════════════════╣");
        System.out.println("║ Secret Key (HS256): " + System.getenv().getOrDefault("JWT_SECRET", DEFAULT_SECRET));
        System.out.println("║ Issuer: " + ISSUER);
        System.out.println("║ Subject: " + SUBJECT);
        System.out.println("║ Expiry: " + TOKEN_EXPIRY_SECONDS + " segundos (1 hora)");
        System.out.println("║ Algorithm: HMAC-SHA256");
        System.out.println("╚════════════════════════════════════════════════════════════════════════════════╝");
    }
}

