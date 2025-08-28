package com.example.astrafarma.Product.domain;

import com.example.astrafarma.Product.domain.Product;
import com.example.astrafarma.Product.dto.ProductDTO;
import com.example.astrafarma.exception.ResourceNotFoundException;
import com.example.astrafarma.mapper.ProductMapper;
import com.example.astrafarma.Product.repository.ProductRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;


import org.springframework.data.jpa.domain.Specification;
import java.math.BigDecimal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductMapper mapper;


    public ProductDTO getById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
        return mapper.toDto(product);
    }

    public List<ProductDTO> listAll() {
        return productRepository.findAll()
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    public Page<ProductDTO> listPaged(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(mapper::toDto);
    }

    public ProductDTO create(ProductDTO dto) {
        Product entity = mapper.toEntity(dto);
        System.out.println("Entidad guardada: " + entity);
        Product saved = productRepository.save(entity);
        ProductDTO result = mapper.toDto(saved);
        System.out.println("DTO devuelto: " + result);
        return result;
    }

    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Producto no encontrado con id: " + id);
        }
        productRepository.deleteById(id);
    }

    public List<ProductDTO> findByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    public ProductDTO updateById(Long id, ProductDTO dto) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + id));
        updateEntityFields(existing, dto);

        Product saved = productRepository.save(existing);
        return mapper.toDto(saved);
    }

    public ProductDTO updateByName(ProductDTO dto) {
        Product existing = productRepository.findByName(dto.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con nombre: " + dto.getName()));

        updateEntityFields(existing, dto);

        Product saved = productRepository.save(existing);
        return mapper.toDto(saved);
    }

    private void updateEntityFields(Product entity, ProductDTO dto) {
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setPrice(dto.getPrice());
        entity.setImageUrl(dto.getImageUrl());
        entity.setCategory(dto.getCategory());
    }

    public Page<ProductDTO> filterProducts(
            String query,
            ProductCategory category,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Pageable pageable
    ) {
        Specification<Product> spec = (root, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (query != null && !query.trim().isEmpty()) {
                String likePattern = "%" + query.trim().toLowerCase() + "%";
                predicates.add(
                        cb.or(
                                cb.like(cb.lower(root.get("name")), likePattern),
                                cb.like(cb.lower(root.get("description")), likePattern)
                        )
                );
            }
            if (category != null) {
                predicates.add(cb.equal(root.get("category"), category));
            }
            if (minPrice != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), minPrice));
            }
            if (maxPrice != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), maxPrice));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return productRepository.findAll(spec, pageable)
                .map(mapper::toDto);
    }
}
