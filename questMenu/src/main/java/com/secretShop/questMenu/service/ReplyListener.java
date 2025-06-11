package com.secretShop.questMenu.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.secretShop.questMenu.DTO.QuestRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

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

    @KafkaListener(topics = "quest-responses", containerFactory = "kafkaListenerContainerFactory")
    @SuppressWarnings("unchecked")
    public void listen(QuestRequest response) {
        String correlationId = response.getCorrelationId();
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
