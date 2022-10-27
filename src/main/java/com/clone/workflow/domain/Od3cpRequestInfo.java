package com.clone.workflow.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;


@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Od3cpRequestInfo implements Serializable {
    @JsonProperty("requestId")
    private String requestId;
    @JsonProperty("source")
    private String source;
    @JsonProperty("srcServiceMode")
    private String srcServiceMode;
    @JsonProperty("destination")
    private String destination;
    @JsonProperty("destServiceMode")
    private String destServiceMode;
    @JsonProperty("commodityType")
    private String commodityType;
    @JsonProperty("containerType")
    private String containerType;
    @JsonProperty("containerSize")
    private double containerSize;
    @JsonProperty("noOfContainers")
    private double noOfContainers;
    @JsonProperty("cargoWeight")
    private double cargoWeight;
}