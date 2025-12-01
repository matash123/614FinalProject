package src.controllers;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import src.database.PromotionCRUD;
import src.events.ControllerBus;
import src.models.Promotion;

public class PromotionController {

    public List<Promotion> getAllPromotions() {
        return PromotionCRUD.findAll();
    }

    public Promotion getPromotionById(String promotionId) {
        if (promotionId == null || promotionId.isBlank()) {
            return null;
        }
        return PromotionCRUD.findById(promotionId);
    }

    public Promotion createPromotion(String title, String message, LocalDate startDate, LocalDate endDate) {
        String promotionId = "PR" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Promotion promo = new Promotion(promotionId, title, message, startDate, endDate);
        PromotionCRUD.savePromotion(promo);
        ControllerBus.getInstance().publish(ControllerBus.EventType.PROMO_SENT, promo);
        return promo;
    }

    public Promotion updatePromotion(String promotionId, String title, String message, LocalDate startDate, LocalDate endDate) {
        Promotion existing = PromotionCRUD.findById(promotionId);
        if (existing == null) {
            throw new IllegalArgumentException("Promotion not found");
        }

        existing.setTitle(title);
        existing.setMessage(message);
        existing.setStartDate(startDate);
        existing.setEndDate(endDate);
        PromotionCRUD.savePromotion(existing);
        ControllerBus.getInstance().publish(ControllerBus.EventType.PROMO_SENT, existing);
        return existing;
    }

    public boolean deletePromotion(String promotionId) {
        if (promotionId == null || promotionId.isBlank()) {
            return false;
        }
        PromotionCRUD.deletePromotion(promotionId);
        return true;
    }

    public List<Promotion> getActivePromotions() {
        LocalDate today = LocalDate.now();
        List<Promotion> allPromotions = getAllPromotions();
        return allPromotions.stream()
            .filter(p -> !today.isBefore(p.getStartDate()) && !today.isAfter(p.getEndDate()))
            .toList();
    }

    public Promotion validateAndGetActivePromotion(String promotionId) {
        if (promotionId == null || promotionId.isBlank()) {
            return null;
        }
        
        Promotion promo = getPromotionById(promotionId);
        if (promo == null) {
            return null;
        }
        
        LocalDate today = LocalDate.now();
        if (!today.isBefore(promo.getStartDate()) && !today.isAfter(promo.getEndDate())) {
            return promo;
        }
        
        return null;
    }
}

