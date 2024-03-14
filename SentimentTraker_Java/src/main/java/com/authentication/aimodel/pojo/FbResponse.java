package com.authentication.aimodel.pojo;

import lombok.Data;

@Data
public class FbResponse {
    private String from;
    private String message;
    private String id;
    private String createdTime;
}
