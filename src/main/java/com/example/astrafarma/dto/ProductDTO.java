package com.example.astrafarma.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class ProductDTO {
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @NotNull @Positive
    private Double price;

    @NotBlank
    private String imageUrl;
}
