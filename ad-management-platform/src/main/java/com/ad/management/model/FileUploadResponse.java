package com.ad.management.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadResponse {
    private Boolean success;
    private String url;
    private String message;
}