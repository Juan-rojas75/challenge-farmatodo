package com.farmatodo.challenge.shared.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.*;
import org.slf4j.MDC;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.core.MethodParameter;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

  GlobalExceptionHandler h;

  @BeforeEach
  void setUp() {
    h = new GlobalExceptionHandler();
    MDC.put("traceId", "trace-123");
  }

  @AfterEach
  void tearDown() {
    MDC.clear();
  }

  private HttpServletRequest req(String method, String uri) {
    MockHttpServletRequest r = new MockHttpServletRequest(method, uri);
    r.setRequestURI(uri);
    r.setMethod(method);
    return r;
  }

  /** Utilidad para construir un MethodArgumentNotValidException con un FieldError */
  private MethodArgumentNotValidException manve(String field, String message) throws Exception {
    // Dummy método para construir MethodParameter
    class Dummy { @SuppressWarnings("unused") public void foo(String p) {} }
    Method m = Dummy.class.getDeclaredMethod("foo", String.class);
    MethodParameter mp = new MethodParameter(m, 0);

    var target = new Object();
    var br = new BeanPropertyBindingResult(target, "req");
    br.addError(new FieldError("req", field, message));
    return new MethodArgumentNotValidException(mp, br);
  }

  @Test
  void handleValidation_devuelve_400_y_errors() throws Exception {
    var ex = manve("items[0].qty", "must be greater than or equal to 1");
    var res = h.handleValidation(ex, req("POST", "/v1/cart"));

    assertThat(res.getStatusCode().value()).isEqualTo(400);
    ProblemDetail pd = res.getBody();
    assertThat(pd).isNotNull();
    assertThat(pd.getTitle()).isEqualTo("Validación fallida");
    assertThat(pd.getDetail()).isEqualTo("Campos inválidos");
    assertThat(pd.getType()).isEqualTo(URI.create("about:blank"));
    assertThat(pd.getProperties().get("path")).isEqualTo("/v1/cart");
    assertThat(pd.getProperties().get("method")).isEqualTo("POST");
    assertThat(pd.getProperties().get("traceId")).isEqualTo("trace-123");

    @SuppressWarnings("unchecked")
    Map<String, List<String>> errors = (Map<String, List<String>>) pd.getProperties().get("errors");
    assertThat(errors).containsKey("items[0].qty");
    assertThat(errors.get("items[0].qty")).contains("must be greater than or equal to 1");
  }

  @Test
  void handleConstraint_devuelve_400() {
    var ex = new ConstraintViolationException("bad constraint", java.util.Collections.emptySet());
    var res = h.handleConstraint(ex, req("POST", "/v1/customers"));

    assertThat(res.getStatusCode().value()).isEqualTo(400);
    ProblemDetail pd = res.getBody();
    assertThat(pd).isNotNull();
    assertThat(pd.getTitle()).isEqualTo("Restricciones violadas");
    assertThat(pd.getDetail()).contains("bad constraint");
    assertThat(pd.getProperties().get("path")).isEqualTo("/v1/customers");
    assertThat(pd.getProperties().get("method")).isEqualTo("POST");
    assertThat(pd.getProperties().get("traceId")).isEqualTo("trace-123");
  }

  @Test
  void handleBadRequest_devuelve_400_con_mensaje() {
    var ex = new HttpMessageNotReadableException("json inválido");
    var res = h.handleBadRequest(ex, req("POST", "/v1/orders"));

    assertThat(res.getStatusCode().value()).isEqualTo(400);
    ProblemDetail pd = res.getBody();
    assertThat(pd).isNotNull();
    assertThat(pd.getTitle()).isEqualTo("Solicitud inválida");
    assertThat(pd.getDetail()).contains("json inválido");
    assertThat(pd.getProperties().get("path")).isEqualTo("/v1/orders");
    assertThat(pd.getProperties().get("method")).isEqualTo("POST");
    assertThat(pd.getProperties().get("traceId")).isEqualTo("trace-123");
  }

    @Test
    void handleNotFound_devuelve_404() {
    var ex = new NoHandlerFoundException("GET", "/nope", new HttpHeaders());

    var res = h.handleNotFound(ex, req("GET", "/nope"));

    assertThat(res.getStatusCode().value()).isEqualTo(404);
    var pd = res.getBody();
    assertThat(pd).isNotNull();
    assertThat(pd.getTitle()).isEqualTo("No encontrado");
    assertThat(pd.getDetail()).isEqualTo("Recurso no existe");
    assertThat(pd.getProperties().get("path")).isEqualTo("/nope");
    assertThat(pd.getProperties().get("method")).isEqualTo("GET");
    }

  @Test
  void handleConflict_devuelve_409() {
    var ex = new DataIntegrityViolationException("duplicate key");
    var res = h.handleConflict(ex, req("POST", "/v1/customers"));

    assertThat(res.getStatusCode().value()).isEqualTo(409);
    ProblemDetail pd = res.getBody();
    assertThat(pd).isNotNull();
    assertThat(pd.getTitle()).isEqualTo("Conflicto de datos");
    assertThat(pd.getDetail()).isEqualTo("Violación de integridad");
  }

  @Test
  void handleResourceNotFound_devuelve_404_y_code() {
    // Usa tu excepción real; si getCode() retorna null, validamos que la propiedad exista
    var ex = new ResourceNotFoundException("No existe");
    var res = h.handleResourceNotFound(ex, req("GET", "/v1/products/XYZ"));

    assertThat(res.getStatusCode().value()).isEqualTo(404);
    ProblemDetail pd = res.getBody();
    assertThat(pd).isNotNull();
    assertThat(pd.getTitle()).isEqualTo("No encontrado");
    assertThat(pd.getDetail()).isEqualTo("No existe");
    assertThat(pd.getProperties()).containsKey("code"); // puede ser null, pero la propiedad existe
  }

  @Test
  void handleFallback_devuelve_500() {
    var ex = new RuntimeException("boom");
    var res = h.handleFallback(ex, req("GET", "/v1/ping"));

    assertThat(res.getStatusCode().value()).isEqualTo(500);
    ProblemDetail pd = res.getBody();
    assertThat(pd).isNotNull();
    assertThat(pd.getTitle()).isEqualTo("Error interno");
    assertThat(pd.getDetail()).isEqualTo("Ocurrió un error no controlado");
  }

  @Test
  void handleTokenizationRejected_devuelve_422() {
    var ex = new TokenizationRejectedException("rechazado por probabilidad");
    var res = h.handleTokRejected(ex, req("POST", "/v1/tokenize"));

    assertThat(res.getStatusCode().value()).isEqualTo(422);
    ProblemDetail pd = res.getBody();
    assertThat(pd).isNotNull();
    assertThat(pd.getTitle()).isEqualTo("Tokenización rechazada");
    assertThat(pd.getDetail()).isEqualTo("rechazado por probabilidad");
    // En este handler solo seteas path explícitamente
    assertThat(pd.getProperties().get("path")).isEqualTo("/v1/tokenize");
  }
}
