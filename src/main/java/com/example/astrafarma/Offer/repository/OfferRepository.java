package com.example.astrafarma.Offer.repository;

import com.example.astrafarma.Offer.domain.Offer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OfferRepository extends JpaRepository<Offer, Long> {
}