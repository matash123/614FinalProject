package src.controllers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import src.database.PromotionCRUD;
import src.events.ControllerBus;
import src.models.Promotion;

public class PromotionController {

    public List<Promotion> getAllPromotions() {
        return PromotionCRUD.findAll();
    }

    /**
     * Get all promotions that are currently active based on today's date.
     * A promotion is considered active if:
     * - startDate is null or today is on/after startDate, AND
     * - endDate is null or today is on/before endDate.
     */
    public List<Promotion> getActivePromotions() {
        LocalDate today = LocalDate.now();
        List<Promotion> active = new ArrayList<>();

        for (Promotion p : getAllPromotions()) {
            if (p == null) continue;

            LocalDate start = p.getStartDate();
            LocalDate end = p.getEndDate();

            if (start != null && today.isBefore(start)) {
                continue;
            }
            if (end != null && today.isAfter(end)) {
                continue;
            }
            active.add(p);
        }

        return active;
    }

    /**
     * Convenience method to get the most recent active promotion.
     * Relies on underlying {@link PromotionCRUD#findAll()} ordering
     * promotions by start date descending.
     */
    public Promotion getMostRecentActivePromotion() {
        List<Promotion> active = getActivePromotions();
        if (active.isEmpty()) {
            return null;
        }
        return active.get(0);
    }

    //get promotion by id
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

    public List<Promotion> getActivePromotions2() {
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

