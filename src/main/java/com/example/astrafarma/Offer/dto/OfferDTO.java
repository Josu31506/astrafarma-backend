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
    private String imageUrl;
    private String mensajeWhatsApp;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private List<Long> productIds;
}
