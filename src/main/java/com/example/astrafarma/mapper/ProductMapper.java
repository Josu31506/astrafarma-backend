// src/main/java/com/example/astrafarma/mapper/ProductMapper.java
package com.example.astrafarma.mapper;

import com.example.astrafarma.Product.domain.Product;
import com.example.astrafarma.Product.dto.ProductDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductDTO toDto(Product entity);
    Product toEntity(ProductDTO dto);
}
