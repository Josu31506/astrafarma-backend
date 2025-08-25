package com.example.astrafarma.Product.controller;

import com.example.astrafarma.Product.dto.ProductDTO;
import com.example.astrafarma.Product.domain.ProductService;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * Controlador REST para gestionar productos de AstraFarma.
 * Habilita CORS para que el frontend en http://localhost:5173
 * pueda realizar peticiones GET, POST, DELETE y la preflight OPTIONS,
 * enviando cabeceras Content-Type y Authorization.
 */
@CrossOrigin(
    origins = "http://localhost:5173",
    allowedHeaders = {"*"},
    methods = {
      RequestMethod.GET,
      RequestMethod.POST,
      RequestMethod.DELETE,
      RequestMethod.OPTIONS
    }
)
@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService service;

    /**
     * Lista todos los productos.
     */
    @GetMapping
    public List<ProductDTO> listAll() {
        return service.listAll();
    }

    /**
     * Crea un nuevo producto a partir del DTO en el body.
     */
    @PostMapping
    public ResponseEntity<ProductDTO> create(@Valid @RequestBody ProductDTO dto) {
        ProductDTO created = service.create(dto);
        return ResponseEntity.ok(created);
    }

    /**
     * Elimina el producto con el ID dado.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Busca productos por nombre (consulta: /api/products/search?name=xyz).
     */
    @GetMapping("/search")
    public List<ProductDTO> searchByName(@RequestParam String name) {
        return service.findByName(name);
    }
}
