package com.farmatodo.challenge.adapters.in.web.customers.response;

import java.util.UUID;
public record CustomerResponse(UUID id, String name, String email, String phone, String address) {}