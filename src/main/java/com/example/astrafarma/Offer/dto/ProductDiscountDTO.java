package com.example.astrafarma.Offer.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class ProductDiscountDTO {
    private String productName;
    private int discountPercentage;
    private BigDecimal discountedPrice;
}
