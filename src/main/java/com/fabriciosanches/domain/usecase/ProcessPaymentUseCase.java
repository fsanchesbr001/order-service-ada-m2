package com.fabriciosanches.domain.usecase;

import com.fabriciosanches.domain.exception.DomainException;
import com.fabriciosanches.domain.port.input.ProcessPaymentCommand;
import com.fabriciosanches.domain.port.input.ProcessPaymentResult;
import com.fabriciosanches.domain.port.input.ProcessPaymentUseCasePort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ProcessPaymentUseCase implements ProcessPaymentUseCasePort {

    @Override
    public ProcessPaymentResult execute(ProcessPaymentCommand command) {
        if (command == null || command.orderId() == null || command.orderId().isBlank()) {
            throw new DomainException("orderId nao pode ser vazio");
        }
        if (command.paymentMethod() == null || command.paymentMethod().isBlank()) {
            throw new DomainException("paymentMethod nao pode ser vazio");
        }

        return new ProcessPaymentResult(UUID.randomUUID().toString(), "APPROVED");
    }
}

