package com.farmatodo.challenge.bootstrap.config;

import jakarta.servlet.ServletException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ApiKeyFilterTest {

  @AfterEach
  void cleanup() { SecurityContextHolder.clearContext(); }

  private ApiKeyFilter newFilterWithKeys(String... keys) {
    var props = new SecurityProperties();
    props.setApiKeys(List.of(keys));
    return new ApiKeyFilter(props);
  }

  @Test
  void autentica_con_api_key_valida() throws ServletException, IOException {
    var filter = newFilterWithKeys("valid-key");

    var req = new MockHttpServletRequest("GET", "/v1/customers");
    req.addHeader(ApiKeyFilter.HEADER, "valid-key");
    var res = new MockHttpServletResponse();

    filter.doFilter(req, res, new MockFilterChain());

    var auth = SecurityContextHolder.getContext().getAuthentication();
    assertThat(auth).isNotNull();
    assertThat(auth.getName()).isEqualTo("valid-key");
  }

  @Test
  void no_autentica_en_ping_aunque_envie_key() throws ServletException, IOException {
    var filter = newFilterWithKeys("valid-key");

    var req = new MockHttpServletRequest("GET", "/v1/ping");
    req.addHeader(ApiKeyFilter.HEADER, "valid-key");
    var res = new MockHttpServletResponse();

    filter.doFilter(req, res, new MockFilterChain());

    var auth = SecurityContextHolder.getContext().getAuthentication();
    assertThat(auth).isNull(); // shouldNotFilter -> no autentica
  }

  @Test
  void no_autentica_con_api_key_invalida() throws ServletException, IOException {
    var filter = newFilterWithKeys("valid-key");

    var req = new MockHttpServletRequest("GET", "/v1/customers");
    // sin header
    var res = new MockHttpServletResponse();

    filter.doFilter(req, res, new MockFilterChain());

    var auth = SecurityContextHolder.getContext().getAuthentication();
    assertThat(auth).isNull();
  }
}
