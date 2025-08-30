package com.farmatodo.challenge.application.products.event;

public record ProductSearchedEvent(String q, int minStock, String requestedBy) {}