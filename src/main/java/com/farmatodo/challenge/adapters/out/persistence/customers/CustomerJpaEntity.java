package com.farmatodo.challenge.adapters.out.persistence.customers;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity @Table(name="customers")
public class CustomerJpaEntity {
  @Id private UUID id;
  private String name;
  @Column(unique = true) private String email;
  private String phone;
  private String address;
}