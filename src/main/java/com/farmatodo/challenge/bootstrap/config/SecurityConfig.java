package com.farmatodo.challenge.bootstrap.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@EnableConfigurationProperties(SecurityProperties.class)
@Configuration
public class SecurityConfig {

    private final JsonAuthHandlers jsonAuthHandlers;

    private final ApiKeyFilter apiKeyFilter;
    public SecurityConfig(ApiKeyFilter apiKeyFilter, JsonAuthHandlers jsonAuthHandlers) { this.apiKeyFilter = apiKeyFilter; this.jsonAuthHandlers = jsonAuthHandlers; }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/v1/ping*").permitAll()
                .requestMatchers("/actuator/health").permitAll()
                .requestMatchers("/v3/api-docs/**").permitAll()
                .requestMatchers("/swagger-ui.html").permitAll()
                .requestMatchers("/swagger-ui/**").permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(apiKeyFilter, UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(jsonAuthHandlers)    // 401 JSON
                .accessDeniedHandler(jsonAuthHandlers)         // 403 JSON
            )
            .build();
    }
}