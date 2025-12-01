package src.components.customer;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import src.config.Theme;
import src.controllers.BookingController;
import src.controllers.UserController;
import src.factory.ControllerFactory;
import src.models.Flight;
import src.models.Reservation;
import src.models.User;
import src.views.DynamicPanel;
import src.views.PageController;

/**
 * Customer reservation editor panel shown inside the customer dashboard
 * active area (same pattern as {@link CustomerBookingPanel}).
 *
 * This panel is intentionally lightweight:
 * - It receives an existing {@link Reservation} to edit
 * - Shows a read-only summary of the flight
 * - Allows the customer to adjust the seat count and/or cancel
 * - Persists changes via {@link ReservationCRUD}
 *
 * Domain validation (ownership checks, etc.) is performed via controllers
 * where appropriate; this class focuses on UI + orchestration.
 */
public class CustomerReservationEditor extends DynamicPanel {

    private final Reservation reservation;

    private final UserController userController;
    private final BookingController bookingController;

    private PageController pageController;

    private JLabel titleLabel;
    private JLabel reservationSummaryLabel;
    private JTextField seatsField;
    private JButton saveButton;
    private JButton cancelReservationButton;
    private JButton backButton;

    /**
     * @param reservation the reservation to edit; assumed non-null.
     */
    public CustomerReservationEditor(Reservation reservation) {
        if (reservation == null) {
            throw new IllegalArgumentException("reservation is required");
        }
        this.reservation = reservation;
        this.userController = ControllerFactory.getInstance().user();
        this.bookingController = ControllerFactory.getInstance().booking();

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        buildHeader();
        buildForm();
        buildActions();
    }

    public void setPageController(PageController pageController) {
        this.pageController = pageController;
    }

    private void buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        // Let the themed parent background show through (avoids bright boxes in dark mode).
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        titleLabel = new JLabel("Edit reservation", JLabel.LEFT);

        Flight f = reservation.getFlight();
        String flightId = f != null ? f.getFlightId() : "";
        String origin = f != null ? f.getOrigin() : "";
        String dest = f != null ? f.getDestination() : "";
        String date = (f != null && f.getDate() != null) ? f.getDate().toString() : "";

        String status = reservation.getStatus() != null
            ? reservation.getStatus().name()
            : "";

        reservationSummaryLabel = new JLabel(
            String.format(
                "Reservation %s | Flight %s: %s → %s on %s | Status: %s",
                reservation.getReservationId(),
                flightId,
                origin,
                dest,
                date,
                status
            ),
            JLabel.LEFT
        );

        header.add(titleLabel, BorderLayout.NORTH);
        header.add(reservationSummaryLabel, BorderLayout.SOUTH);

        add(header, BorderLayout.NORTH);
    }

    private void buildForm() {
        JPanel form = new JPanel(new GridBagLayout());
        // Transparent so the surrounding themed panel controls the background color.
        form.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 8, 8, 8);
        c.fill = GridBagConstraints.HORIZONTAL;

        JLabel seatsLabel = new JLabel("Seats:");
        seatsField = new JTextField(Integer.toString(reservation.getSeats()));

        // Seats label
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0;
        form.add(seatsLabel, c);

        // Seats field
        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 1.0;
        form.add(seatsField, c);

        add(form, BorderLayout.CENTER);
    }

    private void buildActions() {
        JPanel actions = new JPanel();
        // Match the themed background instead of default bright panel color.
        actions.setOpaque(false);

        saveButton = new JButton("Save changes");
        cancelReservationButton = new JButton("Cancel reservation");
        backButton = new JButton("Back");

        saveButton.addActionListener(e -> handleSave());
        cancelReservationButton.addActionListener(e -> handleCancelReservation());
        backButton.addActionListener(e -> {
            // Let the broader app decide what to show next; at minimum we can
            // ask the current view to refresh itself.
            src.controllers.AppController app = src.controllers.AppController.getInstance();
            if (app != null) {
                app.updateAppView();
            }
        });

        actions.add(saveButton);
        actions.add(cancelReservationButton);
        actions.add(backButton);

        add(actions, BorderLayout.SOUTH);
    }

    private boolean ensureUserOwnsReservation() {
        User current = userController.getCurrentUser();
        if (current == null) {
            JOptionPane.showMessageDialog(
                this,
                "You must be logged in to edit reservations.",
                "Not logged in",
                JOptionPane.ERROR_MESSAGE
            );
            return false;
        }
        if (!current.getUserId().equals(reservation.getUser().getUserId())) {
            JOptionPane.showMessageDialog(
                this,
                "You can only edit your own reservations.",
                "Permission denied",
                JOptionPane.WARNING_MESSAGE
            );
            return false;
        }
        return true;
    }

    private void handleSave() {
        if (!ensureUserOwnsReservation()) {
            return;
        }

        String seatsText = seatsField.getText().trim();
        int newSeats;
        try {
            newSeats = Integer.parseInt(seatsText);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                this,
                "Seats must be a valid positive number.",
                "Invalid seats",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (newSeats <= 0) {
            JOptionPane.showMessageDialog(
                this,
                "Seats must be at least 1.",
                "Invalid seats",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        try {
            User current = userController.getCurrentUser();
            if (current == null) {
                throw new IllegalStateException("No current user in session");
            }

            bookingController.updateReservationSeats(
                reservation.getReservationId(),
                newSeats,
                current
            );

            JOptionPane.showMessageDialog(
                this,
                "Reservation updated successfully.",
                "Success",
                JOptionPane.INFORMATION_MESSAGE
            );

            // After a successful edit, send the customer back to flight search.
            if (pageController != null) {
                FlightSearchPanel searchPanel =
                    new FlightSearchPanel(FlightSearchPanel.Mode.CUSTOMER);
                searchPanel.setPageController(pageController);
                pageController.show(searchPanel);
            }

        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(
                this,
                "Error while updating reservation: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void handleCancelReservation() {
        if (!ensureUserOwnsReservation()) {
            return;
        }

        int choice = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to cancel this reservation?",
            "Cancel reservation",
            JOptionPane.YES_NO_OPTION
        );

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            User current = userController.getCurrentUser();
            if (current == null) {
                throw new IllegalStateException("No current user in session");
            }

            bookingController.cancelReservation(
                reservation.getReservationId(),
                current
            );

            JOptionPane.showMessageDialog(
                this,
                "Reservation cancelled.",
                "Cancelled",
                JOptionPane.INFORMATION_MESSAGE
            );

            if (pageController != null) {
                FlightSearchPanel searchPanel =
                    new FlightSearchPanel(FlightSearchPanel.Mode.CUSTOMER);
                searchPanel.setPageController(pageController);
                pageController.show(searchPanel);
            }

        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(
                this,
                "Error while cancelling reservation: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    @Override
    public void refreshTheme(Theme t) {
        setBackground(t.bg);

        if (titleLabel != null) {
            titleLabel.setForeground(t.fg);
        }
        if (reservationSummaryLabel != null) {
            reservationSummaryLabel.setForeground(t.fg);
        }
        if (seatsField != null) {
            seatsField.setBackground(t.inputBg);
            seatsField.setForeground(t.inputFg);
        }
        if (saveButton != null) {
            saveButton.setBackground(t.buttonBg);
            saveButton.setForeground(t.buttonFg);
        }
        if (cancelReservationButton != null) {
            cancelReservationButton.setBackground(t.buttonBg);
            cancelReservationButton.setForeground(t.buttonFg);
        }
        if (backButton != null) {
            backButton.setBackground(t.buttonBg);
            backButton.setForeground(t.buttonFg);
        }

        repaint();
    }
}


