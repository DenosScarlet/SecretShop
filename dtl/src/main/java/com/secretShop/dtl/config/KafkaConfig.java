package com.secretShop.dtl.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.secretShop.dtl.DTO.QuestRequest;
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
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
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

    @Bean
    public ProducerFactory<String, QuestRequest> producerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.secretShop.dtl.DTO, com.secretShop.questMenu.DTO");

        JsonSerializer<QuestRequest> jsonSerializer = new JsonSerializer<>(objectMapper());

        return new DefaultKafkaProducerFactory<>(props, new StringSerializer(), jsonSerializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, QuestRequest> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, QuestRequest> factory = new ConcurrentKafkaListenerContainerFactory<>();
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "${spring.kafka.consumer.group-id}");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, "com.secretShop.dtl.DTO.QuestRequest");

        JsonDeserializer<QuestRequest> jsonDeserializer = new JsonDeserializer<>(QuestRequest.class, objectMapper());

        factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), jsonDeserializer));
        return factory;
    }

    @Bean
    public KafkaTemplate<String, QuestRequest> kafkaTemplate() {
        KafkaTemplate<String, QuestRequest> template = new KafkaTemplate<>(producerFactory());
        template.setMessageConverter(new org.springframework.kafka.support.converter.JsonMessageConverter(objectMapper()));
        return template;
    }

    @Bean
    public NewTopic requestTopic() { return new NewTopic("quest-requests", 1, (short)1); }
    @Bean
    public NewTopic responseTopic() { return new NewTopic("quest-responses", 1, (short)1); }
}