package controller;

import java.math.BigDecimal;
import java.util.List;
import app.AppContext;
import domain.Flight;
import domain.User;
import domain.FlightCustomerReservation;
import controller.PricingStrategy;
import controller.DefaultPricingStrategy;
import controller.PromoPricingDecorator;
import controller.SeatSelectionStrategy;
import controller.BestAvailableSeatStrategy;
import controller.ControllerBus.EventType;

//handles booking logic
public class BookingController {
    private PricingStrategy pricing;
    private SearchSortStrategy sorter;
    private SeatSelectionStrategy seatStrategy;
    private final RepositoryBridge repo;
    private final PaymentGateway paymentGateway;

    public BookingController(){
        this.pricing=new DefaultPricingStrategy();
        this.seatStrategy=new BestAvailableSeatStrategy();
        this.repo=AppContext.getInstance().repository();
        this.paymentGateway=AppContext.getInstance().paymentGateway();
    }

    public void setPricing(PricingStrategy p){ this.pricing=p; }
    public void setSorter(SearchSortStrategy s){ this.sorter=s; }
    public void setSeatStrategy(SeatSelectionStrategy s){ this.seatStrategy=s; }

    public void startBooking(Flight f,User u){
        //checking basic stuff first
        if(f==null||u==null){ throw new IllegalArgumentException("null args"); }
        if(!f.hasAvailableSeats(1)){
            throw new ControllerExceptions.ControllerException(
                ControllerExceptions.ErrorCode.INSUFFICIENT_SEATS,
                "flight has no available seats"
            );
        }
        //todo create booking draft in repo or context
        //todo publish event
    }

    public List<String> selectSeats(Flight f,int seatsRequested){
        //using the seat selection strategy
        if(f==null || seatsRequested<=0){
            throw new IllegalArgumentException("invalid flight or seat count");
        }
        if(!f.hasAvailableSeats(seatsRequested)){
            throw new ControllerExceptions.ControllerException(
                ControllerExceptions.ErrorCode.INSUFFICIENT_SEATS,
                "not enough seats available"
            );
        }
        //todo get actual available seat list from flight
        List<String> availableSeats=List.of("1A","1B","2A","2B");
        return seatStrategy.selectSeats(seatsRequested,availableSeats);
    }

    public boolean confirmBooking(String cardToken,Flight f,User u,int seats){
        //calculating price with optional promo
        if(f==null || u==null || cardToken==null || seats<=0){
            throw new IllegalArgumentException("invalid booking parameters");
        }

        //todo check for active promo and apply decorator if needed
        PricingStrategy pricingToUse=pricing;
        //for now using default todo check for promo and wrap
        BigDecimal amount=new PromoPricingDecorator(pricingToUse,BigDecimal.ZERO)
                .priceFor(f,seats);

        //authorizing the payment
        boolean authOk=paymentGateway.authorize(cardToken,amount);
        if(!authOk){
            throw new ControllerExceptions.ControllerException(
                ControllerExceptions.ErrorCode.PAYMENT_FAILED,
                "payment authorization failed"
            );
        }

        //capturing the payment
        String authId="auth_"+System.currentTimeMillis();
        boolean captureOk=paymentGateway.capture(authId,amount);
        if(!captureOk){
            throw new ControllerExceptions.ControllerException(
                ControllerExceptions.ErrorCode.PAYMENT_FAILED,
                "payment capture failed"
            );
        }

        //saving the reservation
        //todo create FlightCustomerReservation and save via repo
        //todo publish ReservationCreated event

        return true;
    }

    //todo partial seat failures concurrency retry and rollback
}

