package com.secretshop.keycloak.DTO;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "dtl", ignoreUnknownFields = false)
@Data
public class DtlProperties {
    private String baseUrl;
    private String kafkaTopic;

    public DtlProperties() {
        System.out.println("DtlProperties created!");
    }
}
