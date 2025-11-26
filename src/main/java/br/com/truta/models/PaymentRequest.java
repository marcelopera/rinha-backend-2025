package br.com.truta.models;

import java.math.BigDecimal;

public record PaymentRequest(
    String correlationId,
    BigDecimal amount) {
}
