package controller;

import app.AppContext;

//factory for creating controllers
public class ControllerFactory {
    private static ControllerFactory INSTANCE;

    private final AuthController authController;
    private final FlightSearchController flightSearchController;
    private final BookingController bookingController;
    private final PaymentController paymentController;
    //todo add others later

    private ControllerFactory(){
        //setting up the basic controllers we need
        this.authController=new AuthController();
        this.flightSearchController=new FlightSearchController();
        this.bookingController=new BookingController();
        this.paymentController=new PaymentController(AppContext.getInstance().paymentGateway());
        //todo init rest
    }

    public static synchronized ControllerFactory getInstance(){
        if(INSTANCE==null){
            INSTANCE=new ControllerFactory();
        }
        return INSTANCE;
    }

    public AuthController auth(){ return authController; }
    public FlightSearchController flights(){ return flightSearchController; }
    public BookingController booking(){ return bookingController; }
    public PaymentController payments(){ return paymentController; }
    //todo expose others
}

