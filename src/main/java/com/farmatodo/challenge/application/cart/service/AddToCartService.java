package com.farmatodo.challenge.application.cart.service;

import com.farmatodo.challenge.application.cart.port.in.AddToCartUseCase;
import com.farmatodo.challenge.application.cart.port.out.LoadSaveCartPort;
import com.farmatodo.challenge.domain.cart.model.*;
import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service @Transactional
public class AddToCartService implements AddToCartUseCase {
  private final LoadSaveCartPort port;
  public AddToCartService(LoadSaveCartPort port){ this.port=port; }
  /**
   * Crea un nuevo carrito o agrega items a uno existente.
   * <ul>
   *   <li>Si el carrito no existe, se crea uno nuevo.</li>
   *   <li>Si el carrito existe, se agregan los items al carrito existente.</li>
   *   <li>Devuelve el id del carrito creado o actualizado.</li>
   * </ul>
   */
  @Override public UUID addOrCreate(Command cmd){
    var incoming = cmd.items().stream().map(i-> new CartItem(i.sku(), i.qty())).toList();
    var cart = port.loadByCustomer(cmd.customerId())
      .orElseGet(()-> new Cart(UUID.randomUUID(), cmd.customerId(), List.of()));
    cart.upsertItems(incoming);
    return port.save(cart).getId();
  }
}
