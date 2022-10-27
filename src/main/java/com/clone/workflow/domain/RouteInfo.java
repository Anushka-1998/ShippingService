package com.clone.workflow.domain;

import lombok.*;


import java.util.List;

@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class RouteInfo {

    private String routeId;

    private String source;

    private String destination;

    private List<RouteDTO> routeList;


}
