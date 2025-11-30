package src.views;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import src.DTO.FlightDTO;
import src.actions.AgentActions;
import src.components.BookingList;
import src.components.ThemeAware;
import src.components.UserBox;
import src.components.customer.FlightSearchPanel;
import src.config.Theme;

/**
 * Agent dashboard panel.
 *
 * Shares the reusable {@link FlightSearchPanel} table/filters with the
 * customer dashboard but arranges the layout differently and is wired
 * to agent-specific actions (e.g., synchronous search).
 */
public class AgentPanel extends MainPanel {

    private final AgentActions actions;

    // Core UI elements
    private JPanel headerPanel;
    private UserBox userBox;
    private BookingList workList;

    private JPanel activeArea;
    private FlightSearchPanel flightSearchPanel;

    public AgentPanel(AgentActions actions) {
        this.actions = actions;

        setLayout(new BorderLayout());
        buildHeader();
        buildActiveArea();

        // Temporary placeholder data until wired to real session/user info
        userBox.setUser("Agent Smith", "agent@example.com", "AGENT");
        workList.setBookings(List.of(
            "Pending change request #1234",
            "Seat upgrade inquiry #9821",
            "Group booking quote #4410"
        ));

        // Default content: show the reusable agent flight search view
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
        workList = new BookingList();

        headerPanel.add(userBox, BorderLayout.WEST);
        headerPanel.add(workList, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);
    }

    private void buildActiveArea() {
        activeArea = new JPanel(new BorderLayout());
        activeArea.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        activeArea.setOpaque(true);

        JLabel placeholder = new JLabel(
            "Use the search tools below to find and manage customer flights.",
            SwingConstants.CENTER
        );

        activeArea.add(placeholder, BorderLayout.CENTER);
        add(activeArea, BorderLayout.CENTER);
    }

    // -------------------------------------------------------------
    // VIEW FACTORIES / ACCESSORS
    // -------------------------------------------------------------
    /**
     * Lazily create and configure the agent flight search panel,
     * and keep a reference so we can switch back to it later.
     */
    private FlightSearchPanel getOrCreateFlightSearchPanel() {
        if (flightSearchPanel == null) {
            flightSearchPanel = new FlightSearchPanel(FlightSearchPanel.Mode.AGENT);
            flightSearchPanel.setSearchHandler((origin, dest, date) -> {
                if (actions != null) {
                    // Use the shared user-level search and push results
                    // directly into the reusable table component.
                    List<FlightDTO> flights = actions.searchFlights(origin, dest, date, null);
                    flightSearchPanel.setFlights(flights);
                }
            });

            // Example of adding an extra control that only agents see
            JButton manageButton = new JButton("Manage Selected Flight");
            flightSearchPanel.addExtraSearchControl(manageButton);
        }
        return flightSearchPanel;
    }

    /** Convenience method for controllers to make the agent search view active. */
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

    public void refreshWorkItems(List<String> workItems) {
        workList.setBookings(workItems);
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
        if (workList instanceof ThemeAware ta2) ta2.refreshTheme(t);

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


