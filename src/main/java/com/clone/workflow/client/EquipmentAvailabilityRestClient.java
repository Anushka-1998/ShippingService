package com.clone.workflow.client;


import com.clone.workflow.domain.RouteInfo;
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

    public Mono<Double> retrieveEquipmentAvailability(String source, String containerType)  {

        log.info("I am able to reach EquipmentAvailability webclient");
        var url = UriComponentsBuilder.fromHttpUrl(equipmentAvailabilityUrl)
                .queryParam("source",source)
                .queryParam("containerType",containerType)
                .buildAndExpand().toUriString();
        log.info("EquipmentAvailability url is {}",url);

        return webclient
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(Double.class);
    }



}
