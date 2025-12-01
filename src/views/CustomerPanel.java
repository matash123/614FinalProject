package src.views;

import java.awt.*;
import javax.swing.*;
import src.components.AccountEditorPanel;
import src.components.ThemeAware;
import src.components.UserBox;
import src.components.customer.CustomerBookingPanel;
import src.components.customer.CustomerPromotionsPanel;
import src.components.customer.CustomerReservationsPanel;
import src.components.customer.FlightSearchPanel;
import src.config.Theme;
import src.factory.ControllerFactory;
import src.models.Flight;
import src.models.User;


/**
 * CustomerDashboard – full user dashboard with:
 * - Top header: user info + booking list
 * - Center dynamic area
 */
public class CustomerPanel extends DynamicPanel {

    // Core UI elements
    private FlightSearchPanel flightSearchPanel;
    private JPanel headerPanel;
    private UserBox userBox;
    private CustomerReservationsPanel reservationsPanel;

    private JPanel activeArea;

    // Last theme applied to this panel; reused when swapping active views.
    private Theme currentTheme;

    // Page controller for switching the active center panel.
    private final PageController pageController;

    public CustomerPanel() {
        setLayout(new BorderLayout());

        this.pageController = panel -> setActiveView(panel);

        buildHeader();
        buildActiveArea();

        // Default content: show the reusable customer flight search view
        showFlightSearch();
    }

    // -------------------------------------------------------------
    // BUILD SECTIONS
    // -------------------------------------------------------------
    private void buildHeader() {
        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setPreferredSize(new Dimension(0, 140));
        headerPanel.setOpaque(true);

        // New modular components
        userBox = new UserBox();
        reservationsPanel = new CustomerReservationsPanel();
        reservationsPanel.setPageController(pageController);

        // Flight search + account buttons
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setOpaque(false);

        JButton flightSearchButton = new JButton("Flight Search");
        flightSearchButton.addActionListener(e -> showFlightSearch());
        buttonsPanel.add(flightSearchButton);

        JButton promotionsButton = new JButton("Promotions");
        promotionsButton.addActionListener(e -> showPromotions());
        buttonsPanel.add(promotionsButton);

        JButton accountButton = new JButton("My Account");
        accountButton.addActionListener(e -> {
            AccountEditorPanel panel = new AccountEditorPanel(AccountEditorPanel.Mode.SELF);
            panel.setPageController(pageController);
            setActiveView(panel);
        });
        buttonsPanel.add(accountButton);

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setOpaque(false);
        leftPanel.add(userBox, BorderLayout.CENTER);
        leftPanel.add(buttonsPanel, BorderLayout.SOUTH);

        headerPanel.add(leftPanel, BorderLayout.WEST);
        headerPanel.add(reservationsPanel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);
    }

    private void buildActiveArea() {
        activeArea = new JPanel(new BorderLayout());
        activeArea.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        activeArea.setOpaque(true);

        JLabel placeholder = new JLabel(
            "Select an option, flight, or task to continue.",
            SwingConstants.CENTER
        );

        activeArea.add(placeholder, BorderLayout.CENTER);
        add(activeArea, BorderLayout.CENTER);
    }

    // -------------------------------------------------------------
    // VIEW FACTORIES / ACCESSORS
    // -------------------------------------------------------------
    /**
     * Lazily create and configure the customer flight search panel,
     * and keep a reference so we can switch back to it later.
     */
    private FlightSearchPanel getOrCreateFlightSearchPanel() {
        if (flightSearchPanel == null) {
            flightSearchPanel = new FlightSearchPanel(FlightSearchPanel.Mode.CUSTOMER);
            flightSearchPanel.setPageController(pageController);

            // Extra control for customers – jump to booking for selected flight
            JButton bookButton = new JButton("Book selected flight");
            bookButton.addActionListener(e -> {
                Flight selected = flightSearchPanel.getSelectedFlight();
                if (selected == null) {
                    JOptionPane.showMessageDialog(
                        this,
                        "Please select a flight to book.",
                        "No flight selected",
                        JOptionPane.WARNING_MESSAGE
                    );
                    return;
                }
                // Switch the active area to the booking panel, passing the Flight model.
                CustomerBookingPanel bookingPanel = new CustomerBookingPanel(
                    selected,
                    this::showFlightSearch
                );
                bookingPanel.setPageController(pageController);
                setActiveView(bookingPanel);
            });
            flightSearchPanel.addExtraSearchControl(bookButton);
        }
        return flightSearchPanel;
    }

    /** Convenience method to make the search view active. */
    public void showFlightSearch() {
        setActiveView(getOrCreateFlightSearchPanel());
    }

    /** Show the promotions panel. */
    public void showPromotions() {
        CustomerPromotionsPanel promotionsPanel = new CustomerPromotionsPanel();
        promotionsPanel.setPageController(pageController);
        setActiveView(promotionsPanel);
    }

    // -------------------------------------------------------------
    // DYNAMIC UPDATE METHODS
    // -------------------------------------------------------------
    public void setActiveView(DynamicPanel p) {
        activeArea.removeAll();
        activeArea.add(p, BorderLayout.CENTER);
        activeArea.revalidate();
        activeArea.repaint();

        // Ensure the newly active view picks up the current theme immediately.
        if (currentTheme != null) {
            p.refreshTheme(currentTheme);
        }

        p.refreshData();
    }

    public void refreshUser(String name, String email, String role) {

        userBox.setUser(name, email, role);
    }

    // -------------------------------------------------------------
    // THEME HANDLING
    // -------------------------------------------------------------
    @Override
    public void refreshTheme(Theme t) {
        this.currentTheme = t;
        setBackground(t.bg);
        headerPanel.setBackground(t.bg);
        activeArea.setBackground(t.bg);

        // Refresh header components
        if (userBox instanceof ThemeAware ta1) ta1.refreshTheme(t);
        if (reservationsPanel instanceof ThemeAware ta2) ta2.refreshTheme(t);

        // Refresh active component inside activeArea
        if (activeArea.getComponentCount() > 0) {
            Component c = activeArea.getComponent(0);
            if (c instanceof ThemeAware ta3) {
                ta3.refreshTheme(t);
            }
        }

        repaint();
    }

    @Override
    public void refreshData() {
        // Hard-code updates to header components that should react to
        // domain events (e.g., new bookings for the current user).
        User currentUser = ControllerFactory.getInstance()
                .user()
                .getCurrentUser();
        if (currentUser != null) {
            String name = currentUser.getName() != null ? currentUser.getName() : currentUser.getUserId();
            String email = currentUser.getEmail() != null ? currentUser.getEmail() : "";
            String role = currentUser.getRole() != null ? currentUser.getRole() : "";
            userBox.setUser(name, email, role);
        }

        // Header reservations table will pull data from controllers/CRUD.
        if (reservationsPanel != null) {
            reservationsPanel.refreshData();
        }
    }
}
