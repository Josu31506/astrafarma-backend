package com.example.astrafarma.Product.controller;

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

    @GetMapping("/search")
    public List<ProductDTO> searchByName(@RequestParam String name) {
        return service.findByName(name);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ProductDTO> create(@Valid @RequestBody ProductDTO dto) {
        ProductDTO savedDto = service.create(dto);
        return ResponseEntity.ok(savedDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateById(
            @PathVariable Long id,
            @Valid @RequestBody ProductDTO dto) {

        ProductDTO updated = service.updateById(id, dto);
        return ResponseEntity.ok(updated);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/by-name")
    public ResponseEntity<ProductDTO> updateByName(@Valid @RequestBody ProductDTO dto) {
        ProductDTO updated = service.updateByName(dto);
        return ResponseEntity.ok(updated);
    }
}
