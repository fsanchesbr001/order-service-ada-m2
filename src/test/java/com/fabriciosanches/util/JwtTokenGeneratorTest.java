package com.fabriciosanches.util;

import com.nimbusds.jose.JOSEException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Classe para gerar e exibir tokens JWT válidos para teste.
 * Execute com: mvn test -Dtest=JwtTokenGeneratorTest
 */
@DisplayName("JWT Token Generator Test")
class JwtTokenGeneratorTest {

    @Test
    @DisplayName("Deve gerar token com acesso completo")
    void generateFullAccessToken() throws JOSEException {
        String token = JwtTokenGenerator.generateFullAccessToken();
        System.out.println("\n═══════════════════════════════════════════════════════════════");
        System.out.println("TOKEN COM ACESSO COMPLETO (todos os escopos)");
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("Escopos: orders:write, orders:read, payments:write, payments:read");
        System.out.println("\nToken JWT:");
        System.out.println(token);
        System.out.println("\nHeader Authorization:");
        System.out.println("Authorization: Bearer " + token);
        System.out.println("═══════════════════════════════════════════════════════════════\n");
    }

    @Test
    @DisplayName("Deve gerar token para orders:write")
    void generateOrdersWriteToken() throws JOSEException {
        String token = JwtTokenGenerator.generateOrdersWriteToken();
        System.out.println("\n═══════════════════════════════════════════════════════════════");
        System.out.println("TOKEN PARA CRIAR/MODIFICAR PEDIDOS (orders:write)");
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("Escopo: orders:write");
        System.out.println("\nEndpoints Permitidos:");
        System.out.println("  • POST   /api/v1/orders");
        System.out.println("  • POST   /api/v1/orders/{id}/items");
        System.out.println("  • DELETE /api/v1/orders/{id}/items/{itemId}");
        System.out.println("  • POST   /api/v1/orders/{id}/confirm");
        System.out.println("  • DELETE /api/v1/orders/{id}");
        System.out.println("\nToken JWT:");
        System.out.println(token);
        System.out.println("\nHeader Authorization:");
        System.out.println("Authorization: Bearer " + token);
        System.out.println("═══════════════════════════════════════════════════════════════\n");
    }

    @Test
    @DisplayName("Deve gerar token para orders:read")
    void generateOrdersReadToken() throws JOSEException {
        String token = JwtTokenGenerator.generateOrdersReadToken();
        System.out.println("\n═══════════════════════════════════════════════════════════════");
        System.out.println("TOKEN PARA CONSULTAR PEDIDOS (orders:read)");
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("Escopo: orders:read");
        System.out.println("\nEndpoints Permitidos:");
        System.out.println("  • GET /api/v1/orders/{id}");
        System.out.println("  • GET /api/v1/orders/customer/{customerId}");
        System.out.println("\nToken JWT:");
        System.out.println(token);
        System.out.println("\nHeader Authorization:");
        System.out.println("Authorization: Bearer " + token);
        System.out.println("═══════════════════════════════════════════════════════════════\n");
    }

    @Test
    @DisplayName("Deve gerar token para payments:write")
    void generatePaymentsWriteToken() throws JOSEException {
        String token = JwtTokenGenerator.generatePaymentsWriteToken();
        System.out.println("\n═══════════════════════════════════════════════════════════════");
        System.out.println("TOKEN PARA PROCESSAR PAGAMENTOS (payments:write)");
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("Escopo: payments:write");
        System.out.println("\nEndpoints Permitidos:");
        System.out.println("  • POST /api/v1/payments");
        System.out.println("  • POST /api/v1/payments/{id}/callback");
        System.out.println("\nToken JWT:");
        System.out.println(token);
        System.out.println("\nHeader Authorization:");
        System.out.println("Authorization: Bearer " + token);
        System.out.println("═══════════════════════════════════════════════════════════════\n");
    }

    @Test
    @DisplayName("Deve gerar token para payments:read")
    void generatePaymentsReadToken() throws JOSEException {
        String token = JwtTokenGenerator.generatePaymentsReadToken();
        System.out.println("\n═══════════════════════════════════════════════════════════════");
        System.out.println("TOKEN PARA CONSULTAR PAGAMENTOS (payments:read)");
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("Escopo: payments:read");
        System.out.println("\nEndpoints Permitidos:");
        System.out.println("  • GET /api/v1/payments/{id}");
        System.out.println("  • GET /api/v1/payments/order/{orderId}");
        System.out.println("\nToken JWT:");
        System.out.println(token);
        System.out.println("\nHeader Authorization:");
        System.out.println("Authorization: Bearer " + token);
        System.out.println("═══════════════════════════════════════════════════════════════\n");
    }
}

