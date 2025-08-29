package com.farmatodo.challenge.application.logs.port.out;

public interface EventLoggerPort {
  void log(String type, String txId, String payloadJson);
}
