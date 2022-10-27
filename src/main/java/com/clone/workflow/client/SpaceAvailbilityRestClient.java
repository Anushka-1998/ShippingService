package com.clone.workflow.client;


import com.clone.workflow.domain.RouteDTO;
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

    public Flux<RouteDTO> retrieveSpaceAvailability(List<RouteDTO> routeDTOList,Double noOfContainers)  {

        log.info("I am able to reach before retrieveSpaceAvailability webclient");
        var url = UriComponentsBuilder.fromHttpUrl(spaceAvailabilityUrl)
                .queryParam("noOfContainers",noOfContainers)
                .buildAndExpand().toUriString();
        log.info("spaceAvailabilityUrl is {}",url);
        
        return webclient
                .post()
                .uri(url)
                .body(Mono.just(routeDTOList), RouteDTO.class)
                .retrieve()
                .bodyToFlux(RouteDTO.class);
    }



}
