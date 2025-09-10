package com.example.astrafarma.Offer.domain;

import com.example.astrafarma.Mail.events.OffersPublishedEvent;
import com.example.astrafarma.Offer.dto.OfferDTO;
import com.example.astrafarma.Offer.dto.ProductDiscountDTO;
import com.example.astrafarma.Product.domain.Product;
import com.example.astrafarma.Product.repository.ProductRepository;
import com.example.astrafarma.Offer.repository.OfferRepository;
import com.example.astrafarma.SupabaseUpload.domain.SupabaseStorageService;
import com.example.astrafarma.SupabaseUpload.dto.UploadResponseDTO;
import com.example.astrafarma.exception.OfferNotFoundException;
import com.example.astrafarma.exception.InvalidProductException;
import com.example.astrafarma.Offer.domain.OfferProductDiscount;
import com.example.astrafarma.mapper.OfferMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
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

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private SupabaseStorageService supabaseStorage;

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


    public OfferDTO createOffer(OfferDTO dto, MultipartFile image) throws Exception {
        Offer offer = offerMapper.offerDTOToOffer(dto);

        // Load and validate products
        List<Product> products = productRepository.findAllById(dto.getProductIds());
        if (products.size() != dto.getProductIds().size()) {
            List<Long> existingIds = products.stream()
                    .map(Product::getId)
                    .collect(Collectors.toList());
            List<Long> missingIds = dto.getProductIds().stream()
                    .filter(id -> !existingIds.contains(id))
                    .collect(Collectors.toList());
            throw new InvalidProductException("Productos no encontrados: " + missingIds);
        }
        offer.setProducts(products);

        // Map discounts ensuring each product exists
        if (dto.getDiscounts() != null) {
            List<OfferProductDiscount> discounts = new ArrayList<>();
            for (ProductDiscountDTO discountDTO : dto.getDiscounts()) {
                Product product = productRepository.findById(discountDTO.getProductId())
                        .orElseThrow(() -> new InvalidProductException(
                                "Producto no encontrado con id: " + discountDTO.getProductId()));
                OfferProductDiscount discount = new OfferProductDiscount();
                discount.setOffer(offer);
                discount.setProduct(product);
                discount.setDiscountPercentage(discountDTO.getDiscountPercentage());
                discounts.add(discount);
            }
            offer.setDiscounts(discounts);
        }

        if (image != null && !image.isEmpty()) {
            UploadResponseDTO img = supabaseStorage.uploadImage(image, false);
            offer.setImageUrl(img.getUrl());
        }

        Offer saved = offerRepository.save(offer);
        return offerMapper.offerToOfferDTO(saved);
    }


    public OfferDTO updateOffer(Long id, OfferDTO dto, MultipartFile image) throws Exception {
        Offer offer = offerRepository.findById(id)
                .orElseThrow(() -> new OfferNotFoundException("Oferta no encontrada"));

        if (dto.getTitle() != null) {
            offer.setTitle(dto.getTitle());
        }
        if (dto.getDescription() != null) {
            offer.setDescription(dto.getDescription());
        }
        if (dto.getImageUrl() != null) {
            offer.setImageUrl(dto.getImageUrl());
        }
        if (dto.getStartDate() != null) {
            offer.setStartDate(dto.getStartDate());
        }
        if (dto.getEndDate() != null) {
            offer.setEndDate(dto.getEndDate());
        }
        if (dto.getProductIds() != null) {
            List<Product> products = productRepository.findAllById(dto.getProductIds());
            if (products.size() != dto.getProductIds().size()) {
                List<Long> existingIds = products.stream()
                        .map(Product::getId)
                        .collect(Collectors.toList());
                List<Long> missingIds = dto.getProductIds().stream()
                        .filter(id -> !existingIds.contains(id))
                        .collect(Collectors.toList());
                throw new InvalidProductException("Productos no encontrados: " + missingIds);
            }
            offer.setProducts(products);
        }
        if (dto.getDiscounts() != null) {
            offer.getDiscounts().clear();
            for (ProductDiscountDTO discountDTO : dto.getDiscounts()) {
                Product product = productRepository.findById(discountDTO.getProductId())
                        .orElseThrow(() -> new InvalidProductException(
                                "Producto no encontrado con id: " + discountDTO.getProductId()));
                OfferProductDiscount discount = new OfferProductDiscount();
                discount.setOffer(offer);
                discount.setProduct(product);
                discount.setDiscountPercentage(discountDTO.getDiscountPercentage());
                offer.getDiscounts().add(discount);
            }
        }
        if (image != null && !image.isEmpty()) {
            supabaseStorage.deleteImage(offer.getImageUrl(), false);
            UploadResponseDTO img = supabaseStorage.uploadImage(image, false);
            offer.setImageUrl(img.getUrl());
        }
        Offer updated = offerRepository.save(offer);
        return offerMapper.offerToOfferDTO(updated);
    }

    public void deleteOffer(Long id) {
        if (!offerRepository.existsById(id)) {
            throw new OfferNotFoundException("Oferta no encontrada");
        }
        offerRepository.deleteById(id);
    }

    public void notifyOffersToUsers(List<Long> offerIds) {
        List<Offer> offers = offerRepository.findAllWithDiscountsByIdIn(offerIds);

        List<ProductDiscountDTO> allDiscounts = offers.stream()
                .flatMap(offer -> offer.getDiscounts().stream()
                        .map(d -> {
                            ProductDiscountDTO dto = new ProductDiscountDTO();
                            dto.setProductId(d.getProduct().getId());
                            dto.setDiscountPercentage(d.getDiscountPercentage());

                            BigDecimal price = d.getProduct().getPrice();
                            BigDecimal discountFraction = BigDecimal.valueOf(d.getDiscountPercentage())
                                    .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
                            BigDecimal discounted = price.subtract(price.multiply(discountFraction));
                            dto.setDiscountedPrice(discounted);
                            return dto;
                        })
                ).collect(Collectors.toList());

        eventPublisher.publishEvent(new OffersPublishedEvent(offerIds, allDiscounts));
    }
}