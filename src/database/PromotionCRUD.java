package src.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import src.models.Promotion;

//handles promotion database operations
public class PromotionCRUD {

    //get all promotions
    public static List<Promotion> findAll() {
        List<Promotion> results = new ArrayList<>();

        try {
            String sql = "SELECT promotion_id, title, message, start_date, end_date FROM promotion ORDER BY start_date DESC";
            PreparedStatement stmt = DB.prepare(sql);
            ResultSet rs = DB.query(stmt);

            while (DB.next(rs)) {
                String id = DB.getString(rs, "promotion_id");
                String title = DB.getString(rs, "title");
                String message = DB.getString(rs, "message");
                String startDateStr = DB.getString(rs, "start_date");
                String endDateStr = DB.getString(rs, "end_date");

                LocalDate startDate = null;
                if (startDateStr != null && !startDateStr.isBlank()) {
                    try {
                        startDate = LocalDate.parse(startDateStr);
                    } catch (Exception ignored) {}
                }

                LocalDate endDate = null;
                if (endDateStr != null && !endDateStr.isBlank()) {
                    try {
                        endDate = LocalDate.parse(endDateStr);
                    } catch (Exception ignored) {}
                }

                Promotion promo = new Promotion(id, title, message, startDate, endDate);
                results.add(promo);
            }

        } catch (RuntimeException e) {
            System.err.println("Error loading promotions: " + e.getMessage());
            throw e;
        }

        return results;
    }

    //get promotion by id
    public static Promotion findById(String promotionId) {
        if (promotionId == null || promotionId.isBlank()) {
            return null;
        }

        try {
            String sql = "SELECT promotion_id, title, message, start_date, end_date FROM promotion WHERE promotion_id = ?";
            PreparedStatement stmt = DB.prepare(sql);
            DB.set(stmt, 1, promotionId.trim());
            ResultSet rs = DB.query(stmt);

            if (DB.next(rs)) {
                String id = DB.getString(rs, "promotion_id");
                String title = DB.getString(rs, "title");
                String message = DB.getString(rs, "message");
                String startDateStr = DB.getString(rs, "start_date");
                String endDateStr = DB.getString(rs, "end_date");

                LocalDate startDate = null;
                if (startDateStr != null && !startDateStr.isBlank()) {
                    try {
                        startDate = LocalDate.parse(startDateStr);
                    } catch (Exception ignored) {}
                }

                LocalDate endDate = null;
                if (endDateStr != null && !endDateStr.isBlank()) {
                    try {
                        endDate = LocalDate.parse(endDateStr);
                    } catch (Exception ignored) {}
                }

                return new Promotion(id, title, message, startDate, endDate);
            }

            return null;

        } catch (RuntimeException e) {
            System.err.println("Error loading promotion: " + e.getMessage());
            return null;
        }
    }

    //save promotion (create or update)
    public static void savePromotion(Promotion promo) {
        if (promo == null) {
            throw new IllegalArgumentException("promotion cannot be null");
        }
        System.out.println("Saving promotion: " + promo.getTitle());

        try {
            String sql =
                "INSERT INTO promotion (promotion_id, title, message, start_date, end_date) " +
                "VALUES (?, ?, ?, ?, ?) " +
                "ON CONFLICT(promotion_id) DO UPDATE SET " +
                "  title = excluded.title, " +
                "  message = excluded.message, " +
                "  start_date = excluded.start_date, " +
                "  end_date = excluded.end_date";

            PreparedStatement stmt = DB.prepare(sql);
            DB.set(stmt, 1, promo.getPromotionId());
            DB.set(stmt, 2, promo.getTitle());
            DB.set(stmt, 3, promo.getMessage());
            DB.set(stmt, 4, promo.getStartDate() != null ? promo.getStartDate().toString() : null);
            DB.set(stmt, 5, promo.getEndDate() != null ? promo.getEndDate().toString() : null);

            DB.update(stmt);

        } catch (RuntimeException e) {
            System.err.println("Error saving promotion: " + e.getMessage());
            throw e;
        }
    }

    //delete promotion
    public static void deletePromotion(String promotionId) {
        if (promotionId == null || promotionId.isBlank()) {
            throw new IllegalArgumentException("promotionId is required");
        }

        try {
            String sql = "DELETE FROM promotion WHERE promotion_id = ?";
            PreparedStatement stmt = DB.prepare(sql);
            DB.set(stmt, 1, promotionId.trim());
            DB.update(stmt);
        } catch (RuntimeException e) {
            System.err.println("Error deleting promotion: " + e.getMessage());
            throw e;
        }
    }
}

