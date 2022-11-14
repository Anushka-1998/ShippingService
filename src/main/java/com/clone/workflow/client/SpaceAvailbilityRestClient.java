package com.clone.workflow.client;


import com.clone.workflow.domain.RouteDTO;
import com.clone.workflow.exception.ExternalServiceCallException;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@Slf4j
public class SpaceAvailbilityRestClient {

    @Autowired
    private WebClient webclient;

    @Value("${restClient.spaceAvailabilityUrl}")
    private String spaceAvailabilityUrl;

    /**
     * This method calls SpaceAvailability service using webClient call
     * @param routeDTOList
     * @param noOfContainers
     * @return
     */
    public Flux<RouteDTO> retrieveSpaceAvailability(List<RouteDTO> routeDTOList,Double noOfContainers)  {
        log.info("Inside retrieveSpaceAvailability()");
        var url = UriComponentsBuilder.fromHttpUrl(spaceAvailabilityUrl)
                .queryParam("noOfContainers",noOfContainers)
                .buildAndExpand().toUriString();
        log.info("spaceAvailability URL is : {}",url);
        
        return webclient
                .post()
                .uri(url)
                .body(Mono.just(routeDTOList), RouteDTO.class)
                .retrieve()
                .bodyToFlux(RouteDTO.class)
                .onErrorMap(error -> {
                    throw new ExternalServiceCallException("Exception caught while calling space availibility service due to "+error.getMessage());
                });
    }
}
