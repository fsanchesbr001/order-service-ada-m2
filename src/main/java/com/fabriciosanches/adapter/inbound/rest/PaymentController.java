package com.fabriciosanches.adapter.inbound.rest;

import com.fabriciosanches.domain.port.input.ProcessPaymentCommand;
import com.fabriciosanches.domain.port.input.ProcessPaymentResult;
import com.fabriciosanches.domain.port.input.ProcessPaymentUseCasePort;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final ProcessPaymentUseCasePort processPaymentUseCase;

    public PaymentController(ProcessPaymentUseCasePort processPaymentUseCase) {
        this.processPaymentUseCase = processPaymentUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    ProcessPaymentResponse process(@Valid @RequestBody ProcessPaymentRequest request) {
        ProcessPaymentResult result = processPaymentUseCase.execute(
                new ProcessPaymentCommand(request.orderId(), request.paymentMethod())
        );
        return new ProcessPaymentResponse(result.paymentId(), result.status());
    }

    public record ProcessPaymentRequest(@NotBlank String orderId,
                                        @NotBlank String paymentMethod) {
    }

    public record ProcessPaymentResponse(String paymentId, String status) {
    }
}
