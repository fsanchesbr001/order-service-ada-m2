package com.fabriciosanches.domain.port.output;

import com.fabriciosanches.domain.model.Order;

import java.util.Optional;

public interface OrderRepositoryPort {

    Order save(Order order);

    Optional<Order> findById(String id);
}
