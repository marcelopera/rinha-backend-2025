package br.com.truta.models;

import java.math.BigDecimal;

public record PaymentDetails(
    BigDecimal totalRequests,
    BigDecimal totalAmount) {
}
