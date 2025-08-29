package com.farmatodo.challenge.adapters.in.web.orders.request;

public record CreateOrderRequest(String customerId, String cartId, String shippingAddress, String token,
                                 Integer retryCount, Double rejectProbability) {}