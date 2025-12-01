package src.factory;

import src.controllers.AdminFlightController;
import src.controllers.AgentController;
import src.controllers.AppContext;
import src.controllers.AuthController;
import src.controllers.BookingController;
import src.controllers.FlightSearchController;
import src.controllers.PaymentController;
import src.controllers.PromotionController;
import src.controllers.UserController;

//factory for creating controllers
public class ControllerFactory {
    private static ControllerFactory INSTANCE;

    private final AuthController authController;
    private final FlightSearchController flightSearchController;
    private final BookingController bookingController;
    private final PaymentController paymentController;
    private final AdminFlightController adminFlightController;
    private final AgentController agentController;
    private final UserController userController;
    private final PromotionController promotionController;
    //todo add others later

    private ControllerFactory() {
        //setting up the basic controllers we need
        this.authController = new AuthController();
        this.flightSearchController = new FlightSearchController();
        this.bookingController = new BookingController();
        this.paymentController = new PaymentController(AppContext.getInstance().paymentGateway());
        this.adminFlightController = new AdminFlightController();
        this.agentController = new AgentController();
        this.userController = new UserController();
        this.promotionController = new PromotionController();
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
    public AgentController agent() { return agentController; }
    public UserController user() { return userController; }
    public PromotionController promotions() { return promotionController; }
    //todo expose others
}
