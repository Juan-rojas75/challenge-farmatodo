package com.farmatodo.challenge.bootstrap.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.*;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.*;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class JsonAuthHandlers implements AuthenticationEntryPoint, AccessDeniedHandler {
  private final ObjectMapper om = new ObjectMapper();

  private void write(HttpServletResponse res, HttpStatus status, String title, String detail) throws IOException {
    res.setStatus(status.value());
    res.setContentType("application/json");
    var body = Map.of(
      "status", status.value(),
      "title", title,
      "detail", detail,
      "traceId", String.valueOf(MDC.get("traceId"))
    );
    om.writeValue(res.getWriter(), body);
  }

  @Override
  public void commence(HttpServletRequest req, HttpServletResponse res, org.springframework.security.core.AuthenticationException ex) throws IOException {
    write(res, HttpStatus.UNAUTHORIZED, "No autenticado", "API Key requerida o inválida");
  }

  @Override
  public void handle(HttpServletRequest req, HttpServletResponse res, AccessDeniedException ex) throws IOException {
    write(res, HttpStatus.FORBIDDEN, "Acceso denegado", "No tiene permisos para esta operación");
  }
}
