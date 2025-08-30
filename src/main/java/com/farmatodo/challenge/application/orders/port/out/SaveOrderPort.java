package com.farmatodo.challenge.application.orders.port.out;

import com.farmatodo.challenge.domain.orders.model.Order;
public interface SaveOrderPort { Order save(Order o); }