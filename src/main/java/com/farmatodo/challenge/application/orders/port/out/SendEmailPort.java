package com.farmatodo.challenge.application.orders.port.out;

public interface SendEmailPort {
  void send(String to, String subject, String body);
}