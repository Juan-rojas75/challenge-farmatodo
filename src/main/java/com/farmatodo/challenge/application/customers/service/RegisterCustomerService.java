package com.farmatodo.challenge.application.customers.service;


import com.farmatodo.challenge.application.customers.port.in.RegisterCustomerUseCase;
import com.farmatodo.challenge.application.customers.port.out.FindCustomerByEmailPort;
import com.farmatodo.challenge.application.customers.port.out.FindCustomerByPhonePort;
import com.farmatodo.challenge.application.customers.port.out.SaveCustomerPort;
import com.farmatodo.challenge.domain.customers.model.Customer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class RegisterCustomerService implements RegisterCustomerUseCase {

  private final SaveCustomerPort savePort;
  private final FindCustomerByEmailPort findByEmail;
  private final FindCustomerByPhonePort findByPhone;

  public RegisterCustomerService(SaveCustomerPort savePort, FindCustomerByEmailPort findByEmail, FindCustomerByPhonePort findByPhone) {
    this.savePort = savePort; this.findByEmail = findByEmail; this.findByPhone = findByPhone;
  }

  @Override
  public UUID register(Command cmd) {
    findByEmail.findByEmail(cmd.email())
        .ifPresent(c -> { throw new IllegalArgumentException("Email ya existe"); });

    findByPhone.findByPhone(cmd.phone())
        .ifPresent(c -> { throw new IllegalArgumentException("Phone ya existe"); });

    Customer toSave = new Customer(UUID.randomUUID(), cmd.name(), cmd.email(), cmd.phone(), cmd.address());
    Customer saved = savePort.save(toSave);
    return saved.getId();
  }
}