package com.fabriciosanches.adapter.inbound.rest;

import com.fabriciosanches.config.SecurityConfig;
import com.fabriciosanches.domain.port.input.ProcessPaymentResult;
import com.fabriciosanches.domain.port.input.ProcessPaymentUseCasePort;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
@TestPropertySource(properties = "security.jwt.secret=01234567890123456789012345678901")
@DisplayName("PaymentController - WebMvc")
class PaymentControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProcessPaymentUseCasePort processPaymentUseCase;

    @Test
    @DisplayName("Deve retornar 401 quando JWT estiver ausente")
    void shouldReturn401WhenTokenIsMissing() throws Exception {
        mockMvc.perform(post("/api/v1/payments")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"orderId\":\"order-1\",\"paymentMethod\":\"pix\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Unauthorized"));
    }

    @Test
    @DisplayName("Deve retornar 403 quando escopo payments:write não for enviado")
    void shouldReturn403WhenScopeIsMissing() throws Exception {
        mockMvc.perform(post("/api/v1/payments")
                        .with(csrf())
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority("SCOPE_orders:write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"orderId\":\"order-1\",\"paymentMethod\":\"pix\"}"))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Forbidden"));
    }

    @Test
    @DisplayName("Deve processar pagamento quando escopo payments:write for válido")
    void shouldProcessPaymentWhenScopeIsValid() throws Exception {
        when(processPaymentUseCase.execute(any())).thenReturn(new ProcessPaymentResult("payment-123", "PROCESSING"));

        mockMvc.perform(post("/api/v1/payments")
                        .with(csrf())
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority("SCOPE_payments:write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new PaymentController.ProcessPaymentRequest("order-1", "pix"))))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.paymentId").value("payment-123"))
                .andExpect(jsonPath("$.status").value("PROCESSING"));

        verify(processPaymentUseCase).execute(any());
    }
}
