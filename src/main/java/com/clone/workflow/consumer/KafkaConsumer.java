package com.clone.workflow.consumer;

import com.clone.workflow.domain.Od3cpRequestInfo;
import com.clone.workflow.service.ShippingService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.client.WorkflowStub;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumer {

    private final WorkflowClient client;
    ObjectMapper mapper = new ObjectMapper();


    @KafkaListener(topics = "consumer-topic-1", containerFactory = "kafkaListenerContainerFactory")
    public void consumeEvents(@Payload String payload,
                              @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key,
                              @Header(KafkaHeaders.RECEIVED_TIMESTAMP) Long createdTimestamp,
                              Acknowledgment acknowledgment) throws JsonProcessingException {

        log.info("key in final kafka topic: {}", key);
        log.info("payload in final kafka topic: {}", payload);

        var request = mapper.readValue(payload, Od3cpRequestInfo.class);
        acknowledgment.acknowledge();
        WorkflowStub untypedWorkflowStub = client.newUntypedWorkflowStub("save Request",
                WorkflowOptions.newBuilder()
                        .setWorkflowId("Order_" + request.getRequestId())
                        .setTaskQueue("SAVE-PRODUCT-QUEUE")
                        .build());
        if (request.getSource().equalsIgnoreCase("CN")) {
            untypedWorkflowStub.signalWithStart("startKafkaEvent",  new Object[]{request}, new Object[]{request});

        } else {
            untypedWorkflowStub.signalWithStart("resumeKafkaEvent", new Object[]{request}, new Object[]{request})
            ;
        }

    }
}
