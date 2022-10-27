package com.clone.workflow.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.lang.annotation.Documented;
import java.util.List;

@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Document
public class ProductDetails implements Serializable {
    @Id
    @JsonProperty("productId")
    private String productId;
    @JsonProperty("source")
    private String source;
    @JsonProperty("availableRoutes")
    private List<RouteDTO> availableRoutes;
    @JsonProperty("destination")
    private String destination;
    @JsonProperty("containerType")
    private String containerType;
    @JsonProperty("containerSize")
    private Double containerSize;

}
