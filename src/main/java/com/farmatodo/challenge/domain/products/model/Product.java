package com.farmatodo.challenge.domain.products.model;

import java.util.UUID;

public class Product {
  private final UUID id;
  private final String sku;
  private final String name;
  private final int stock;
  private final double price;

  public Product(UUID id, String sku, String name, int stock, double price) {
    this.id = id; this.sku = sku; this.name = name; this.stock = stock; this.price = price;
  }
  public UUID getId() { return id; }
  public String getSku() { return sku; }
  public String getName() { return name; }
  public int getStock() { return stock; }
  public double getPrice() { return price; }
}