package com.example.astrafarma.mapper;

import com.example.astrafarma.domain.Product;
import com.example.astrafarma.dto.ProductDTO;
import java.math.BigDecimal;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-17T19:25:38-0500",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.6 (Microsoft)"
)
@Component
public class ProductMapperImpl implements ProductMapper {

    @Override
    public ProductDTO toDto(Product entity) {
        if ( entity == null ) {
            return null;
        }

        ProductDTO productDTO = new ProductDTO();

        productDTO.setId( entity.getId() );
        productDTO.setName( entity.getName() );
        productDTO.setDescription( entity.getDescription() );
        if ( entity.getPrice() != null ) {
            productDTO.setPrice( entity.getPrice().doubleValue() );
        }
        productDTO.setImageUrl( entity.getImageUrl() );

        return productDTO;
    }

    @Override
    public Product toEntity(ProductDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Product product = new Product();

        product.setId( dto.getId() );
        product.setName( dto.getName() );
        product.setDescription( dto.getDescription() );
        if ( dto.getPrice() != null ) {
            product.setPrice( BigDecimal.valueOf( dto.getPrice() ) );
        }
        product.setImageUrl( dto.getImageUrl() );

        return product;
    }
}
