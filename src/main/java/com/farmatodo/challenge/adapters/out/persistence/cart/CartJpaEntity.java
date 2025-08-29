package com.farmatodo.challenge.adapters.out.persistence.cart;

import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Data
@Entity @Table(name = "carts")
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CartJpaEntity {

  @Id
  @EqualsAndHashCode.Include
  @ToString.Include
  private UUID id;

  @Column(nullable = false)
  private UUID customerId;

  @OneToMany(mappedBy = "cart",
             cascade = CascadeType.ALL,
             orphanRemoval = true,
             fetch = FetchType.LAZY)
  @ToString.Exclude
  private List<CartItemJpaEntity> items = new ArrayList<>();

  public void addItem(CartItemJpaEntity i){ i.setCart(this); this.items.add(i); }
  public void removeItem(CartItemJpaEntity i){ i.setCart(null); this.items.remove(i); }
}