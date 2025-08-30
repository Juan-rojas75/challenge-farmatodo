package com.farmatodo.challenge.adapters.in.web.tokenization.response;


import java.time.Instant;
import java.util.UUID;

public record TokenResponse(UUID txId, String token, Instant createdAt) {}