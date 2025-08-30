package com.farmatodo.challenge.application.cart.port.in;

import java.util.*;

public interface AddToCartUseCase {
  record Command(UUID customerId, List<Item> items){ public record Item(String sku,int qty){} }
  UUID addOrCreate(Command cmd);
}
