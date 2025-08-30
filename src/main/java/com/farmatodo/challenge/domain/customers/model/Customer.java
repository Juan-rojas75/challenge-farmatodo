package com.farmatodo.challenge.domain.customers.model;

import java.util.UUID;

public class Customer {
  private final UUID id;
  private final String name;
  private final String email;
  private final String phone;
  private final String address;

  public Customer(UUID id, String name, String email, String phone, String address) {
    this.id = id; this.name = name; this.email = email; this.phone = phone; this.address = address;
  }
  public UUID getId() { return id; }
  public String getName() { return name; }
  public String getEmail() { return email; }
  public String getPhone() { return phone; }
  public String getAddress() { return address; }
}