package br.com.truta.models;

public record HealthDetails(
    Boolean failing,
    Integer minResponseTime
) {
    
}
