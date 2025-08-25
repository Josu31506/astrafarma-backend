package com.example.astrafarma.Offer.domain;

import com.example.astrafarma.Product.domain.Product;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "offers")
@Data
@NoArgsConstructor
public class Offer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Column(length = 1000, nullable = false)
    private String mensajeWhatsApp;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @ManyToMany
    @JoinTable(
            name = "offer_products",
            joinColumns = @JoinColumn(name = "offer_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    @NotEmpty(message = "La oferta debe estar vinculada al menos a un producto")
    private List<Product> products;
}
