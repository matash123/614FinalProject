package src.controllers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import src.database.PaymentCrud;
import src.database.ReservationCRUD;
import src.events.ControllerBus;
import src.events.ControllerBus.EventType;
import src.events.ControllerExceptions;
import src.models.Flight;
import src.models.Payment;
import src.models.PaymentStatus;
import src.models.Reservation;
import src.models.ReservationStatus;
import src.models.User;
import src.payment.PaymentGateway;
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
        this.paymentGateway = AppContext.getInstance().paymentGateway();
    }

    public void setPricing(PricingStrategy p) { this.pricing = p; }
    public void setSeatStrategy(SeatSelectionStrategy s) { this.seatStrategy = s; }

    public void startBooking(Flight f, User u) {
        //checking basic stuff first
        if (f == null || u == null) { throw new IllegalArgumentException("null args"); }
        if (!f.hasAvailableSeats(1)) {
            throw new ControllerExceptions.ControllerException(
                ControllerExceptions.ErrorCode.INSUFFICIENT_SEATS,
                "flight has no available seats"
            );
        }
        //todo create booking draft in repo or context
        //todo publish event
    }

    public List<String> selectSeats(Flight f, int seatsRequested) {
        //using the seat selection strategy
        if (f == null || seatsRequested <= 0) {
            throw new IllegalArgumentException("invalid flight or seat count");
        }
        if (!f.hasAvailableSeats(seatsRequested)) {
            throw new ControllerExceptions.ControllerException(
                ControllerExceptions.ErrorCode.INSUFFICIENT_SEATS,
                "not enough seats available"
            );
        }
        // TODO: integrate with real seat map; for now use a simple fixed list.
        List<String> availableSeats = List.of("1A", "1B", "2A", "2B");
        return seatStrategy.selectSeats(seatsRequested, availableSeats);
    }

    public boolean confirmBooking(String cardToken, Flight f, User u, int seats) {
        // basic parameter validation
        if (f == null || u == null || cardToken == null || seats <= 0) {
            throw new IllegalArgumentException("invalid booking parameters");
        }

        // ensure only customers can book
        String role = u.getRole();
        if (role == null || !role.equalsIgnoreCase("customer")) {
            throw new IllegalArgumentException("only customers can book flights");
        }

        // ensure the flight actually has enough seats for this booking
        if (!f.hasAvailableSeats(seats)) {
            throw new ControllerExceptions.ControllerException(
                ControllerExceptions.ErrorCode.INSUFFICIENT_SEATS,
                "not enough seats available"
            );
        }

        // calculating price with optional promo
        // TODO: check for active promo and apply decorator if needed
        BigDecimal amount = new PromoPricingDecorator(pricing, BigDecimal.ZERO)
                .priceFor(f, seats);

        // authorizing the payment
        boolean authOk = paymentGateway.authorize(cardToken, amount);
        if (!authOk) {
            throw new ControllerExceptions.ControllerException(
                ControllerExceptions.ErrorCode.PAYMENT_FAILED,
                "payment authorization failed"
            );
        }

        // capturing the payment
        String authId = "auth_" + System.currentTimeMillis();
        boolean captureOk = paymentGateway.capture(authId, amount);
        if (!captureOk) {
            throw new ControllerExceptions.ControllerException(
                ControllerExceptions.ErrorCode.PAYMENT_FAILED,
                "payment capture failed"
            );
        }

        // reserving seats on flight before creating reservation
        f.reserveSeats(seats);

        // creating reservation entity with confirmed status
        String reservationId = "RES_" + System.currentTimeMillis();
        LocalDateTime now = LocalDateTime.now();
        Reservation reservation = new Reservation(
            reservationId,
            u,
            f,
            ReservationStatus.CONFIRMED,
            seats,
            now
        );

        // persist reservation so that payment FK can point to it
        ReservationCRUD.saveReservation(reservation);

        // create and persist payment record
        String paymentId = "PAY_" + System.currentTimeMillis();
        Payment payment = new Payment(
            paymentId,
            reservation,
            amount.doubleValue(),
            PaymentStatus.PAID,
            now
        );
        PaymentCrud.savePayment(payment);

        // publishing reservation created event
        ControllerBus.getInstance().publish(EventType.RESERVATION_CREATED, reservation);

        return true;
    }

    //todo add transaction management for payment + reservation save
    //todo handle concurrency for seat selection
}

