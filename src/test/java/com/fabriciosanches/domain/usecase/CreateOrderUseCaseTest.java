package com.fabriciosanches.domain.usecase;

import com.fabriciosanches.domain.exception.DomainException;
import com.fabriciosanches.domain.port.input.CreateOrderCommand;
import com.fabriciosanches.domain.port.input.CreateOrderResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CreateOrderUseCase - Testes de Unidade")
class CreateOrderUseCaseTest {

    private CreateOrderUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new CreateOrderUseCase();
    }

    @Test
    @DisplayName("Deve criar pedido e retornar um orderId quando o comando é válido")
    void shouldReturnOrderIdWhenCommandIsValid() {
        CreateOrderResult result = useCase.execute(new CreateOrderCommand("customer-001"));

        assertNotNull(result);
        assertNotNull(result.orderId());
        assertFalse(result.orderId().isBlank());
    }

    @Test
    @DisplayName("Deve lançar DomainException quando o comando é nulo")
    void shouldThrowWhenCommandIsNull() {
        DomainException ex = assertThrows(DomainException.class, () -> useCase.execute(null));
        assertTrue(ex.getMessage().contains("customerId"));
    }

    @Test
    @DisplayName("Deve lançar DomainException quando o customerId é nulo")
    void shouldThrowWhenCustomerIdIsNull() {
        DomainException ex = assertThrows(DomainException.class,
                () -> useCase.execute(new CreateOrderCommand(null)));
        assertTrue(ex.getMessage().contains("customerId"));
    }

    @Test
    @DisplayName("Deve lançar DomainException quando o customerId está em branco")
    void shouldThrowWhenCustomerIdIsBlank() {
        DomainException ex = assertThrows(DomainException.class,
                () -> useCase.execute(new CreateOrderCommand("   ")));
        assertTrue(ex.getMessage().contains("customerId"));
    }

    @Test
    @DisplayName("Deve gerar orderId único a cada execução")
    void shouldGenerateUniqueOrderIdOnEachExecution() {
        CreateOrderResult first = useCase.execute(new CreateOrderCommand("customer-001"));
        CreateOrderResult second = useCase.execute(new CreateOrderCommand("customer-001"));

        assertNotEquals(first.orderId(), second.orderId());
    }
}
