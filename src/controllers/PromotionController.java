package src.controllers;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import src.database.PromotionCRUD;
import src.events.ControllerBus;
import src.models.Promotion;

//handles promotion management operations
public class PromotionController {

    //get all promotions
    public List<Promotion> getAllPromotions() {
        return PromotionCRUD.findAll();
    }

    //get promotion by id
    public Promotion getPromotionById(String promotionId) {
        if (promotionId == null || promotionId.isBlank()) {
            throw new IllegalArgumentException("promotionId is required");
        }
        return PromotionCRUD.findById(promotionId);
    }

    //create new promotion
    public Promotion createPromotion(String title, String message, LocalDate startDate, LocalDate endDate) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("title is required");
        }
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("message is required");
        }
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("startDate and endDate are required");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("startDate must be before endDate");
        }

        String promotionId = "PR" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Promotion promo = new Promotion(promotionId, title, message, startDate, endDate);
        PromotionCRUD.savePromotion(promo);

        ControllerBus.getInstance().publish(ControllerBus.EventType.PROMO_SENT, promo);
        return promo;
    }

    //update existing promotion
    public Promotion updatePromotion(String promotionId, String title, String message, LocalDate startDate, LocalDate endDate) {
        if (promotionId == null || promotionId.isBlank()) {
            throw new IllegalArgumentException("promotionId is required");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("title is required");
        }
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("message is required");
        }
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("startDate and endDate are required");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("startDate must be before endDate");
        }

        Promotion existing = PromotionCRUD.findById(promotionId);
        if (existing == null) {
            throw new IllegalArgumentException("promotion not found");
        }

        existing.setTitle(title);
        existing.setMessage(message);
        existing.setStartDate(startDate);
        existing.setEndDate(endDate);

        PromotionCRUD.savePromotion(existing);
        ControllerBus.getInstance().publish(ControllerBus.EventType.PROMO_SENT, existing);
        return existing;
    }

    //delete promotion
    public boolean deletePromotion(String promotionId) {
        if (promotionId == null || promotionId.isBlank()) {
            throw new IllegalArgumentException("promotionId is required");
        }

        try {
            PromotionCRUD.deletePromotion(promotionId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

