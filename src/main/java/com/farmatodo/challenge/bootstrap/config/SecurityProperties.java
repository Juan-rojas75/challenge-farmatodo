package com.farmatodo.challenge.bootstrap.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "app")
public class SecurityProperties {
    private List<String> apiKeys = new ArrayList<>();
    public List<String> getApiKeys() { return apiKeys; }
    public void setApiKeys(List<String> apiKeys) { this.apiKeys = apiKeys; }
}
