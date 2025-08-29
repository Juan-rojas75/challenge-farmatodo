package com.farmatodo.challenge.adapters.out.email;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.thymeleaf.TemplateEngine;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Map;
import java.util.Properties;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class SpringMailAdapterTest {

  JavaMailSender mailSender;
  TemplateEngine templateEngine;
  SpringMailAdapter adapter;
  Session session;

  @BeforeEach
  void setUp() {
    mailSender = mock(JavaMailSender.class);
    templateEngine = mock(TemplateEngine.class);
    adapter = new SpringMailAdapter(mailSender, templateEngine);
    session = Session.getInstance(new Properties()); // sesión simple para MimeMessage real
  }

  @Test
  void send_envia_html_con_to_y_subject_correctos() throws Exception {
    // arrange
    when(mailSender.createMimeMessage()).thenReturn(new MimeMessage(session));

    // act
    adapter.send("user@mail.com", "Pago exitoso", "<b>OK</b>");

    // assert
    ArgumentCaptor<MimeMessage> cap = ArgumentCaptor.forClass(MimeMessage.class);
    verify(mailSender, times(1)).send(cap.capture());
    MimeMessage sent = cap.getValue();

    assertThat(sent.getAllRecipients()[0].toString()).isEqualTo("user@mail.com");
    assertThat(sent.getSubject()).isEqualTo("Pago exitoso");

    Object content = sent.getContent();
    assertThat(content).isInstanceOf(String.class);
    assertThat((String) content).isEqualTo("<b>OK</b>");
    // Content-Type debe ser text/html
    String ct = sent.getDataHandler().getContentType();
    assertThat(ct).containsIgnoringCase("text/html");
  }

  @Test
  void sendTemplate_procesa_template_y_envia_html_renderizado() throws Exception {
    // arrange
    when(mailSender.createMimeMessage()).thenReturn(new MimeMessage(session));
    when(templateEngine.process(eq("mail/payment-success"), any()))
        .thenReturn("<h1>Hola Andres</h1>");

    // act
    adapter.sendTemplate("andres@mail.com", "Pago exitoso",
        "mail/payment-success", Map.of("name", "Andres", "orderId", "123"));

    // assert
    verify(templateEngine, times(1)).process(eq("mail/payment-success"), any());

    ArgumentCaptor<MimeMessage> cap = ArgumentCaptor.forClass(MimeMessage.class);
    verify(mailSender).send(cap.capture());
    MimeMessage sent = cap.getValue();

    assertThat(sent.getAllRecipients()[0].toString()).isEqualTo("andres@mail.com");
    assertThat(sent.getSubject()).isEqualTo("Pago exitoso");
    String ct = sent.getDataHandler().getContentType();
    assertThat(ct).containsIgnoringCase("text/html");
    assertThat((String) sent.getContent()).isEqualTo("<h1>Hola Andres</h1>");
  }

  @Test
  void send_propaga_runtime_exception_con_mensaje_esperado() {
    // arrange
    when(mailSender.createMimeMessage()).thenReturn(new MimeMessage(session));
    doThrow(new RuntimeException("smtp down"))
        .when(mailSender).send(any(MimeMessage.class));

    // act + assert
    assertThatThrownBy(() ->
        adapter.send("x@y.com", "Asunto", "<i>body</i>")
    )
    .isInstanceOf(RuntimeException.class)
    .hasMessage("Error enviando correo")
    .cause()
    .hasMessage("smtp down");
  }
}
