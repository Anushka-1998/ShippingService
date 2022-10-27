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
public class ContainerDTO {
    @JsonProperty("id")
    private String id;
    @JsonProperty("source")
    private String source;
    @JsonProperty("noOfContainers")
    private double noOfContainers;
    @JsonProperty("containerType")
    private String containerType;


}


