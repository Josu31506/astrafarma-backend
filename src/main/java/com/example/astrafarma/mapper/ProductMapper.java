// src/main/java/com/example/astrafarma/mapper/ProductMapper.java
package com.example.astrafarma.mapper;

import com.example.astrafarma.domain.Product;
import com.example.astrafarma.dto.ProductDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = ProductCategoryMapper.class)
public interface ProductMapper {
    @Mapping(source = "category", target = "category", qualifiedByName = "categoryToString")
    ProductDTO toDto(Product entity);

    @Mapping(source = "category", target = "category", qualifiedByName = "stringToCategory")
    Product toEntity(ProductDTO dto);
}
