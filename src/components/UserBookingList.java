package src.components;

import java.awt.BorderLayout;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.JPanel;
import src.config.Theme;
import src.controllers.CustomerController;
import src.controllers.UserController;
import src.events.ControllerBus;
import src.events.ControllerBus.EventType;
import src.events.Observer;
import src.factory.ControllerFactory;
import src.models.Flight;
import src.models.Reservation;
import src.models.User;

/**
 * Controller-backed booking list component.
 *
 * - Renders a list of reservations for a given user
 * - By default, follows the currently logged-in user from {@link UserController}
 * - Listens to {@link EventType#RESERVATION_CREATED} and refreshes automatically
 *
 * Internally it delegates the actual rendering to the existing {@link BookingList}
 * component so this class focuses on orchestration and data loading.
 */
public class UserBookingList extends JPanel implements ThemeAware, Updatable, Observer {

    private final BookingList innerList;

    // Controllers
    private final UserController userController;
    private final CustomerController customerController;

    // Optional explicit user id; when null we fall back to the current session user.
    private String userIdOverride;

    public UserBookingList() {
        setLayout(new BorderLayout());
        setOpaque(true);

        this.innerList = new BookingList();
        this.userController = ControllerFactory.getInstance().user();
        this.customerController = ControllerFactory.getInstance().customer();

        add(innerList, BorderLayout.CENTER);

        // Initial load for the resolved user.
        refreshData();

        // Keep in sync with new reservations.
        ControllerBus.getInstance().subscribe(EventType.RESERVATION_CREATED, this);
    }

    /**
     * Backwards-compatible hook so existing callers that want to push in
     * preformatted booking strings can still do so directly.
     */
    public void setBookings(List<String> bookings) {
        innerList.setBookings(bookings != null ? bookings : List.of());
    }

    /**
     * Optionally pin this component to a specific user id.
     * If not set, the component will use the currently logged-in user.
     */
    public void setUserId(String userId) {
        this.userIdOverride = userId;
        refreshData();
    }

    @Override
    public void refreshData() {
        System.out.println("refreshing data");
        String userId = resolveUserId();
        if (userId == null || userId.isBlank()) {
            innerList.setBookings(List.of());
            return;
        }

        try {
            List<Reservation> reservations =
                customerController.getReservations(userId);
            innerList.setBookings(formatReservations(reservations));
        } catch (Exception ex) {
            innerList.setBookings(List.of());
        }
    }

    private String resolveUserId() {
        if (userIdOverride != null && !userIdOverride.isBlank()) {
            return userIdOverride;
        }
        User currentUser = userController.getCurrentUser();
        return currentUser != null ? currentUser.getUserId() : null;
    }

    private List<String> formatReservations(List<Reservation> reservations) {
        if (reservations == null || reservations.isEmpty()) {
            return List.of();
        }

        return reservations.stream()
            .map(r -> {
                Flight f = r.getFlight();
                String origin = (f != null && f.getOrigin() != null) ? f.getOrigin() : "";
                String dest   = (f != null && f.getDestination() != null) ? f.getDestination() : "";
                String date   = (f != null && f.getDate() != null) ? f.getDate().toString() : "";
                String status = (r.getStatus() != null) ? r.getStatus().name() : "";
                int seats     = r.getSeats();

                String seatLabel = seats == 1 ? "seat" : "seats";

                return String.format(
                    "%s - %s → %s | %s | %d %s | %s",
                    r.getReservationId(),
                    origin,
                    dest,
                    date,
                    seats,
                    seatLabel,
                    status
                );
            })
            .collect(Collectors.toList());
    }

    // ---------------------------------------------------------------------
    // Theme support – delegate to inner list
    // ---------------------------------------------------------------------
    @Override
    public void refreshTheme(Theme t) {
        setBackground(t.bg);
        if (innerList instanceof ThemeAware ta) {
            ta.refreshTheme(t);
        }
    }

    // ---------------------------------------------------------------------
    // Observer implementation – reload on new reservation
    // ---------------------------------------------------------------------
    @Override
    public void update(Object event) {
        if (!(event instanceof Reservation)) {
            return;
        }

        String userId = resolveUserId();
        if (userId == null || userId.isBlank()) {
            return;
        }

        Reservation r = (Reservation) event;
        User u = r.getUser();
        if (u == null || u.getUserId() == null) {
            return;
        }

        if (!userId.equals(u.getUserId())) {
            // Reservation is for a different user; ignore.
            return;
        }

        refreshData();
    }
}


