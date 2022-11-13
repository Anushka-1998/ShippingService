package com.clone.workflow.models;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@AllArgsConstructor
public class Task {

    private String taskName;
    private String listenTo;
    private String publish;
    private String publishFailed;
}
