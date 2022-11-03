package com.clone.workflow.client;

import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class EquipmentAvailabilityRestClient {

    @Autowired
    private WebClient webclient;

    @Value("${restClient.equipmentAvailabilityUrl}")
    private String equipmentAvailabilityUrl;

    /**
     * This method calls EquipmentAvailabilityService using webClient call
     * @param source
     * @param containerType
     * @return
     */
    public Mono<Double> retrieveEquipmentAvailability(String source, String containerType)  {
        log.info("Inside retrieveEquipmentAvailability");
        var url = UriComponentsBuilder.fromHttpUrl(equipmentAvailabilityUrl)
                .queryParam("source",source)
                .queryParam("containerType",containerType)
                .buildAndExpand().toUriString();
        log.info("EquipmentAvailability URL is {}",url);

        return webclient
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(Double.class)
                .onErrorMap(error -> {
                    throw new RuntimeException("Exception caught while calling equipment availibility service due to "+error.getMessage());
                });
    }
}
