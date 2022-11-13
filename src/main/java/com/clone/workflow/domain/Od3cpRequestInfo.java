package com.clone.workflow.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.annotation.Id;

import java.io.Serializable;


@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties
public class Od3cpRequestInfo implements Serializable {
    @JsonProperty("requestId")
    @Id
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

    @JsonProperty("status")
    private String status= "active";

}