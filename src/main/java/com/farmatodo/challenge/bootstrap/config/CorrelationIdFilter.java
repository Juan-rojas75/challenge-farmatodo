package com.farmatodo.challenge.bootstrap.config;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class CorrelationIdFilter extends OncePerRequestFilter {
  @Override
  protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
      throws ServletException, IOException {
    String incoming = req.getHeader("X-Request-Id");
    String traceId = (incoming == null || incoming.isBlank()) ? UUID.randomUUID().toString() : incoming;
    MDC.put("traceId", traceId);
    res.setHeader("X-Request-Id", traceId);
    try { chain.doFilter(req, res); }
    finally { MDC.remove("traceId"); }
  }
}
