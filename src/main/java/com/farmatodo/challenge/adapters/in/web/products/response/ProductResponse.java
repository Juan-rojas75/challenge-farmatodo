package com.farmatodo.challenge.adapters.in.web.products.response;

public record  ProductResponse(
    String id,
    String sku,
    String name,
    int stock,
    double price
) {

}
