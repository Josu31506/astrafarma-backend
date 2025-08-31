package com.example.astrafarma.SupabaseUpload.dto;

import lombok.Data;

@Data
public class UploadResponseDTO {
    private String url;
    private String fileName;

    public UploadResponseDTO(String url, String fileName) {
        this.url = url;
        this.fileName = fileName;
    }
}