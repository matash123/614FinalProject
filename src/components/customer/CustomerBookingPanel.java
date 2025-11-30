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
import src.components.ThemeAware;
import src.config.Theme;
import src.controllers.BookingController;
import src.controllers.UserController;
import src.factory.ControllerFactory;
import src.models.Flight;
import src.models.User;

/**
 * Customer booking/payment panel shown inside the {@link src.views.CustomerPanel}
 * active area. It receives a selected {@link Flight} model and allows the customer
 * to enter card details and confirm the booking.
 *
 * All domain work is delegated to the appropriate controllers obtained from
 * {@link ControllerFactory} – this panel is purely UI + orchestration.
 */
public class CustomerBookingPanel extends JPanel implements ThemeAware {

    private final BookingController bookingController;
    private final UserController userController;
    private final Flight flight;

    private JLabel titleLabel;
    private JLabel flightSummaryLabel;
    private JTextField cardField;
    private JButton confirmButton;
    private JButton backButton;

    /**
     * @param flight the selected flight to book; assumed non-null.
     */
    public CustomerBookingPanel(Flight flight, Runnable onBackToSearch) {
        this.flight = flight;
        this.bookingController = ControllerFactory.getInstance().booking();
        this.userController = ControllerFactory.getInstance().user();

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        buildHeader();
        buildForm(onBackToSearch);
    }

    private void buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        titleLabel = new JLabel("Confirm your booking", JLabel.LEFT);
        flightSummaryLabel = new JLabel(
            String.format(
                "%s → %s on %s | %s | $%.2f",
                flight.getOrigin(),
                flight.getDestination(),
                flight.getDate() != null ? flight.getDate().toString() : "",
                flight.getAirline() != null ? flight.getAirline().getName() : "",
                flight.getPrice()
            ),
            JLabel.LEFT
        );

        header.add(titleLabel, BorderLayout.NORTH);
        header.add(flightSummaryLabel, BorderLayout.SOUTH);

        add(header, BorderLayout.NORTH);
    }

    private void buildForm(Runnable onBackToSearch) {
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 8, 8, 8);
        c.fill = GridBagConstraints.HORIZONTAL;

        JLabel cardLabel = new JLabel("Card number:");
        cardField = new JTextField();

        confirmButton = new JButton("Pay & Book");
        backButton = new JButton("Back to search");

        // Card label
        c.gridx = 0; c.gridy = 0;
        c.weightx = 0;
        form.add(cardLabel, c);

        // Card field
        c.gridx = 1; c.gridy = 0;
        c.weightx = 1.0;
        form.add(cardField, c);

        // Confirm button
        c.gridx = 0; c.gridy = 1;
        c.weightx = 0;
        form.add(confirmButton, c);

        // Back button
        c.gridx = 1; c.gridy = 1;
        c.weightx = 0;
        form.add(backButton, c);

        confirmButton.addActionListener(e -> handleConfirmBooking());
        backButton.addActionListener(e -> {
            if (onBackToSearch != null) {
                onBackToSearch.run();
            }
        });

        add(form, BorderLayout.CENTER);
    }

    private void handleConfirmBooking() {
        String cardNumber = cardField.getText().trim();
        if (cardNumber.isBlank()) {
            JOptionPane.showMessageDialog(
                this,
                "Please enter a card number.",
                "Missing information",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        User currentUser = userController.getCurrentUser();
        if (currentUser == null) {
            JOptionPane.showMessageDialog(
                this,
                "You must be logged in as a customer to book.",
                "Not logged in",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }

    
        if (flight == null) {
            JOptionPane.showMessageDialog(
                this,
                "The selected flight could not be found. Please refresh and try again.",
                "Flight missing",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        try {
            // For now we always book 1 seat; this can be extended later.
            int seats = 1;

            // The BookingController already coordinates payment via AppContext's gateway.
            boolean ok = bookingController.confirmBooking(cardNumber, flight, currentUser, seats);
            if (ok) {
                JOptionPane.showMessageDialog(
                    this,
                    "Your booking has been confirmed!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
                );
            } else {
                JOptionPane.showMessageDialog(
                    this,
                    "Booking could not be completed.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                this,
                "Error while processing booking: " + ex.getMessage(),
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
        if (flightSummaryLabel != null) {
            flightSummaryLabel.setForeground(t.fg);
        }
        if (cardField != null) {
            cardField.setBackground(t.inputBg);
            cardField.setForeground(t.inputFg);
        }
        if (confirmButton != null) {
            confirmButton.setBackground(t.buttonBg);
            confirmButton.setForeground(t.buttonFg);
        }
        if (backButton != null) {
            backButton.setBackground(t.buttonBg);
            backButton.setForeground(t.buttonFg);
        }

        repaint();
    }
}


