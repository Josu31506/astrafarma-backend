package com.example.astrafarma.mapper;

import com.example.astrafarma.Offer.domain.Offer;
import com.example.astrafarma.Offer.domain.OfferProductDiscount;
import com.example.astrafarma.Offer.dto.OfferDTO;
import com.example.astrafarma.Offer.dto.ProductDiscountDTO;
import com.example.astrafarma.Product.domain.Product;
import org.mapstruct.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class OfferMapper {

    @Mapping(target = "productNames", source = "products", qualifiedByName = "productsToNames")
    @Mapping(target = "discounts", source = "discounts", qualifiedByName = "discountsToDTOs")
    public abstract OfferDTO offerToOfferDTO(Offer offer);

    @Mapping(target = "products", ignore = true)
    @Mapping(target = "discounts", ignore = true)
    public abstract Offer offerDTOToOffer(OfferDTO dto);

    @Named("productsToNames")
    public List<String> productsToNames(List<Product> products) {
        return products == null ? null : products.stream().map(Product::getName).collect(Collectors.toList());
    }

    @Named("discountsToDTOs")
    public List<ProductDiscountDTO> discountsToDTOs(List<OfferProductDiscount> discounts) {
        if (discounts == null) return new ArrayList<>();
        return discounts.stream().map(d -> {
            ProductDiscountDTO dto = new ProductDiscountDTO();
            dto.setProductName(d.getProduct().getName());
            dto.setDiscountPercentage(d.getDiscountPercentage());

            BigDecimal price = d.getProduct().getPrice();
            BigDecimal discountFraction = BigDecimal.valueOf(d.getDiscountPercentage())
                    .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
            BigDecimal discounted = price.subtract(price.multiply(discountFraction));
            dto.setDiscountedPrice(discounted);
            return dto;
        }).collect(Collectors.toList());
    }
}