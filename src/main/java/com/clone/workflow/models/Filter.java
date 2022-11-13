package com.clone.workflow.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@AllArgsConstructor
public class Filter {

    private String product;
    @JsonAlias(value = "domain-name")
    private String domainName;
    @JsonAlias(value = "cross-border")
    private String crossBorder;


}
