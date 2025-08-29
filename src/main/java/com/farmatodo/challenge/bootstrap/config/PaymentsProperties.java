package com.farmatodo.challenge.bootstrap.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
@ConfigurationProperties(prefix="payments")
public class PaymentsProperties {
  private int retryCount = 3; private double rejectProbability = 0.2;
  public int getRetryCount(){return retryCount;} public void setRetryCount(int v){retryCount=v;}
  public double getRejectProbability(){return rejectProbability;} public void setRejectProbability(double v){rejectProbability=v;}
}