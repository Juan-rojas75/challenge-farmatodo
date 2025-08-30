package com.farmatodo.challenge.application.orders.port.out;

import com.farmatodo.challenge.domain.cart.model.Cart; 
import java.util.*;

public interface LoadCartPort { Optional<Cart> loadById(UUID cartId); }
