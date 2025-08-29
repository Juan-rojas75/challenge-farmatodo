package com.farmatodo.challenge.application.customers.port.in;

import java.util.UUID;

public interface RegisterCustomerUseCase {
  record Command(String name, String email, String phone, String address) {}
  UUID register(Command cmd);
}