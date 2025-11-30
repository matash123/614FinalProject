package src.views;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import src.components.BookingList;
import src.components.ThemeAware;
import src.components.UserBox;
import src.components.admin.AdminFlightEditorPanel;
import src.components.admin.PromotionalNewsEditor;
import src.components.customer.FlightSearchPanel;
import src.config.Theme;
import src.factory.ControllerFactory;
import src.models.Flight;
import src.models.User;

/**
 * Admin dashboard panel.
 *
 * Shares the reusable {@link FlightSearchPanel} table/filters with the
 * customer and agent dashboards but is wired to admin-specific actions
 * (e.g., editing flights themselves instead of bookings).
 */
public class AdminPanel extends DynamicPanel {

    // Core UI elements
    private JPanel headerPanel;
    private UserBox userBox;
    private BookingList taskList;

    private JPanel activeArea;
    private FlightSearchPanel flightSearchPanel;

    // Last theme applied to this panel; reused when swapping active views.
    private Theme currentTheme;

    // Page controller for switching the active center panel.
    private final PageController pageController;

    public AdminPanel() {
        setLayout(new BorderLayout());

        this.pageController = panel -> setActiveView(panel);

        buildHeader();
        buildActiveArea();

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

        //buttons panel for navigation
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setOpaque(false);
        JButton flightSearchButton = new JButton("Flight Search");
        JButton promotionsButton = new JButton("Promotions / News");

        flightSearchButton.addActionListener(e -> showFlightSearch());
        promotionsButton.addActionListener(e -> {
            PromotionalNewsEditor editor = new PromotionalNewsEditor();
            editor.setPageController(pageController);
            setActiveView(editor);
        });

        buttonsPanel.add(flightSearchButton);
        buttonsPanel.add(promotionsButton);

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setOpaque(false);
        leftPanel.add(userBox, BorderLayout.CENTER);
        leftPanel.add(buttonsPanel, BorderLayout.SOUTH);

        headerPanel.add(leftPanel, BorderLayout.WEST);
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
            flightSearchPanel.setPageController(pageController);

            // Extra control that only admins see – jump to flight editor
            JButton editFlightButton = new JButton("Edit selected flight");
            editFlightButton.addActionListener(e -> {
                Flight selected = flightSearchPanel.getSelectedFlight();
                if (selected == null) {
                    JOptionPane.showMessageDialog(
                        this,
                        "Please select a flight to edit.",
                        "No flight selected",
                        JOptionPane.WARNING_MESSAGE
                    );
                    return;
                }

                AdminFlightEditorPanel editor = new AdminFlightEditorPanel(
                    selected,
                    this::showFlightSearch
                );
                editor.setPageController(pageController);
                // Show the admin flight editor inside this panel's active area.
                setActiveView(editor);
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
    public void setActiveView(DynamicPanel p) {
        activeArea.removeAll();
        activeArea.add(p, BorderLayout.CENTER);
        activeArea.revalidate();
        activeArea.repaint();

        if (currentTheme != null) {
            p.refreshTheme(currentTheme);
        }
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
        this.currentTheme = t;
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

    @Override
    public void refreshData() {
        // Update header user box based on the currently logged-in user.
        User currentUser = ControllerFactory.getInstance()
                .user()
                .getCurrentUser();
        if (currentUser != null) {
            String name = currentUser.getName() != null ? currentUser.getName() : currentUser.getUserId();
            String email = currentUser.getEmail() != null ? currentUser.getEmail() : "";
            String role = currentUser.getRole() != null ? currentUser.getRole() : "";
            userBox.setUser(name, email, role);
        }
    }
}
