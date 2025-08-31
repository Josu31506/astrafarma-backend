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

    @Column(name = "title", length = 256)
    private String title;

    @Column(name = "description", length = 1256)
    private String description;

    @Column(name = "image_url", length = 1256)
    private String imageUrl;

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

    @OneToMany(mappedBy = "offer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfferProductDiscount> discounts;
}