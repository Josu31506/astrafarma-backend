package com.example.astrafarma.User.domain;

import com.example.astrafarma.Product.domain.ProductCategory;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_category_stats",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "category"}))
@Data
@NoArgsConstructor
public class UserCategoryStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductCategory category;

    @Column(nullable = false)
    private int interactionCount = 0;

    public UserCategoryStats(User user, ProductCategory category) {
        this.user = user;
        this.category = category;
        this.interactionCount = 1;
    }

    public void increment() {
        this.interactionCount++;
    }
}
