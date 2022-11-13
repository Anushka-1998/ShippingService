package com.clone.workflow.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.netty.util.internal.StringUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ContainerProperties;

import java.util.HashMap;
import java.util.Map;

@Getter
@RequiredArgsConstructor
@Configuration
public class KafkaConfig {

    private static final String SECURITY_PROTOCOL = "security.protocol";
    private static final String SASL_MECHANISM = "sasl.mechanism";
    private static final String SASL_JAAS_CONFIG = "sasl.jaas.config";
    private static final String SASL_TRUSTSTORE_LOCATION = "ssl.truststore.location";
    private static final String SASL_TRUSTSTORE_PWORD = "ssl.truststore.password";

    @Value("${spring.kafka.streams.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.streams.username:}")
    private String username;

    @Value("${spring.kafka.streams.password:}")
    private String password;

    @Value("${spring.kafka.streams.login-module}")
    private String loginModule;

    @Value("${spring.kafka.streams.sasl-mechanism}")
    private String saslMechanism;

    @Value("${spring.kafka.streams.security-protocol}")
    private String securityProtocol;

    @Value("${spring.kafka.streams.truststore-location:}")
    private String truststoreLocation;

    @Value("${spring.kafka.streams.truststore-password:}")
    private String truststorePassword;

    @Value("${spring.kafka.streams.consumer.consumer-group}")
    private String consumerGroup;

    @Value("${spring.kafka.streams.consumer.concurrency}")
    private int consumerConcurrency;

    @Value("${spring.kafka.streams.consumer.max-poll-interval}")
    private int maxPollInterval;

    @Value("${spring.kafka.streams.consumer.max-poll-records}")
    private int maxPollRecords;

    @Value("${spring.kafka.streams.producer.acks-config:all}")
    private String producerAcksConfig;

    @Value("${spring.kafka.streams.producer.linger:1}")
    private int producerLinger;

    @Value("${spring.kafka.streams.producer.timeout:30000}")
    private int producerRequestTimeout;

    @Value("${spring.kafka.streams..producer.batch-size:16384}")
    private int producerBatchSize;

    @Value("${spring.kafka.streams.producer.client-id}")
    private String kafkaClientId;

    @Value("${spring.kafka.streams.producer.idle-connection-timeout:180000}")
    private String idleConnectionTimeout;

    @Value(value = "${spring.kafka.streams.consumer.topic}")
    private String consumerTopic;


    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }


    @Bean
    public Map<String, Object> setConsumerProperties() {
        Map<String, Object> properties = new HashMap<>();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092");
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroup);
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        properties.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords);
        properties.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, maxPollInterval);
        properties.put(ConsumerConfig.ALLOW_AUTO_CREATE_TOPICS_CONFIG, false);
        addSaslProperties(properties, "PLAIN", "SASL_SSL", "org.apache.kafka.common.security.plain.PlainLoginModule",
                "", "");
        addTruststoreProperties(properties, "", "");
        return properties;
    }


    @Bean
    public ProducerFactory<String, String> producerFactory() {
        return new DefaultKafkaProducerFactory<>(setProducerProperties());
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(setConsumerProperties()));
        factory.setConcurrency(consumerConcurrency);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        return factory;
    }

    private Map<String, Object> setProducerProperties() {
        Map<String, Object> properties = new HashMap<>();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.LINGER_MS_CONFIG, producerLinger);
        properties.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, producerRequestTimeout);
        properties.put(ProducerConfig.BATCH_SIZE_CONFIG, producerBatchSize);
        properties.put(ProducerConfig.ACKS_CONFIG, producerAcksConfig);
        properties.put(ProducerConfig.CLIENT_ID_CONFIG, kafkaClientId);
        properties.put(ProducerConfig.CONNECTIONS_MAX_IDLE_MS_CONFIG, idleConnectionTimeout);
        addSaslProperties(properties, saslMechanism, securityProtocol, loginModule, username, password);
        addTruststoreProperties(properties, truststoreLocation, truststorePassword);
        return properties;
    }

    public static void addSaslProperties(Map<String, Object> properties, String saslMechanism, String securityProtocol, String loginModule, String username, String password) {
        if (!StringUtil.isNullOrEmpty(username)) {
            properties.put(SECURITY_PROTOCOL, securityProtocol);
            properties.put(SASL_MECHANISM, saslMechanism);
            properties.put(SASL_JAAS_CONFIG, String.format("%s required username=\"%s\" password=\"%s\" ;", loginModule, username, password));
        }
    }

    private static void addTruststoreProperties(Map<String, Object> properties, String location, String password) {
        if (!StringUtil.isNullOrEmpty(location)) {
            properties.put(SASL_TRUSTSTORE_LOCATION, location);
            properties.put(SASL_TRUSTSTORE_PWORD, password);
        }
    }

}
