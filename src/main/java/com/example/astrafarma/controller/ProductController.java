package com.example.astrafarma.controller;

import com.example.astrafarma.dto.ProductDTO;
import com.example.astrafarma.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    @PostMapping
    public ResponseEntity<ProductDTO> create(@Valid @RequestBody ProductDTO dto) {
        ProductDTO savedDto = service.create(dto);
        return ResponseEntity.ok(savedDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/search")
    public List<ProductDTO> searchByName(@RequestParam String name) {
        return service.findByName(name);
    }
}
