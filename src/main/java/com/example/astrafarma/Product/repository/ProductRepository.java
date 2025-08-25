package com.example.astrafarma.Product.repository;

import com.example.astrafarma.Product.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    /**
     * Busca productos cuyo nombre contenga (ignore case) la cadena dada.
     */
    List<Product> findByNameContainingIgnoreCase(String name);
}
