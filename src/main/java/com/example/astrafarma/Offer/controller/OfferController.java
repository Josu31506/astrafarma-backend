package com.example.astrafarma.Offer.controller;

import com.example.astrafarma.Offer.dto.OfferDTO;
import com.example.astrafarma.Offer.domain.OfferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/offers")
public class OfferController {

    @Autowired
    private OfferService offerService;

    @GetMapping
    public List<OfferDTO> getAllOffers() {
        return offerService.getAllOffers();
    }

    @GetMapping("/{id}")
    public OfferDTO getOffer(@PathVariable Long id) {
        return offerService.getOfferById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public OfferDTO createOffer(@RequestBody OfferDTO dto) {
        return offerService.createOffer(dto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public OfferDTO updateOffer(@PathVariable Long id, @RequestBody OfferDTO dto) {
        return offerService.updateOffer(id, dto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteOffer(@PathVariable Long id) {
        offerService.deleteOffer(id);
    }
}