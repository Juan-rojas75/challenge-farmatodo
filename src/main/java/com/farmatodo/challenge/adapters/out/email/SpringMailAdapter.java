package com.farmatodo.challenge.adapters.out.email;

import com.farmatodo.challenge.application.orders.port.out.SendEmailPort;
import jakarta.mail.internet.MimeMessage;

import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.*;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

@Component
@Profile("prod")
public class SpringMailAdapter implements SendEmailPort {

  private final JavaMailSender mailSender;
  private final TemplateEngine templateEngine;

  public SpringMailAdapter(JavaMailSender mailSender, TemplateEngine templateEngine) {
    this.mailSender = mailSender; this.templateEngine = templateEngine;
  }

  @Override
  public void send(String to, String subject, String body) {
    // body puede ser HTML; lo enviamos como HTML
    sendHtml(to, subject, body);
  }

  /** Helper para enviar un template Thymeleaf */
  public void sendTemplate(String to, String subject, String template, Map<String, Object> model) {
    Context ctx = new Context();
    ctx.setVariables(model);
    String html = templateEngine.process(template, ctx);
    sendHtml(to, subject, html);
  }

  private void sendHtml(String to, String subject, String html) {
    MimeMessage msg = mailSender.createMimeMessage();
    try {
      MimeMessageHelper helper = new MimeMessageHelper(msg, "UTF-8");
      helper.setTo(to);
      helper.setSubject(subject);
      helper.setText(html, true);
      mailSender.send(msg);
    } catch (Exception e) {
      throw new RuntimeException("Error enviando correo", e);
    }
  }
}