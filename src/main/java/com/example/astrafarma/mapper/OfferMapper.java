package com.example.astrafarma.mapper;

import com.example.astrafarma.Offer.domain.Offer;
import com.example.astrafarma.Offer.dto.OfferDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface OfferMapper {
    OfferMapper INSTANCE = Mappers.getMapper(OfferMapper.class);

    @Mapping(target = "productIds", source = "products", qualifiedByName = "productsToIds")
    OfferDTO offerToOfferDTO(Offer offer);

    @Mapping(target = "products", ignore = true)
    Offer offerDTOToOffer(OfferDTO dto);

    @Named("productsToIds")
    default List<Long> productsToIds(List<com.example.astrafarma.Product.domain.Product> products) {
        return products == null ? null : products.stream().map(com.example.astrafarma.Product.domain.Product::getId).collect(Collectors.toList());
    }
}