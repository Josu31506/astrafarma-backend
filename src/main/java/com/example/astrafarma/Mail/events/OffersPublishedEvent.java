package com.example.astrafarma.Mail.events;

import java.util.List;
import com.example.astrafarma.Offer.dto.ProductDiscountDTO;

public class OffersPublishedEvent {
    private final List<Long> offerIds;
    private final List<ProductDiscountDTO> discounts;

    public OffersPublishedEvent(List<Long> offerIds, List<ProductDiscountDTO> discounts) {
        this.offerIds = offerIds;
        this.discounts = discounts;
    }

    public List<Long> getOfferIds() {
        return offerIds;
    }

    public List<ProductDiscountDTO> getDiscounts() {
        return discounts;
    }
}