package src.controllers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import src.database.PaymentCrud;
import src.database.ReservationCRUD;
import src.factory.ControllerFactory;
import src.models.Customer;
import src.models.Flight;
import src.models.Payment;
import src.models.PaymentStatus;
import src.models.Promotion;
import src.models.Reservation;
import src.models.ReservationStatus;
import src.models.User;
import src.payment.PaymentGateway;
import src.payment.SimulatedPaymentGateway;
import src.strategies.BestAvailableSeatStrategy;
import src.strategies.DefaultPricingStrategy;
import src.strategies.PricingStrategy;
import src.strategies.PromoPricingDecorator;
import src.strategies.SeatSelectionStrategy;

//handles booking logic
public class BookingController {
    private PricingStrategy pricing;
    private SeatSelectionStrategy seatStrategy;
    private final PaymentGateway paymentGateway;

    public BookingController() {
        this.pricing = new DefaultPricingStrategy();
        this.seatStrategy = new BestAvailableSeatStrategy();
        // Use a concrete gateway directly; no AppContext indirection.
        this.paymentGateway = new SimulatedPaymentGateway();
    }

    public void setPricing(PricingStrategy p) { this.pricing = p; }
    public void setSeatStrategy(SeatSelectionStrategy s) { this.seatStrategy = s; }

    //implementing basic checks first
    public void startBooking(Flight f, User u) {
    // ensuring fields look okay
    if (f == null || u == null) {
        throw new IllegalArgumentException("Flight and user must not be null");
    }
    // Simple seat check
    if (!f.hasAvailableSeats(1)) {
        throw new IllegalStateException("Flight has no available seats");
    }

}
        //todo create booking draft in repo or context
        //todo publish event
    



/**
 * This perhaps is the most complication cass of our entire project
 * its the deepest in terms of layering and speaks to the most amount of classes
 * we 
 * 
 * */

public boolean confirmBooking(String cardToken, Flight f, User u, int seats){
    return confirmBooking(cardToken, f, u, seats, null);
}

public boolean confirmBooking(String cardToken, Flight f, User u, int seats, String promotionId){
    // Again basic validatoo
    if (f == null || u == null) {
        throw new IllegalArgumentException("Flight and user are required");
    }
    if (seats <= 0) {
        throw new IllegalArgumentException("Seats must be positive");
    }

    // Ensuring seat availability
    if (!f.hasAvailableSeats(seats)) {
        throw new IllegalStateException("Not enough seats available on this flight");
    }

    // 2) Calculate the total price with optional promotion discount
    PricingStrategy basePricing = new DefaultPricingStrategy();
    BigDecimal discountPercent = BigDecimal.ZERO;
    
    // Check if promotion ID is provided and valid
    if (promotionId != null && !promotionId.isBlank()) {
        Promotion promo = ControllerFactory.getInstance().promotions().validateAndGetActivePromotion(promotionId);
        if (promo != null) {
            // Use the discount percentage from the promotion
            discountPercent = BigDecimal.valueOf(promo.getDiscountPercent());
        }
    }
    
    PricingStrategy finalPricing = new PromoPricingDecorator(basePricing, discountPercent);
    BigDecimal totalAmountBigDecimal = finalPricing.priceFor(f, seats);
    double totalAmount = totalAmountBigDecimal.doubleValue();

    // Reserve seats in memory, we dont need to write to database yet
    f.reserveSeats(seats);
    // (optional) later: persist updated available_seats with a repo method

    // Now moving our customer from dabtabase into the JAVA by constructing an object of it
    Customer customer;
    if (u instanceof Customer c) {
        customer = c;
    } else {
        customer = new Customer(
                u.getUserId(),
                u.getName(),
                ""   // we don't need password/email here
        );
    }

    // Building a reservation object
    String reservationId = "R" + System.currentTimeMillis();

    Reservation reservation = new Reservation(
            reservationId,
            u,          // we store the authenticated user on the reservation
            f,
            ReservationStatus.CONFIRMED,    // treat as confirmed
            seats,
            LocalDateTime.now()
    );

    // Save reservation directly via CRUD
    System.out.println("Saving reservation");
    ReservationCRUD.saveReservation(reservation);

    // Now we can create and save payment
    String paymentId = "P" + System.currentTimeMillis();

    Payment payment = new Payment(
            paymentId,
            reservation,
            totalAmount,
            PaymentStatus.PAID,
            LocalDateTime.now()
    );

    // Save payment directly via CRUD
    PaymentCrud.insertPayment(payment);

    
    return true;
    }

}


