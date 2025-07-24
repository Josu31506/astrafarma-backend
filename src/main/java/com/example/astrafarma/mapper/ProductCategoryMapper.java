package com.example.astrafarma.mapper;


import com.example.astrafarma.domain.ProductCategory;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

@Component
public class ProductCategoryMapper {

    @Named("stringToCategory")
    public ProductCategory toCategory(String value) {
        return value == null ? null : ProductCategory.valueOf(value.toUpperCase());
    }

    @Named("categoryToString")
    public String fromCategory(ProductCategory category) {
        return category == null ? null : category.name();
    }
}
