package com.example.astrafarma.Product.domain;

import com.example.astrafarma.Product.domain.Product;
import com.example.astrafarma.Product.dto.ProductDTO;
import com.example.astrafarma.exception.ResourceNotFoundException;
import com.example.astrafarma.mapper.ProductMapper;
import com.example.astrafarma.Product.repository.ProductRepository;
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

    public List<ProductDTO> listAll() {
        return repo.findAll()
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    public ProductDTO create(ProductDTO dto) {
        Product entity = mapper.toEntity(dto);
        System.out.println("Entidad guardada: " + entity);
        Product saved = repo.save(entity);
        ProductDTO result = mapper.toDto(saved);
        System.out.println("DTO devuelto: " + result);
        return result;
    }

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

    public ProductDTO updateById(Long id, ProductDTO dto) {
        Product existing = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + id));
        updateEntityFields(existing, dto);

        Product saved = repo.save(existing);
        return mapper.toDto(saved);
    }

    public ProductDTO updateByName(ProductDTO dto) {
        Product existing = repo.findByName(dto.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con nombre: " + dto.getName()));

        updateEntityFields(existing, dto);

        Product saved = repo.save(existing);
        return mapper.toDto(saved);
    }

    private void updateEntityFields(Product entity, ProductDTO dto) {
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setPrice(dto.getPrice());
        entity.setImageUrl(dto.getImageUrl());
        entity.setCategory(dto.getCategory());
    }
}
