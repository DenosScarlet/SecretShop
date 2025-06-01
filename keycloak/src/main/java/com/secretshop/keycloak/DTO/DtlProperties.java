package com.secretshop.keycloak.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "dtl", ignoreUnknownFields = false)
@Data
@Schema(description = "Конфигурационные свойства для подключения к DTL сервису")
public class DtlProperties {
    @Schema(description = "Базовый URL DTL сервиса", example = "http://localhost:8580")
    private String baseUrl;

    @Schema(description = "Название Kafka топика", example = "user-events")
    private String kafkaTopic;
    public DtlProperties() {
        System.out.println("DtlProperties created!");
    }
}
