package com.fabriciosanches.domain.usecase;

import com.fabriciosanches.domain.exception.DomainException;
import com.fabriciosanches.domain.port.input.ProcessPaymentCommand;
import com.fabriciosanches.domain.port.input.ProcessPaymentResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ProcessPaymentUseCase - Testes de Unidade")
class ProcessPaymentUseCaseTest {

    private ProcessPaymentUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ProcessPaymentUseCase();
    }

    @Test
    @DisplayName("Deve processar pagamento e retornar paymentId quando o comando é válido")
    void shouldReturnPaymentResultWhenCommandIsValid() {
        ProcessPaymentResult result = useCase.execute(new ProcessPaymentCommand("order-001", "pix"));

        assertNotNull(result);
        assertNotNull(result.paymentId());
        assertFalse(result.paymentId().isBlank());
        assertNotNull(result.status());
    }

    @Test
    @DisplayName("Deve lançar DomainException quando o comando é nulo")
    void shouldThrowWhenCommandIsNull() {
        DomainException ex = assertThrows(DomainException.class, () -> useCase.execute(null));
        assertTrue(ex.getMessage().contains("orderId"));
    }

    @Test
    @DisplayName("Deve lançar DomainException quando o orderId é nulo")
    void shouldThrowWhenOrderIdIsNull() {
        DomainException ex = assertThrows(DomainException.class,
                () -> useCase.execute(new ProcessPaymentCommand(null, "pix")));
        assertTrue(ex.getMessage().contains("orderId"));
    }

    @Test
    @DisplayName("Deve lançar DomainException quando o orderId está em branco")
    void shouldThrowWhenOrderIdIsBlank() {
        DomainException ex = assertThrows(DomainException.class,
                () -> useCase.execute(new ProcessPaymentCommand("  ", "pix")));
        assertTrue(ex.getMessage().contains("orderId"));
    }

    @Test
    @DisplayName("Deve lançar DomainException quando o paymentMethod é nulo")
    void shouldThrowWhenPaymentMethodIsNull() {
        DomainException ex = assertThrows(DomainException.class,
                () -> useCase.execute(new ProcessPaymentCommand("order-001", null)));
        assertTrue(ex.getMessage().contains("paymentMethod"));
    }

    @Test
    @DisplayName("Deve lançar DomainException quando o paymentMethod está em branco")
    void shouldThrowWhenPaymentMethodIsBlank() {
        DomainException ex = assertThrows(DomainException.class,
                () -> useCase.execute(new ProcessPaymentCommand("order-001", "")));
        assertTrue(ex.getMessage().contains("paymentMethod"));
    }

    @Test
    @DisplayName("Deve gerar paymentId único a cada execução")
    void shouldGenerateUniquePaymentIdOnEachExecution() {
        ProcessPaymentResult first = useCase.execute(new ProcessPaymentCommand("order-001", "pix"));
        ProcessPaymentResult second = useCase.execute(new ProcessPaymentCommand("order-001", "pix"));

        assertNotEquals(first.paymentId(), second.paymentId());
    }
}
