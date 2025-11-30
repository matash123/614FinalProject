package src.models;

import java.time.LocalDate;

//represents a promotional news item or discount
public class Promotion {
    private String promotionId;
    private String title;
    private String message;
    private LocalDate startDate;
    private LocalDate endDate;

    public Promotion(String promotionId, String title, String message, LocalDate startDate, LocalDate endDate) {
        this.promotionId = promotionId;
        this.title = title;
        this.message = message;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getPromotionId() { return promotionId; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }

    public void setTitle(String title) { this.title = title; }
    public void setMessage(String message) { this.message = message; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    @Override
    public String toString() {
        return title != null && !title.isBlank()
            ? title
            : promotionId != null ? promotionId : "Promotion";
    }
}


