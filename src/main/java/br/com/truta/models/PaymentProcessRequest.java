package br.com.truta.models;

public record PaymentProcessRequest(
    String correlationId,
    String amount,
    String requestedAt
) {
} 