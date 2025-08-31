package com.example.astrafarma.Product.controller;

import com.example.astrafarma.Product.domain.ProductCategory;
import com.example.astrafarma.Product.dto.ProductDTO;
import com.example.astrafarma.Product.domain.ProductService;
import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService service;

    @GetMapping
    public List<ProductDTO> listAll() {
        return service.listAll();
    }

    @GetMapping("/paged")
    public Page<ProductDTO> listPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return service.listPaged(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getById(@PathVariable Long id) {
        ProductDTO dto = service.getById(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/filter")
    public Page<ProductDTO> filterProducts(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) ProductCategory category,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) List<Long> ids,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return service.filterProducts(query, category, minPrice, maxPrice, ids, pageable);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<ProductDTO> createWithImage(
            @RequestPart("data") ProductDTO dto,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) throws Exception {
        ProductDTO savedDto = service.create(dto, image);
        return ResponseEntity.ok(savedDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<ProductDTO> updateWithImage(
            @PathVariable Long id,
            @RequestPart("data") ProductDTO dto,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) throws Exception {
        ProductDTO updated = service.updateById(id, dto, image);
        return ResponseEntity.ok(updated);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/by-name")
    public ResponseEntity<ProductDTO> updateByName(@Valid @RequestBody ProductDTO dto) {
        ProductDTO updated = service.updateByName(dto);
        return ResponseEntity.ok(updated);
    }
}
