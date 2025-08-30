package com.farmatodo.challenge.shared.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.MDC;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.net.URI;
import java.util.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private ProblemDetail pd(HttpStatus status, String title, String detail, HttpServletRequest req) {
    ProblemDetail body = ProblemDetail.forStatusAndDetail(status, detail == null ? status.getReasonPhrase() : detail);
    body.setTitle(title == null ? status.getReasonPhrase() : title);
    body.setType(URI.create("about:blank"));
    body.setProperty("path", req.getRequestURI());
    body.setProperty("method", req.getMethod());
    body.setProperty("traceId", Optional.ofNullable(MDC.get("traceId")).orElse(""));
    return body;
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ProblemDetail> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
    Map<String, List<String>> errors = new LinkedHashMap<>();
    for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
      errors.computeIfAbsent(fe.getField(), k -> new ArrayList<>()).add(fe.getDefaultMessage());
    }
    ProblemDetail body = pd(HttpStatus.BAD_REQUEST, "Validación fallida", "Campos inválidos", req);
    body.setProperty("errors", errors);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ProblemDetail> handleConstraint(ConstraintViolationException ex, HttpServletRequest req) {
    ProblemDetail body = pd(HttpStatus.BAD_REQUEST, "Restricciones violadas", ex.getMessage(), req);
    return ResponseEntity.badRequest().body(body);
  }

  @ExceptionHandler({
      HttpMessageNotReadableException.class,
      MethodArgumentTypeMismatchException.class
  })
  public ResponseEntity<ProblemDetail> handleBadRequest(Exception ex, HttpServletRequest req) {
    ProblemDetail body = pd(HttpStatus.BAD_REQUEST, "Solicitud inválida", ex.getMessage(), req);
    return ResponseEntity.badRequest().body(body);
  }

  @ExceptionHandler(NoHandlerFoundException.class)
  public ResponseEntity<ProblemDetail> handleNotFound(NoHandlerFoundException ex, HttpServletRequest req) {
    ProblemDetail body = pd(HttpStatus.NOT_FOUND, "No encontrado", "Recurso no existe", req);
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ProblemDetail> handleConflict(DataIntegrityViolationException ex, HttpServletRequest req) {
    ProblemDetail body = pd(HttpStatus.CONFLICT, "Conflicto de datos", "Violación de integridad", req);
    return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ProblemDetail> handleResourceNotFound(ResourceNotFoundException ex, HttpServletRequest req) {
    ProblemDetail body = pd(HttpStatus.NOT_FOUND, "No encontrado", ex.getMessage(), req);
    body.setProperty("code", ex.getCode());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
  }

  @ExceptionHandler({ ErrorResponseException.class, Exception.class })
  public ResponseEntity<ProblemDetail> handleFallback(Exception ex, HttpServletRequest req) {
    ProblemDetail body = pd(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno", "Ocurrió un error no controlado", req);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
  }

  @ExceptionHandler(TokenizationRejectedException.class)
  public ResponseEntity<ProblemDetail> handleTokRejected(TokenizationRejectedException ex, HttpServletRequest req) {
    ProblemDetail body = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
    body.setTitle("Tokenización rechazada");
    body.setProperty("path", req.getRequestURI());
    return ResponseEntity.unprocessableEntity().body(body);
  }
}