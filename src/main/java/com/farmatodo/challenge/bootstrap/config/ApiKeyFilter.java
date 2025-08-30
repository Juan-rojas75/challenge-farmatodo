package com.farmatodo.challenge.bootstrap.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class ApiKeyFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(ApiKeyFilter.class);
    static final String HEADER = "X-API-Key";
    private final Set<String> validKeys;

    public ApiKeyFilter(SecurityProperties props) {
        this.validKeys = props.getApiKeys().stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toUnmodifiableSet());
        log.debug("API Keys cargadas ({}): {}", validKeys.size(), validKeys);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest req) {
        // Quita el context-path para matchear por ruta real (p.ej. /v1/ping)
        String ctx = req.getContextPath(); // puede ser "/api" o ""
        String path = req.getRequestURI().substring(ctx.length());
        return path.equals("/v1/ping") || path.equals("/actuator/health") || path.startsWith("/public/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        String apiKey = Optional.ofNullable(req.getHeader(HEADER))
                .orElseGet(() -> extractFromAuthorization(req.getHeader("Authorization")));
        if (apiKey != null) apiKey = apiKey.trim();

        if (apiKey != null && validKeys.contains(apiKey)) {
            Authentication auth = new UsernamePasswordAuthenticationToken(apiKey, null, List.of());
            ((UsernamePasswordAuthenticationToken)auth).setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
            SecurityContextHolder.getContext().setAuthentication(auth);
            log.debug("Autenticado por API Key");
        } else {
            log.debug("API Key inválida o ausente. Header={} Authorization={}",
                    req.getHeader(HEADER), req.getHeader("Authorization"));
        }
        chain.doFilter(req, res);
    }

    private String extractFromAuthorization(String h) {
        // Soporta: Authorization: ApiKey <token>
        if (h == null) return null;
        String p = "ApiKey ";
        return h.startsWith(p) ? h.substring(p.length()) : null;
    }
}