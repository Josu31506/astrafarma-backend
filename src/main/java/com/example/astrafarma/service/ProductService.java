// src/main/java/com/example/astrafarma/service/ProductService.java
package com.example.astrafarma.service;

import com.example.astrafarma.domain.Product;
import com.example.astrafarma.dto.ProductDTO;
import com.example.astrafarma.exception.ResourceNotFoundException;
import com.example.astrafarma.mapper.ProductMapper;
import com.example.astrafarma.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductService {

    @Autowired
    private ProductRepository repo;

    @Autowired
    private ProductMapper mapper;

    /**
     * Obtiene todos los productos, los mapea a DTO y los devuelve.
     */
    public List<ProductDTO> listAll() {
        return repo.findAll()
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Crea un producto nuevo a partir del DTO,
     * lo guarda y devuelve el DTO resultante.
     */
    public ProductDTO create(ProductDTO dto) {
        Product entity = mapper.toEntity(dto);
        System.out.println("Entidad guardada: " + entity); // Verifica que tenga category
        Product saved = repo.save(entity);
        ProductDTO result = mapper.toDto(saved);
        System.out.println("DTO devuelto: " + result); // Verifica que NO tenga category null
        return result;
    }


    /**
     * Elimina el producto con el ID dado. Si no existe, lanza 404.
     */
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new ResourceNotFoundException("Producto no encontrado con id: " + id);
        }
        repo.deleteById(id);
    }

    public List<ProductDTO> findByName(String name) {
        return repo.findByNameContainingIgnoreCase(name)
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

}
