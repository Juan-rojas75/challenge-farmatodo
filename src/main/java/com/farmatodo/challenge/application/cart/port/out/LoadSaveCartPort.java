package com.farmatodo.challenge.application.cart.port.out;

import com.farmatodo.challenge.domain.cart.model.Cart;
import java.util.*;

public interface LoadSaveCartPort {
  Optional<Cart> loadByCustomer(UUID customerId);
  Optional<Cart> loadById(UUID cartId);
  Cart save(Cart cart);
}