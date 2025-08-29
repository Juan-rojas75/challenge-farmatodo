package com.farmatodo.challenge.adapters.out.email;

import com.farmatodo.challenge.application.orders.port.out.SendEmailPort; import org.slf4j.*;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
class ConsoleMailAdapter implements SendEmailPort {
  private static final Logger log = LoggerFactory.getLogger(ConsoleMailAdapter.class);
  @Override public void send(String to, String subject, String body){ log.info("MAIL to={} subj={} body={}", to, subject, body); }
}
