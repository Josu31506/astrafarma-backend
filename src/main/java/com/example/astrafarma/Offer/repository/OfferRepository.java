package com.example.astrafarma.Offer.repository;

import com.example.astrafarma.Offer.domain.Offer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OfferRepository extends JpaRepository<Offer, Long> {
    @Query("SELECT o FROM Offer o LEFT JOIN FETCH o.discounts WHERE o.id IN :ids")
    List<Offer> findAllWithDiscountsByIdIn(@Param("ids") List<Long> ids);
}