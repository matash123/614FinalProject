package src.factory;

import src.controllers.AdminFlightController;
import src.controllers.AppContext;
import src.controllers.AuthController;
import src.controllers.BookingController;
import src.controllers.FlightSearchController;
import src.controllers.PaymentController;
import src.controllers.UserController;

//factory for creating controllers
public class ControllerFactory {
    private static ControllerFactory INSTANCE;

    private final AuthController authController;
    private final FlightSearchController flightSearchController;
    private final BookingController bookingController;
    private final PaymentController paymentController;
    private final AdminFlightController adminFlightController;
    private final UserController userController;
    //todo add others later

    private ControllerFactory() {
        //setting up the basic controllers we need
        this.authController = new AuthController();
        this.flightSearchController = new FlightSearchController();
        this.bookingController = new BookingController();
        this.paymentController = new PaymentController(AppContext.getInstance().paymentGateway());
        this.adminFlightController = new AdminFlightController();
        this.userController = new UserController();
        //todo init rest
    }

    public static synchronized ControllerFactory getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ControllerFactory();
        }
        return INSTANCE;
    }

    public AuthController auth() { return authController; }
    public FlightSearchController flights() { return flightSearchController; }
    public BookingController booking() { return bookingController; }
    public PaymentController payments() { return paymentController; }
    public AdminFlightController adminFlights() { return adminFlightController; }
    public UserController user() { return userController; }
    //todo expose others
}
