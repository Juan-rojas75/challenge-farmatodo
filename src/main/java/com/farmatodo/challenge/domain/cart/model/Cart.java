package com.farmatodo.challenge.domain.cart.model;

import java.util.*;

public class Cart {
  private final UUID id; private final UUID customerId; private final List<CartItem> items;
  public Cart(UUID id, UUID customerId, List<CartItem> items){ this.id=id; this.customerId=customerId; this.items = new ArrayList<>(items); }
  public UUID getId(){return id;} public UUID getCustomerId(){return customerId;} public List<CartItem> getItems(){return List.copyOf(items);}
  public void upsertItems(List<CartItem> newItems){
    var map = new HashMap<String,Integer>();
    items.forEach(i -> map.put(i.sku(), map.getOrDefault(i.sku(),0)+i.qty()));
    newItems.forEach(i -> map.put(i.sku(), map.getOrDefault(i.sku(),0)+i.qty()));
    items.clear(); map.forEach((k,v)-> items.add(new CartItem(k,v)));
  }
  public boolean isEmpty(){ return items.isEmpty(); }
}
