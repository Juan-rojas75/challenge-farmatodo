package com.farmatodo.challenge.bootstrap.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "products")
public class ProductsProperties {
  private int minStockThreshold = 1;
  public int getMinStockThreshold() { return minStockThreshold; }
  public void setMinStockThreshold(int v) { this.minStockThreshold = v; }
}
