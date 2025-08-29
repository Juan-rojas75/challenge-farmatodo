package com.farmatodo.challenge.bootstrap.config;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "tokenization")
public class TokenizationProperties {
  @DecimalMin("0.0") @DecimalMax("1.0")
  private double rejectProbability = 0.0;

  public double getRejectProbability() { return rejectProbability; }
  public void setRejectProbability(double rejectProbability) { this.rejectProbability = rejectProbability; }
}