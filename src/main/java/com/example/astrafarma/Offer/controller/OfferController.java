package com.example.astrafarma.Offer.controller;

import com.example.astrafarma.Offer.dto.OfferDTO;
import com.example.astrafarma.Offer.domain.OfferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    @PostMapping(consumes = {"multipart/form-data"})
    public OfferDTO createOfferWithImage(
            @RequestPart("data") OfferDTO dto,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) throws Exception {
        return offerService.createOffer(dto, image);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public OfferDTO updateOfferWithImage(
            @PathVariable Long id,
            @RequestPart("data") OfferDTO dto,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) throws Exception {
        return offerService.updateOffer(id, dto, image);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteOffer(@PathVariable Long id) {
        offerService.deleteOffer(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/notify")
    public void notifyOffersToUsers(@RequestBody List<Long> offerIds) {
        offerService.notifyOffersToUsers(offerIds);
    }
}