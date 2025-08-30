package com.farmatodo.challenge.shared.logging;

import org.slf4j.MDC;
import java.util.UUID;

public final class TxContext {
  private static final String KEY = "txId";
  private TxContext(){}
  public static String start(){ var id = UUID.randomUUID().toString(); MDC.put(KEY, id); return id; }
  public static void set(String id){ MDC.put(KEY, id); }
  public static String get(){ return MDC.get(KEY); }
  public static void clear(){ MDC.remove(KEY); }
}