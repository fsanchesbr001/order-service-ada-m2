package com.fabriciosanches.adapter.inbound.rest;

import com.fabriciosanches.domain.port.input.CreateOrderCommand;
import com.fabriciosanches.domain.port.input.CreateOrderResult;
import com.fabriciosanches.domain.port.input.CreateOrderUseCasePort;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final CreateOrderUseCasePort createOrderUseCase;

    public OrderController(CreateOrderUseCasePort createOrderUseCase) {
        this.createOrderUseCase = createOrderUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    CreateOrderResponse createOrder(@Valid @RequestBody CreateOrderRequest request) {
        CreateOrderResult result = createOrderUseCase.execute(new CreateOrderCommand(request.customerId()));
        return new CreateOrderResponse(result.orderId());
    }

    public record CreateOrderRequest(@NotBlank String customerId) {
    }

    public record CreateOrderResponse(String orderId) {
    }
}
