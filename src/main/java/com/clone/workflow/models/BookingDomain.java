package com.clone.workflow.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookingDomain {
    private Filter filter;
    private Rule rule;

}
