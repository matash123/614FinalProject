package src.controllers;

import java.time.LocalDateTime;
import java.util.List;
import src.database.FlightCrud;
import src.database.PaymentCRUD;
import src.database.ReservationCRUD;
import src.models.Customer;
import src.models.Flight;
import src.models.Payment;
import src.models.PaymentStatus;
import src.models.Reservation;
import src.models.ReservationStatus;
import src.models.User;
import src.payment.PaymentGateway;
import src.payment.SimulatedPaymentGateway;
import src.strategies.BestAvailableSeatStrategy;
import src.strategies.DefaultPricingStrategy;
import src.strategies.PricingStrategy;
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

    // 2) Calculate the total price
    double totalAmount = f.getPrice() * seats;

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

    //VERY LAST ADDITION - had to fix update missed this and doing so after updating FlightCRUD
    //we want to decrement Available Seats

    boolean seatUpdated = FlightCrud.decrementAvailableSeats(f.getFlightId(), seats);
    if (!seatUpdated) {
        System.err.println("Warning: seat update failed for flight " + f.getFlightId());
    }

    // Now we can create and save payment
    String paymentId = "P" + System.currentTimeMillis();

    Payment payment = new Payment(
            paymentId,
            reservation,
            totalAmount,
            PaymentStatus.PAID,
            LocalDateTime.now(),
            cardToken
    );

    // Save payment directly via CRUD
    PaymentCRUD.insertPayment(payment);

    // Ask the top-level app controller to refresh the current view
    AppController app = AppController.getInstance();
    if (app != null) {
        app.updateAppView();
    }

    return true;
    }

    /**
     * Update the number of seats on an existing reservation, ensuring that
     * the acting user owns the reservation.
     */
    public void updateReservationSeats(String reservationId, int newSeats, User actor) {
        if (reservationId == null || reservationId.isBlank()) {
            throw new IllegalArgumentException("reservationId is required");
        }
        if (actor == null || actor.getUserId() == null || actor.getUserId().isBlank()) {
            throw new IllegalArgumentException("actor user is required");
        }
        if (newSeats <= 0) {
            throw new IllegalArgumentException("Seats must be positive");
        }

        Reservation existing = ReservationCRUD.getReservationById(reservationId);
        if (existing == null) {
            throw new IllegalArgumentException("Reservation not found: " + reservationId);
        }

        User resUser = existing.getUser();
        if (resUser == null || resUser.getUserId() == null ||
            !actor.getUserId().equals(resUser.getUserId())) {
            throw new SecurityException("You can only modify your own reservations.");
        }

        // Look up the latest payment for this reservation so we can reuse
        // the stored credit card number when re-booking.
        List<Payment> payments = PaymentCRUD.getPaymentsForReservation(reservationId);
        String cardNumber = null;
        LocalDateTime latestTs = null;
        Payment latestPayment = null;
        for (Payment p : payments) {
            if (p == null) continue;
            LocalDateTime ts = p.getTimestamp();
            if (ts == null) {
                continue;
            }
            if (latestTs == null || ts.isAfter(latestTs)) {
                latestTs = ts;
                cardNumber = p.getCreditCardNumber();
                latestPayment = p;
            }
        }

        if (latestPayment == null || cardNumber == null || cardNumber.isBlank()) {
            throw new IllegalStateException("No previous payment with a stored credit card was found for this reservation.");
        }

        // Mark the old payment as cancelled before creating the new one.
        PaymentCRUD.updatePaymentStatus(latestPayment.getPaymentId(), PaymentStatus.CANCELLED);

        // First cancel the existing reservation record.
        cancelReservation(reservationId, actor);

        // Then create a new booking on the same flight with the new seat
        // count, reusing the stored credit card number.
        Flight flight = existing.getFlight();
        confirmBooking(cardNumber, flight, actor, newSeats);
    }

    /**
     * Cancel an existing reservation by setting its status to CANCELLED,
     * ensuring that the acting user owns the reservation.
     */
    public void cancelReservation(String reservationId, User actor) {
        if (reservationId == null || reservationId.isBlank()) {
            throw new IllegalArgumentException("reservationId is required");
        }
        if (actor == null || actor.getUserId() == null || actor.getUserId().isBlank()) {
            throw new IllegalArgumentException("actor user is required");
        }

        Reservation existing = ReservationCRUD.getReservationById(reservationId);
        if (existing == null) {
            throw new IllegalArgumentException("Reservation not found: " + reservationId);
        }

        User resUser = existing.getUser();
        if (resUser == null || resUser.getUserId() == null ||
            !actor.getUserId().equals(resUser.getUserId())) {
            throw new SecurityException("You can only cancel your own reservations.");
        }

        ReservationCRUD.updateStatus(reservationId, ReservationStatus.CANCELLED);

        //SAME THING VERY LAST ADDITION TO CODE, now that we updated Status as cancelled, we can give back the space
        //and use our incrementAvailableSeats

        //Reference to ChatGPT for much and grateful help with this (we were stuck along time)
        //need to get the flight info exactly from this object
        Flight flight = existing.getFlight();
        int seats     = existing.getSeats();

        //now actually applying the update with a check
        boolean seatUpdated = FlightCrud.incrementAvailableSeats(flight.getFlightId(), seats);
        if (!seatUpdated) {
            System.err.println("Warning: seat restore failed for flight " + flight.getFlightId());
        }

        AppController app = AppController.getInstance();
        if (app != null) {
            app.updateAppView();
        }
    }
}


