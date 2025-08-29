package com.farmatodo.challenge.application.customers.service;

import com.farmatodo.challenge.application.customers.port.in.RegisterCustomerUseCase;
import com.farmatodo.challenge.application.customers.port.out.FindCustomerByEmailPort;
import com.farmatodo.challenge.application.customers.port.out.FindCustomerByPhonePort;
import com.farmatodo.challenge.application.customers.port.out.SaveCustomerPort;
import com.farmatodo.challenge.domain.customers.model.Customer;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class RegisterCustomerServiceTest {

  @Test
  void registra_ok_cuando_no_existe_email_ni_phone() {
    var save = mock(SaveCustomerPort.class);
    var byEmail = mock(FindCustomerByEmailPort.class);
    var byPhone = mock(FindCustomerByPhonePort.class);

    when(byEmail.findByEmail("a@b.com")).thenReturn(Optional.empty());
    when(byPhone.findByPhone("3001112233")).thenReturn(Optional.empty());
    when(save.save(any())).thenAnswer(inv -> inv.getArgument(0));

    var svc = new RegisterCustomerService(save, byEmail, byPhone);
    var cmd = new RegisterCustomerUseCase.Command("Andres","a@b.com","3001112233","Calle 123");

    UUID id = svc.register(cmd);

    assertThat(id).isNotNull();

    var cap = ArgumentCaptor.forClass(Customer.class);
    verify(save).save(cap.capture());
    var saved = cap.getValue();
    assertThat(saved.getName()).isEqualTo("Andres");
    assertThat(saved.getEmail()).isEqualTo("a@b.com");
    assertThat(saved.getPhone()).isEqualTo("3001112233");
    assertThat(saved.getAddress()).isEqualTo("Calle 123");
  }

  @Test
  void lanza_si_email_existe_y_no_consulta_phone() {
    var save = mock(SaveCustomerPort.class);
    var byEmail = mock(FindCustomerByEmailPort.class);
    var byPhone = mock(FindCustomerByPhonePort.class);

    when(byEmail.findByEmail("a@b.com")).thenReturn(Optional.of(
        new Customer(UUID.randomUUID(),"X","a@b.com","300","addr")));

    var svc = new RegisterCustomerService(save, byEmail, byPhone);
    var cmd = new RegisterCustomerUseCase.Command("Andres","a@b.com","3001112233","Calle 123");

    assertThatThrownBy(() -> svc.register(cmd))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Email ya existe");

    verify(byPhone, never()).findByPhone(any());
    verify(save, never()).save(any());
  }

  @Test
  void lanza_si_phone_existe() {
    var save = mock(SaveCustomerPort.class);
    var byEmail = mock(FindCustomerByEmailPort.class);
    var byPhone = mock(FindCustomerByPhonePort.class);

    when(byEmail.findByEmail("a@b.com")).thenReturn(Optional.empty());
    when(byPhone.findByPhone("3001112233")).thenReturn(Optional.of(
        new Customer(UUID.randomUUID(),"Y","y@z.com","3001112233","addr")));

    var svc = new RegisterCustomerService(save, byEmail, byPhone);
    var cmd = new RegisterCustomerUseCase.Command("Andres","a@b.com","3001112233","Calle 123");

    assertThatThrownBy(() -> svc.register(cmd))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Phone ya existe");

    verify(save, never()).save(any());
  }
}
