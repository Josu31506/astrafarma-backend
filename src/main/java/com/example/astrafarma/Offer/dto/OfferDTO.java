package com.example.astrafarma.Offer.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class OfferDTO {
    private Long id;
    private String title;
    private String description;
    private String mensajeWhatsApp;
    private String imageUrl;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private List<String> productNames;
    private List<ProductDiscountDTO> discounts;
}
