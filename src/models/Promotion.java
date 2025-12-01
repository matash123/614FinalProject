package src.models;

import java.time.LocalDate;

//promotional discount code and message
public class Promotion {
    private String promotionId;
    private String title;
    private String message;
    private LocalDate startDate;
    private LocalDate endDate;
    private double discountPercent; //0.0 to 1.0

    //constructor
    public Promotion(String promotionId, String title, String message, LocalDate startDate, LocalDate endDate) {
        this(promotionId, title, message, startDate, endDate, 0.0);
    }

    //constructor
    public Promotion(String promotionId, String title, String message, LocalDate startDate, LocalDate endDate, double discountPercent) {
        this.promotionId = promotionId;
        this.title = title;
        this.message = message;
        this.startDate = startDate;
        this.endDate = endDate;
        this.discountPercent = discountPercent;
    }

    //getters
    public String getPromotionId(){ 
        return promotionId; 
    }
    public String getTitle(){ 
        return title; 
    }
    public String getMessage(){ 
        return message; 
    }
    public LocalDate getStartDate(){ 
        return startDate; 
    }
    public LocalDate getEndDate(){ 
        return endDate; 
    }
    public double getDiscountPercent(){ 
        return discountPercent; 
    }

    public void setTitle(String title) { this.title = title; }
    public void setMessage(String message) { this.message = message; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public void setDiscountPercent(double discountPercent){ 
        this.discountPercent = discountPercent; 
    }

    @Override
    public String toString() {
        return title != null && !title.isBlank()
            ? title
            : promotionId != null ? promotionId : "Promotion";
    }
}




