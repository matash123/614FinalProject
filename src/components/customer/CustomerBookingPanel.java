package src.components.customer;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import src.config.Theme;
import src.controllers.BookingController;
import src.controllers.PromotionController;
import src.controllers.UserController;
import src.factory.ControllerFactory;
import src.models.Flight;
import src.models.Promotion;
import src.models.User;
import src.strategies.DefaultPricingStrategy;
import src.strategies.PricingStrategy;
import src.strategies.PromoPricingDecorator;
import src.views.DynamicPanel;
import src.views.PageController;

/**
 * Customer booking/payment panel shown inside the {@link src.views.CustomerPanel}
 * active area. It receives a selected {@link Flight} model and allows the customer
 * to enter card details and confirm the booking.
 *
 * All domain work is delegated to the appropriate controllers obtained from
 * {@link ControllerFactory} – this panel is purely UI + orchestration.
 */
public class CustomerBookingPanel extends DynamicPanel {

    private final BookingController bookingController;
    private final UserController userController;
    private final PromotionController promotionController;
    private final Flight flight;
    private final Runnable onBackToSearch;
    private PageController pageController;
    private double currentPrice;

    private JLabel titleLabel;
    private JLabel flightSummaryLabel;
    private JLabel priceLabel;
    private JTextField cardField;
    private JTextField promotionIdField;
    private JButton confirmButton;
    private JButton backButton;
    private JButton applyPromoButton;

    /**
     * @param flight the selected flight to book; assumed non-null.
     */
    public CustomerBookingPanel(Flight flight, Runnable onBackToSearch) {
        this.flight = flight;
        this.onBackToSearch = onBackToSearch;
        this.bookingController = ControllerFactory.getInstance().booking();
        this.userController = ControllerFactory.getInstance().user();
        this.promotionController = ControllerFactory.getInstance().promotions();
        this.currentPrice = flight.getPrice();

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        buildHeader();
        buildForm(onBackToSearch);
    }

    public void setPageController(PageController pageController) {
        this.pageController = pageController;
    }

    private void buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        titleLabel = new JLabel("Confirm your booking", JLabel.LEFT);
        flightSummaryLabel = new JLabel(
            String.format(
                "%s → %s on %s | %s",
                flight.getOrigin(),
                flight.getDestination(),
                flight.getDate() != null ? flight.getDate().toString() : "",
                flight.getAirline() != null ? flight.getAirline().getName() : ""
            ),
            JLabel.LEFT
        );
        priceLabel = new JLabel(
            String.format("Price: $%.2f", currentPrice),
            JLabel.LEFT
        );

        header.add(titleLabel, BorderLayout.NORTH);
        header.add(flightSummaryLabel, BorderLayout.CENTER);
        header.add(priceLabel, BorderLayout.SOUTH);

        add(header, BorderLayout.NORTH);
    }

    private void buildForm(Runnable onBackToSearch) {
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 8, 8, 8);
        c.fill = GridBagConstraints.HORIZONTAL;

        JLabel cardLabel = new JLabel("Card number:");
        cardField = new JTextField();

        JLabel promoLabel = new JLabel("Promotion ID (optional):");
        promotionIdField = new JTextField();

        confirmButton = new JButton("Pay & Book");
        backButton = new JButton("Back to search");
        applyPromoButton = new JButton("Apply Promotion");

        // Card label
        c.gridx = 0; c.gridy = 0;
        c.weightx = 0;
        form.add(cardLabel, c);

        // Card field
        c.gridx = 1; c.gridy = 0;
        c.weightx = 1.0;
        form.add(cardField, c);

        // Promotion label
        c.gridx = 0; c.gridy = 1;
        c.weightx = 0;
        form.add(promoLabel, c);

        // Promotion field and apply button
        JPanel promoPanel = new JPanel(new BorderLayout());
        promoPanel.add(promotionIdField, BorderLayout.CENTER);
        promoPanel.add(applyPromoButton, BorderLayout.EAST);
        c.gridx = 1; c.gridy = 1;
        c.weightx = 1.0;
        form.add(promoPanel, c);

        // Confirm button
        c.gridx = 0; c.gridy = 2;
        c.weightx = 0;
        form.add(confirmButton, c);

        // Back button
        c.gridx = 1; c.gridy = 2;
        c.weightx = 0;
        form.add(backButton, c);

        confirmButton.addActionListener(e -> handleConfirmBooking());
        backButton.addActionListener(e -> {
            if (onBackToSearch != null) {
                onBackToSearch.run();
            }
        });
        applyPromoButton.addActionListener(e -> handleApplyPromotion());

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

            // Get promotion ID if entered
            String promoId = promotionIdField.getText().trim();
            if (promoId.isBlank()) {
                promoId = null;
            }

            // The BookingController coordinates payment processing.
            boolean ok = bookingController.confirmBooking(cardNumber, flight, currentUser, seats, promoId);
            if (ok) {
                JOptionPane.showMessageDialog(
                    this,
                    "Your booking has been confirmed!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
                );
                // After a successful booking, return the user to flight search.
                if (onBackToSearch != null) {
                    onBackToSearch.run();
                }
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

    private void handleApplyPromotion() {
        String promoId = promotionIdField.getText().trim();
        if (promoId.isBlank()) {
            JOptionPane.showMessageDialog(
                this,
                "Please enter a promotion ID.",
                "Missing promotion ID",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        try {
            Promotion promo = promotionController.validateAndGetActivePromotion(promoId);
            if (promo == null) {
                JOptionPane.showMessageDialog(
                    this,
                    "Invalid or inactive promotion ID. Please check and try again.",
                    "Invalid Promotion",
                    JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            // Apply discount from promotion
            double discountPct = promo.getDiscountPercent();
            int discountPercentInt = (int)(discountPct * 100);
            PricingStrategy basePricing = new DefaultPricingStrategy();
            PricingStrategy promoPricing = new PromoPricingDecorator(basePricing, BigDecimal.valueOf(discountPct));
            BigDecimal discountedPrice = promoPricing.priceFor(flight, 1);
            currentPrice = discountedPrice.doubleValue();

            priceLabel.setText(String.format("Price: $%.2f (Promotion: %s applied - %d%% off)", 
                currentPrice, promo.getTitle(), discountPercentInt));
            
            JOptionPane.showMessageDialog(
                this,
                String.format("Promotion '%s' applied! New price: $%.2f", promo.getTitle(), currentPrice),
                "Promotion Applied",
                JOptionPane.INFORMATION_MESSAGE
            );
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                this,
                "Error applying promotion: " + ex.getMessage(),
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
        if (priceLabel != null) {
            priceLabel.setForeground(t.fg);
        }
        if (cardField != null) {
            cardField.setBackground(t.inputBg);
            cardField.setForeground(t.inputFg);
        }
        if (promotionIdField != null) {
            promotionIdField.setBackground(t.inputBg);
            promotionIdField.setForeground(t.inputFg);
        }
        if (applyPromoButton != null) {
            applyPromoButton.setBackground(t.buttonBg);
            applyPromoButton.setForeground(t.buttonFg);
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


