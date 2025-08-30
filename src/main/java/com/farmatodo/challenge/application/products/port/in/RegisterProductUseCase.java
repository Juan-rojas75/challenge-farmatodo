package com.farmatodo.challenge.application.products.port.in;

import java.util.UUID;

public interface RegisterProductUseCase {
    record Command(String sku, String name, Integer stock, Double price) {}
    UUID register(Command cmd);
}
