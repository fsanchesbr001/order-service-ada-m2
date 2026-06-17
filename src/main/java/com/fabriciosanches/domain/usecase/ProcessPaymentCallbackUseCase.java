package com.fabriciosanches.domain.usecase;

import com.fabriciosanches.domain.exception.DomainException;
import com.fabriciosanches.domain.exception.ResourceNotFoundException;
import com.fabriciosanches.domain.model.Order;
import com.fabriciosanches.domain.model.Payment;
import com.fabriciosanches.domain.port.input.ProcessPaymentCallbackCommand;
import com.fabriciosanches.domain.port.input.ProcessPaymentCallbackResult;
import com.fabriciosanches.domain.port.input.ProcessPaymentCallbackUseCasePort;
import com.fabriciosanches.domain.port.output.OrderRepositoryPort;
import com.fabriciosanches.domain.port.output.PaymentRepositoryPort;
import org.springframework.stereotype.Service;

@Service
public class ProcessPaymentCallbackUseCase implements ProcessPaymentCallbackUseCasePort {

    private final PaymentRepositoryPort paymentRepository;
    private final OrderRepositoryPort orderRepository;

    public ProcessPaymentCallbackUseCase(PaymentRepositoryPort paymentRepository,
                                         OrderRepositoryPort orderRepository) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public ProcessPaymentCallbackResult execute(ProcessPaymentCallbackCommand command) {
        if (command == null || command.paymentId() == null || command.paymentId().isBlank()) {
            throw new DomainException("paymentId nao pode ser vazio");
        }
        if (command.callbackStatus() == null || command.callbackStatus().isBlank()) {
            throw new DomainException("callbackStatus nao pode ser vazio");
        }
        Payment payment = paymentRepository.findById(command.paymentId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Pagamento não encontrado. paymentId=" + command.paymentId()));
        if (payment.isAlreadyProcessed()) {
            return new ProcessPaymentCallbackResult(payment.getId(), payment.getStatus().name());
        }
        String normalizedStatus = command.callbackStatus().toUpperCase();
        if ("APPROVED".equals(normalizedStatus)) {
            payment.approve();
        } else if ("REJECTED".equals(normalizedStatus)) {
            payment.reject();
            Order order = orderRepository.findById(payment.getOrderId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Pedido não encontrado. orderId=" + payment.getOrderId()));
            order.applyPaymentFailure();
            orderRepository.save(order);
        } else {
            throw new DomainException("callbackStatus inválido: " + command.callbackStatus());
        }
        Payment saved = paymentRepository.save(payment);
        return new ProcessPaymentCallbackResult(saved.getId(), saved.getStatus().name());
    }
}
