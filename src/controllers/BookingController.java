package src.controllers;

import app.AppContext;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import src.database.RepositoryBridge;
import src.events.ControllerBus;
import src.events.ControllerBus.EventType;
import src.events.ControllerExceptions;
import src.models.Customer;
import src.models.Flight;
import src.models.FlightCustomerReservation;
import src.models.ReservationStatus;
import src.models.User;
import src.payment.PaymentGateway;
import src.strategies.BestAvailableSeatStrategy;
import src.strategies.DefaultPricingStrategy;
import src.strategies.PricingStrategy;
import src.strategies.PromoPricingDecorator;
import src.strategies.SearchSortStrategy;
import src.strategies.SeatSelectionStrategy;

//handles booking logic
public class BookingController {
    private PricingStrategy pricing;
    private SearchSortStrategy sorter;
    private SeatSelectionStrategy seatStrategy;
    private final RepositoryBridge repo;
    private final PaymentGateway paymentGateway;

    public BookingController() {
        this.pricing = new DefaultPricingStrategy();
        this.seatStrategy = new BestAvailableSeatStrategy();
        this.repo = AppContext.getInstance().repository();
        this.paymentGateway = AppContext.getInstance().paymentGateway();
    }

    public void setPricing(PricingStrategy p) { this.pricing = p; }
    public void setSorter(SearchSortStrategy s) { this.sorter = s; }
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
        //todo get actual available seat list from flight
        List<String> availableSeats = List.of("1A", "1B", "2A", "2B");
        return seatStrategy.selectSeats(seatsRequested, availableSeats);
    }

    public boolean confirmBooking(String cardToken, Flight f, User u, int seats) {
        //calculating price with optional promo
        if (f == null || u == null || cardToken == null || seats <= 0) {
            throw new IllegalArgumentException("invalid booking parameters");
        }

        //calculating price with optional promo
        PricingStrategy pricingToUse = pricing;
        //todo check for active promo and apply decorator if needed
        BigDecimal amount = new PromoPricingDecorator(pricingToUse, BigDecimal.ZERO)
                .priceFor(f, seats);

        //authorizing the payment
        boolean authOk = paymentGateway.authorize(cardToken, amount);
        if (!authOk) {
            throw new ControllerExceptions.ControllerException(
                ControllerExceptions.ErrorCode.PAYMENT_FAILED,
                "payment authorization failed"
            );
        }

        //capturing the payment
        String authId = "auth_" + System.currentTimeMillis();
        boolean captureOk = paymentGateway.capture(authId, amount);
        if (!captureOk) {
            throw new ControllerExceptions.ControllerException(
                ControllerExceptions.ErrorCode.PAYMENT_FAILED,
                "payment capture failed"
            );
        }

        //saving the reservation
        //creating reservation entity
        String reservationId = "RES_" + System.currentTimeMillis();
        //converting User to Customer
        Customer customer = null;
        if (u instanceof Customer) {
            customer = (Customer) u;
        } else {
            throw new IllegalArgumentException("user must be a Customer");
        }
        
        //reserving seats on flight before creating reservation
        f.reserveSeats(seats);
        
        //creating reservation with confirmed status
        FlightCustomerReservation reservation = new FlightCustomerReservation(
            reservationId,
            customer,
            f,
            ReservationStatus.CONFIRMED,
            seats,
            LocalDateTime.now()
        );
        
        //saving reservation via repo
        repo.saveReservation(reservation);
        
        //publishing reservation created event
        ControllerBus.getInstance().publish(EventType.RESERVATION_CREATED, reservation);

        return true;
    }

    //todo add transaction management for payment + reservation save
    //todo handle concurrency for seat selection
}

