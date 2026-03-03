package com.pictet.backend_task.repository.model;

public record Consequence(
    ConsequenceType type,
    String value,
    String text,
    Integer previousHealth,
    Integer currentHealth
) {}

