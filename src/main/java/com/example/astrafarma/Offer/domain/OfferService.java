package com.example.astrafarma.Offer.domain;

import com.example.astrafarma.Offer.dto.OfferDTO;
import com.example.astrafarma.Product.repository.ProductRepository;
import com.example.astrafarma.Offer.repository.OfferRepository;
import com.example.astrafarma.exception.OfferNotFoundException;
import com.example.astrafarma.mapper.OfferMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OfferService {

    @Autowired
    private OfferRepository offerRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OfferMapper offerMapper;

    public List<OfferDTO> getAllOffers() {
        return offerRepository.findAll().stream()
                .map(offerMapper::offerToOfferDTO)
                .collect(Collectors.toList());
    }

    public OfferDTO getOfferById(Long id) {
        Offer offer = offerRepository.findById(id)
                .orElseThrow(() -> new OfferNotFoundException("Oferta no encontrada"));
        return offerMapper.offerToOfferDTO(offer);
    }

    public OfferDTO createOffer(OfferDTO dto) {
        Offer offer = offerMapper.offerDTOToOffer(dto);
        offer.setProducts(productRepository.findAllById(dto.getProductIds()));
        Offer saved = offerRepository.save(offer);
        return offerMapper.offerToOfferDTO(saved);
    }

    public OfferDTO updateOffer(Long id, OfferDTO dto) {
        Offer offer = offerRepository.findById(id)
                .orElseThrow(() -> new OfferNotFoundException("Oferta no encontrada"));
        offer.setImageUrl(dto.getImageUrl());
        offer.setMensajeWhatsApp(dto.getMensajeWhatsApp());
        offer.setStartDate(dto.getStartDate());
        offer.setEndDate(dto.getEndDate());
        offer.setProducts(productRepository.findAllById(dto.getProductIds()));
        Offer updated = offerRepository.save(offer);
        return offerMapper.offerToOfferDTO(updated);
    }

    public void deleteOffer(Long id) {
        if (!offerRepository.existsById(id)) {
            throw new OfferNotFoundException("Oferta no encontrada");
        }
        offerRepository.deleteById(id);
    }
}