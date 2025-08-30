package com.farmatodo.challenge.bootstrap.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({ SecurityProperties.class, TokenizationProperties.class , ProductsProperties.class, PaymentsProperties.class})
public class AppConfig {}