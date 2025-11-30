package src.views;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import src.components.BookingList;
import src.components.ThemeAware;
import src.components.UserBox;
import src.components.customer.CustomerBookingPanel;
import src.components.customer.FlightSearchPanel;
import src.config.Theme;
import src.models.Flight;


/**
 * CustomerDashboard – full user dashboard with:
 * - Top header: user info + booking list
 * - Center dynamic area
 */
public class CustomerPanel extends MainPanel {

    // Core UI elements
    private FlightSearchPanel flightSearchPanel;
    private JPanel headerPanel;
    private UserBox userBox;
    private BookingList bookingList;


    private JPanel activeArea;

    public CustomerPanel() {
        setLayout(new BorderLayout());
        buildHeader();
        buildActiveArea();

        // Temporary placeholder initialization until actions layer is ready
        userBox.setUser("John Doe", "john@example.com", "CUSTOMER");
        bookingList.setBookings(List.of(
            "YYC → YVR | 2025-12-01",
            "YYC → LAX | 2025-12-10",
            "YVR → NRT | 2026-01-03"
        ));

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
        bookingList = new BookingList();

        headerPanel.add(userBox, BorderLayout.WEST);
        headerPanel.add(bookingList, BorderLayout.EAST);

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
                setActiveView(new CustomerBookingPanel(
                    selected,
                    this::showFlightSearch
                ));
            });
            flightSearchPanel.addExtraSearchControl(bookButton);
        }
        return flightSearchPanel;
    }

    /** Convenience method to make the search view active. */
    public void showFlightSearch() {
        setActiveView(getOrCreateFlightSearchPanel());
    }

    // -------------------------------------------------------------
    // DYNAMIC UPDATE METHODS
    // -------------------------------------------------------------
    public void setActiveView(JPanel p) {
        activeArea.removeAll();
        activeArea.add(p, BorderLayout.CENTER);
        activeArea.revalidate();
        activeArea.repaint();
    }

    public void refreshUser(String name, String email, String role) {
        userBox.setUser(name, email, role);
    }

    public void refreshBookings(List<String> bookings) {
        bookingList.setBookings(bookings);
    }

    // -------------------------------------------------------------
    // THEME HANDLING
    // -------------------------------------------------------------
    @Override
    public void refreshTheme(Theme t) {
        setBackground(t.bg);
        headerPanel.setBackground(t.bg);
        activeArea.setBackground(t.bg);

        // Refresh header components
        if (userBox instanceof ThemeAware ta1) ta1.refreshTheme(t);
        if (bookingList instanceof ThemeAware ta2) ta2.refreshTheme(t);

        // Refresh active component inside activeArea
        if (activeArea.getComponentCount() > 0) {
            Component c = activeArea.getComponent(0);
            if (c instanceof ThemeAware ta3) {
                ta3.refreshTheme(t);
            }
        }

        repaint();
    }

}
