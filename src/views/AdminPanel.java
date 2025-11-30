package src.views;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import src.components.BookingList;
import src.components.ThemeAware;
import src.components.UserBox;
import src.components.customer.FlightSearchPanel;
import src.config.Theme;

/**
 * Admin dashboard panel.
 *
 * Shares the reusable {@link FlightSearchPanel} table/filters with the
 * customer and agent dashboards but is wired to admin-specific actions
 * (e.g., editing flights themselves instead of bookings).
 */
public class AdminPanel extends MainPanel {

    // Core UI elements
    private JPanel headerPanel;
    private UserBox userBox;
    private BookingList taskList;

    private JPanel activeArea;
    private FlightSearchPanel flightSearchPanel;

    public AdminPanel() {
        setLayout(new BorderLayout());
        buildHeader();
        buildActiveArea();

        // Temporary placeholder data until wired to real session/user info
        userBox.setUser("System Admin", "admin@example.com", "ADMIN");
        taskList.setBookings(List.of(
            "Review schedule changes",
            "Audit recent reservations",
            "Manage airline & airplane catalog"
        ));

        // Default content: show the reusable admin flight search view
        showFlightSearch();
    }

    // -------------------------------------------------------------
    // BUILD SECTIONS
    // -------------------------------------------------------------
    private void buildHeader() {
        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setPreferredSize(new Dimension(0, 140));
        headerPanel.setOpaque(true);

        userBox = new UserBox();
        taskList = new BookingList();

        headerPanel.add(userBox, BorderLayout.WEST);
        headerPanel.add(taskList, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);
    }

    private void buildActiveArea() {
        activeArea = new JPanel(new BorderLayout());
        activeArea.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        activeArea.setOpaque(true);

        JLabel placeholder = new JLabel(
            "Use the search tools below to inspect and manage flights.",
            SwingConstants.CENTER
        );

        activeArea.add(placeholder, BorderLayout.CENTER);
        add(activeArea, BorderLayout.CENTER);
    }

    // -------------------------------------------------------------
    // VIEW FACTORIES / ACCESSORS
    // -------------------------------------------------------------
    /**
     * Lazily create and configure the admin flight search panel,
     * and keep a reference so we can switch back to it later.
     */
    private FlightSearchPanel getOrCreateFlightSearchPanel() {
        if (flightSearchPanel == null) {
            flightSearchPanel = new FlightSearchPanel(FlightSearchPanel.Mode.ADMIN);

            // Extra control that only admins see – jump to flight editor
            JButton editFlightButton = new JButton("Edit selected flight");
            editFlightButton.addActionListener(e -> {
                // Show the admin flight editor inside this panel's active area.
                setActiveView(new AdminFlightEditorPanel());
            });
            flightSearchPanel.addExtraSearchControl(editFlightButton);
        }
        return flightSearchPanel;
    }

    /** Convenience method for controllers to make the admin search view active. */
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

    public void refreshTasks(List<String> tasks) {
        taskList.setBookings(tasks);
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
        if (taskList instanceof ThemeAware ta2) ta2.refreshTheme(t);

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
