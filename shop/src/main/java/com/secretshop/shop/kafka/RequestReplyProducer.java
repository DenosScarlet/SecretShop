package com.secretshop.shop.kafka;

import com.secretshop.shop.DTO.ShopRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestReplyProducer {
    private final KafkaTemplate<String, ShopRequest> kafkaTemplate;
    private final ReplyListener replyListener;

    public <T> CompletableFuture<T> sendAndReceive(ShopRequest request, Class<T> responseType) {
        String correlationId = UUID.randomUUID().toString();
        request.setCorrelationId(correlationId);

        CompletableFuture<T> future = new CompletableFuture<>();
        replyListener.registerCallback(correlationId, future, responseType);

        try {
            CompletableFuture<SendResult<String, ShopRequest>> sendFuture =
                    kafkaTemplate.send("shop-requests", correlationId, request);

            sendFuture.whenComplete((result, ex) -> {
                if (ex != null) {
                    log.error("Failed to send message with correlationId: {}", correlationId, ex);
                    future.completeExceptionally(ex);
                } else {
                    log.debug("Message sent successfully with correlationId: {}", correlationId);
                }
            });
        } catch (Exception e) {
            log.error("Exception while sending message with correlationId: {}", correlationId, e);
            future.completeExceptionally(e);
        }

        return future;
    }
}