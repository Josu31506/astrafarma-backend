package com.example.astrafarma.Mail.listeners;

import com.example.astrafarma.Mail.domain.MailService;
import com.example.astrafarma.Mail.events.OffersPublishedEvent;
import com.example.astrafarma.Offer.domain.Offer;
import com.example.astrafarma.Offer.domain.OfferProductDiscount;
import com.example.astrafarma.Offer.repository.OfferRepository;
import com.example.astrafarma.User.domain.User;
import com.example.astrafarma.User.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OffersPublishedListener {

    @Autowired
    private MailService mailService;

    @Autowired
    private OfferRepository offerRepository;

    @Autowired
    private UserRepository userRepository;

    @Async
    @EventListener
    public void handleOffersPublished(OffersPublishedEvent event) {
        List<Long> offerIds = event.getOfferIds();
        List<Offer> offers = offerRepository.findAllWithDiscountsByIdIn(offerIds);

        String message = buildOffersMessage(offers);

        List<User> verifiedUsers = userRepository.findAll().stream()
                .filter(User::isVerified)
                .collect(Collectors.toList());

        for (User user : verifiedUsers) {
            try {
                mailService.sendOffersNotificationMail(user.getEmail(), user.getFullName(), message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String buildOffersMessage(List<Offer> offers) {
        StringBuilder sb = new StringBuilder();
        sb.append("<div style='font-family: -apple-system,BlinkMacSystemFont,\"Segoe UI\",Roboto,Arial,sans-serif;'>");
        for (Offer offer : offers) {
            sb.append("<div style='background:#fff5f5;border-radius:14px;padding:18px;margin:18px 0;box-shadow:0 2px 8px rgba(0,0,0,0.05);'>");

            if (offer.getTitle() != null && !offer.getTitle().isEmpty()) {
                sb.append("<h3 style='color:#ff6b35;font-size:20px;margin:0 0 10px 0;'>")
                        .append(escapeHtml(offer.getTitle())).append("</h3>");
            }

            if (offer.getDescription() != null && !offer.getDescription().isEmpty()) {
                sb.append("<p style='color:#4a5568;margin:0 0 8px 0;'>")
                        .append(escapeHtml(offer.getDescription())).append("</p>");
            }

            sb.append("<div style='color:#2d3748;font-size:14px;'>");
            if (offer.getStartDate() != null && offer.getEndDate() != null) {
                sb.append("Válida desde <strong>").append(offer.getStartDate()).append("</strong> hasta <strong>")
                        .append(offer.getEndDate()).append("</strong><br>");
            }
            if (offer.getDiscounts() != null && !offer.getDiscounts().isEmpty()) {
                sb.append("<ul style='margin:10px 0 0 0;padding:0;list-style:none;'>");
                for (OfferProductDiscount d : offer.getDiscounts()) {
                    sb.append("<li style='padding:4px 0;'>")
                            .append("<span style='color:#2b6cb0;font-weight:600;'>")
                            .append(escapeHtml(d.getProduct().getName()))
                            .append("</span>: ")
                            .append("<span style='background:#f7931e;color:white;padding:3px 8px;border-radius:8px;font-weight:700;'>")
                            .append(d.getDiscountPercentage()).append("% OFF</span></li>");
                }
                sb.append("</ul>");
            }
            sb.append("</div>");

            if (offer.getImageUrl() != null && !offer.getImageUrl().isEmpty()) {
                sb.append("<div style='margin-top:10px;'><a href='").append(escapeHtml(offer.getImageUrl()))
                        .append("' style='color:#ff6b35;'>Más información</a></div>");
            }

            sb.append("</div>");
        }
        sb.append("</div>");
        return sb.toString();
    }

    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;");
    }
}