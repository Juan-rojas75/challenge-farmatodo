package com.farmatodo.challenge.application.products.port.out;

import com.farmatodo.challenge.domain.products.model.Product;

public interface SaveProductPort {
    Product save(Product product);
}
