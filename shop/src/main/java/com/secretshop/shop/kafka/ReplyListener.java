package com.secretshop.shop.kafka;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.secretshop.shop.DTO.ShopRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReplyListener {
    private final Map<String, CallbackEntry<?>> callbacks = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;

    public <T> void registerCallback(String correlationId, CompletableFuture<T> future, Class<T> type) {
        callbacks.put(correlationId, new CallbackEntry<>(future, type));
        log.debug("Registered callback for correlationId: {}", correlationId);
    }

    @KafkaListener(topics = "shop-responses-list", containerFactory = "listListenerFactory")
    public void listenList(ConsumerRecord<String, Object> record) {
        Object rawResponses = record.value();
        if (rawResponses == null) {
            log.warn("Received null response list for correlationId: {}", record.key());
            return;
        }

        try {
            List<ShopRequest> responses = objectMapper.convertValue(rawResponses, new TypeReference<List<ShopRequest>>() {});
            if (responses.isEmpty()) {
                log.warn("Received empty response list for correlationId: {}", record.key());
                return;
            }

            for (ShopRequest response : responses) {
                processResponse(response);
            }
        } catch (Exception e) {
            log.error("Failed to deserialize response list for correlationId: {}", record.key(), e);
        }
    }

    @KafkaListener(topics = "shop-responses-single", containerFactory = "singleListenerFactory")
    public void listenSingle(ConsumerRecord<String, ShopRequest> record) {
        ShopRequest response = record.value();
        if (response == null) {
            log.warn("Received null single response for correlationId: {}", record.key());
            return;
        }
        processResponse(response);
    }

    private void processResponse(ShopRequest response) {
        String correlationId = response.getCorrelationId();
        if (correlationId == null) {
            log.warn("Response missing correlationId: {}", response);
            return;
        }

        log.debug("Received response for correlationId: {}", correlationId);
        CallbackEntry<?> entry = callbacks.remove(correlationId);
        if (entry != null) {
            try {
                processEntry(entry, response.getPayload());
                log.debug("Successfully processed response for correlationId: {}", correlationId);
            } catch (Exception e) {
                log.error("Error processing response for correlationId: {}", correlationId, e);
                entry.future.completeExceptionally(e);
            }
        } else {
            log.warn("No callback found for correlationId: {}", correlationId);
        }
    }

    private <T> void processEntry(CallbackEntry<T> entry, Object payload) {
        try {
            // Обработка ErrorResponse
            Map<String, String> map = null;
            if (payload instanceof Map) {
                map = (Map<String, String>) payload;
                if (map.containsKey("error")) {
                    entry.future.completeExceptionally(new RuntimeException(map.get("error")));
                    return;
                }
            }
            // Обработка SuccessResponse
            if (entry.type == String.class && map != null && map.containsKey("message")) {
                entry.future.complete((T) map.get("message"));
                return;
            }
            // Обычная десериализация
            T converted = objectMapper.convertValue(payload, entry.type);
            entry.future.complete(converted);
        } catch (Exception e) {
            log.error("Error converting payload to type: {}", entry.type.getSimpleName(), e);
            entry.future.completeExceptionally(e);
        }
    }

    private record CallbackEntry<T>(CompletableFuture<T> future, Class<T> type) {
    }
}