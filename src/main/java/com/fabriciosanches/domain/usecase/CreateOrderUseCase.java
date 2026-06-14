package com.fabriciosanches.domain.usecase;

import com.fabriciosanches.domain.exception.DomainException;
import com.fabriciosanches.domain.port.input.CreateOrderCommand;
import com.fabriciosanches.domain.port.input.CreateOrderResult;
import com.fabriciosanches.domain.port.input.CreateOrderUseCasePort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CreateOrderUseCase implements CreateOrderUseCasePort {

    @Override
    public CreateOrderResult execute(CreateOrderCommand command) {
        if (command == null || command.customerId() == null || command.customerId().isBlank()) {
            throw new DomainException("customerId nao pode ser vazio");
        }

        return new CreateOrderResult(UUID.randomUUID().toString());
    }
}

