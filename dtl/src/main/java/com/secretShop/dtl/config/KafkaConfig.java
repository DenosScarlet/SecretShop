package com.secretShop.dtl.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.secretShop.dtl.DTO.QuestRequest;
import com.secretShop.dtl.DTO.ShopRequest;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.registerModule(new Jdk8Module());
        return mapper;
    }

    // ProducerFactory и KafkaTemplate для QuestRequest
    @Bean
    public ProducerFactory<String, QuestRequest> questProducerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.secretShop.dtl.DTO,com.secretShop.questMenu.DTO");
        return new DefaultKafkaProducerFactory<>(props, new StringSerializer(), new JsonSerializer<>(objectMapper()));
    }

    @Bean
    public KafkaTemplate<String, QuestRequest> questKafkaTemplate() {
        KafkaTemplate<String, QuestRequest> template = new KafkaTemplate<>(questProducerFactory());
        return template;
    }

    // ProducerFactory и KafkaTemplate для ShopRequest (одиночный)
    @Bean
    public ProducerFactory<String, ShopRequest> singleProducerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.secretShop.dtl.DTO,com.secretshop.shop.DTO");
        return new DefaultKafkaProducerFactory<>(props, new StringSerializer(), new JsonSerializer<>(objectMapper()));
    }

    @Bean
    public KafkaTemplate<String, ShopRequest> singleKafkaTemplate() {
        KafkaTemplate<String, ShopRequest> template = new KafkaTemplate<>(singleProducerFactory());
        return template;
    }

    // ProducerFactory и KafkaTemplate для List<ShopRequest>
    @Bean
    public ProducerFactory<String, List<ShopRequest>> listProducerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.secretShop.dtl.DTO,com.secretshop.shop.DTO");
        return new DefaultKafkaProducerFactory<>(props, new StringSerializer(), new JsonSerializer<>(objectMapper()));
    }

    @Bean
    public KafkaTemplate<String, List<ShopRequest>> listKafkaTemplate() {
        KafkaTemplate<String, List<ShopRequest>> template = new KafkaTemplate<>(listProducerFactory());
        return template;
    }

    // ConsumerFactory и ListenerFactory для QuestRequest
    @Bean
    public ConsumerFactory<String, QuestRequest> questConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "${spring.kafka.consumer.group-id}");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.secretShop.dtl.DTO,com.secretShop.questMenu.DTO");
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, "com.secretShop.dtl.DTO.QuestRequest");
        props.put(JsonDeserializer.TYPE_MAPPINGS, "QuestRequest:com.secretShop.dtl.DTO.QuestRequest");
        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), new JsonDeserializer<>(QuestRequest.class, objectMapper()));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, QuestRequest> questKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, QuestRequest> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(questConsumerFactory());
        return factory;
    }

    // ConsumerFactory и ListenerFactory для ShopRequest
    @Bean
    public ConsumerFactory<String, ShopRequest> shopConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "dtl-shop-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.secretShop.dtl.DTO,com.secretshop.shop.DTO");
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, "com.secretShop.dtl.DTO.ShopRequest");
        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), new JsonDeserializer<>(objectMapper()));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ShopRequest> shopKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ShopRequest> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(shopConsumerFactory());
        return factory;
    }

    // Топики для QuestRequest
    @Bean
    public NewTopic questRequestTopic() {
        return new NewTopic("quest-requests", 3, (short) 1);
    }

    @Bean
    public NewTopic questResponseTopic() {
        return new NewTopic("quest-responses", 3, (short) 1);
    }

    // Топики для ShopRequest
    @Bean
    public NewTopic shopRequestTopic() {
        return new NewTopic("shop-requests", 3, (short) 1);
    }

    @Bean
    public NewTopic shopResponsesListTopic() {
        return new NewTopic("shop-responses-list", 3, (short) 1);
    }

    @Bean
    public NewTopic shopResponsesSingleTopic() {
        return new NewTopic("shop-responses-single", 3, (short) 1);
    }
}