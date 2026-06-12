package com.fabriciosanches.domain.model;

import com.fabriciosanches.domain.exception.DomainException;

public enum OrderStatus {

    PENDENTE {
        @Override
        public boolean canAddItems() {
            return true;
        }

        @Override
        public boolean canConfirm() {
            return true;
        }

        @Override
        public boolean canApplyPaymentFailure() {
            return false;
        }
    },

    CONFIRMADO {
        @Override
        public boolean canAddItems() {
            return false;
        }

        @Override
        public boolean canConfirm() {
            return false;
        }

        @Override
        public boolean canApplyPaymentFailure() {
            return true;
        }
    },

    CANCELADO {
        @Override
        public boolean canAddItems() {
            return false;
        }

        @Override
        public boolean canConfirm() {
            return false;
        }

        @Override
        public boolean canApplyPaymentFailure() {
            return false;
        }
    };

    public abstract boolean canAddItems();

    public abstract boolean canConfirm();

    public abstract boolean canApplyPaymentFailure();

    public OrderStatus transitionTo(OrderStatus next) {
        if (next == CONFIRMADO && !this.canConfirm()) {
            throw new DomainException(
                    String.format("Transição inválida: não é possível passar de '%s' para '%s'.", this, next));
        }
        if (next == CANCELADO && !this.canApplyPaymentFailure() && this != PENDENTE) {
            throw new DomainException(
                    String.format("Transição inválida: não é possível passar de '%s' para '%s'.", this, next));
        }
        return next;
    }
}
