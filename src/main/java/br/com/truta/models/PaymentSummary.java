package br.com.truta.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PaymentSummary(
    @JsonProperty("default") PaymentDetails defaulValue,
    @JsonProperty("fallback") PaymentDetails fallbackValue) {
}
