package src.views;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import src.components.BookingList;
import src.components.ThemeAware;
import src.components.UserBox;
import src.components.agent.AgentUserListPanel;
import src.components.agent.AgentUserReservationsPanel;
import src.components.agent.FlightReservationsEditorPanel;
import src.components.customer.FlightSearchPanel;
import src.config.Theme;
import src.models.Flight;

/**
 * Agent dashboard panel.
 *
 * Shares the reusable {@link FlightSearchPanel} table/filters with the
 * customer dashboard but arranges the layout differently and is wired
 * to agent-specific actions (e.g., synchronous search).
 */
public class AgentPanel extends DynamicPanel {

    // Core UI elements
    private JPanel headerPanel;
    private UserBox userBox;
    private BookingList workList;

    private JPanel activeArea;
    private FlightSearchPanel flightSearchPanel;

    // Last theme applied to this panel; reused when swapping active views.
    private Theme currentTheme;

    // Page controller for switching the active center panel.
    private final PageController pageController;

    public AgentPanel() {
        setLayout(new BorderLayout());

        this.pageController = panel -> setActiveView(panel);

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

        //left side: user box + flight search button
        JPanel leftButtonsPanel = new JPanel();
        leftButtonsPanel.setOpaque(false);
        JButton flightSearchButton = new JButton("Flight Search");
        flightSearchButton.addActionListener(e -> showFlightSearch());
        leftButtonsPanel.add(flightSearchButton);

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setOpaque(false);
        leftPanel.add(userBox, BorderLayout.CENTER);
        leftPanel.add(leftButtonsPanel, BorderLayout.SOUTH);

        headerPanel.add(leftPanel, BorderLayout.WEST);

        // Right side: work list + agent tools (user list, reservation search)
        JPanel right = new JPanel(new BorderLayout());
        right.setOpaque(false);

        JButton viewUsersButton = new JButton("View all active users");
        viewUsersButton.addActionListener(e -> {
            AgentUserListPanel userListPanel = new AgentUserListPanel();
            // Wire the shared PageController so the list panel can open
            // a reservations view for the selected user.
            userListPanel.setPageController(pageController);
            pageController.show(userListPanel);
        });

        JButton searchReservationsButton = new JButton("Search reservations by user ID");
        searchReservationsButton.addActionListener(e -> {
            AgentUserReservationsPanel panel = new AgentUserReservationsPanel();
            pageController.show(panel);
        });

        JPanel buttons = new JPanel();
        buttons.setOpaque(false);
        buttons.add(viewUsersButton);
        buttons.add(searchReservationsButton);

        right.add(workList, BorderLayout.CENTER);
        right.add(buttons, BorderLayout.SOUTH);

        headerPanel.add(right, BorderLayout.EAST);

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
            flightSearchPanel.setPageController(pageController);

            // Extra control that only agents see – view reservations for selected flight
            JButton viewReservationsButton = new JButton("View reservations for selected flight");
            viewReservationsButton.addActionListener(e -> {
                Flight selected = flightSearchPanel.getSelectedFlight();
                if (selected == null) {
                    JOptionPane.showMessageDialog(
                        this,
                        "Please select a flight to view reservations.",
                        "No flight selected",
                        JOptionPane.WARNING_MESSAGE
                    );
                    return;
                }
                FlightReservationsEditorPanel panel = new FlightReservationsEditorPanel(selected);
                pageController.show(panel);
            });
            flightSearchPanel.addExtraSearchControl(viewReservationsButton);
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
    public void setActiveView(DynamicPanel p) {
        activeArea.removeAll();
        activeArea.add(p, BorderLayout.CENTER);
        activeArea.revalidate();

        // Apply the currently active theme (if any) to the new view immediately.
        if (currentTheme != null) {
            p.refreshTheme(currentTheme);
        }

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
        this.currentTheme = t;
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


