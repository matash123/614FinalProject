package src.components.agent;

import java.awt.BorderLayout;
import java.awt.Font;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import src.components.ReservationTablePanel;
import src.config.Theme;
import src.controllers.AgentController;
import src.database.ReservationCRUD;
import src.factory.ControllerFactory;
import src.models.Reservation;
import src.models.ReservationStatus;
import src.views.DynamicPanel;
import src.views.PageController;

/**
 * Agent-facing panel that shows reservations for a given user (customer)
 * and allows the agent to remove (cancel) bookings.
 *
 * Can be opened either "blank" (agent types a user ID and searches) or
 * pre-populated with a specific user ID from the user list.
 */
public class AgentUserReservationsPanel extends DynamicPanel {

    private final String initialUserId;

    private PageController pageController;

    private final AgentController agentController;

    private final JLabel titleLabel;
    private final JTextField userIdField;
    private final JButton searchButton;
    private final ReservationTablePanel reservationTablePanel;
    private final JButton removeButton;
    private final JButton changeStatusButton;

    private List<Reservation> currentReservations;

    public AgentUserReservationsPanel() {
        this(null);
    }

    public AgentUserReservationsPanel(String initialUserId) {
        this.initialUserId = initialUserId;
        this.agentController = ControllerFactory.getInstance().agent();

        setLayout(new BorderLayout());

        // Header: title + simple search bar for user ID
        JPanel header = new JPanel(new BorderLayout(8, 0));

        titleLabel = new JLabel("Reservations by user", JLabel.LEFT);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));

        JPanel searchBar = new JPanel(new BorderLayout(4, 0));
        JLabel userIdLabel = new JLabel("User ID:");
        userIdField = new JTextField(12);
        searchButton = new JButton("Search");

        searchBar.add(userIdLabel, BorderLayout.WEST);
        searchBar.add(userIdField, BorderLayout.CENTER);
        searchButton.addActionListener(e -> refreshData());
        searchBar.add(searchButton, BorderLayout.EAST);

        header.add(titleLabel, BorderLayout.WEST);
        header.add(searchBar, BorderLayout.EAST);

        // Table area
        reservationTablePanel = new ReservationTablePanel();

        // Bottom actions
        removeButton = new JButton("Remove selected booking");
        removeButton.addActionListener(e -> removeSelectedReservation());

        changeStatusButton = new JButton("Change status");
        changeStatusButton.addActionListener(e -> changeSelectedReservationStatus());

        JPanel buttons = new JPanel();
        buttons.add(changeStatusButton);
        buttons.add(removeButton);

        JPanel bottomBar = new JPanel(new BorderLayout());
        bottomBar.add(buttons, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);
        add(reservationTablePanel, BorderLayout.CENTER);
        add(bottomBar, BorderLayout.SOUTH);

        // If we were given an initial user ID (e.g., from the user list), seed the field and load
        if (initialUserId != null && !initialUserId.isBlank()) {
            userIdField.setText(initialUserId);
            refreshData();
        } else {
            // Start with an empty model
            currentReservations = List.of();
            reservationTablePanel.setReservations(currentReservations);
        }
    }

    public void setPageController(PageController pageController) {
        this.pageController = pageController;
    }

    @Override
    public void refreshTheme(Theme t) {
        setBackground(t.bg);

        titleLabel.setForeground(t.fg);

        userIdField.setBackground(t.inputBg);
        userIdField.setForeground(t.inputFg);

        reservationTablePanel.refreshTheme(t);

        searchButton.setBackground(t.buttonBg);
        searchButton.setForeground(t.buttonFg);

        removeButton.setBackground(t.buttonBg);
        removeButton.setForeground(t.buttonFg);
        changeStatusButton.setBackground(t.buttonBg);
        changeStatusButton.setForeground(t.buttonFg);

        repaint();
    }

    @Override
    public void refreshData() {
        String userId = userIdField.getText();
        if (userId == null || userId.trim().isEmpty()) {
            currentReservations = List.of();
            reservationTablePanel.setReservations(currentReservations);
            return;
        }

        try {
            currentReservations = ReservationCRUD.findByUserId(userId.trim());
            reservationTablePanel.setReservations(currentReservations);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(
                this,
                ex.getMessage(),
                "Validation error",
                JOptionPane.WARNING_MESSAGE
            );
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(
                this,
                "Error while loading reservations: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void removeSelectedReservation() {
        int row = reservationTablePanel.getSelectedRowIndex();
        if (row < 0 || currentReservations == null || row >= currentReservations.size()) {
            JOptionPane.showMessageDialog(
                this,
                "Please select a reservation to remove.",
                "No selection",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        Reservation r = currentReservations.get(row);
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Remove booking " + r.getReservationId() + " for user " +
                (r.getUser() != null ? r.getUser().getUserId() : "") + "?",
            "Confirm removal",
            JOptionPane.YES_NO_OPTION
        );
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            src.database.ReservationCRUD.deleteById(r.getReservationId());
            refreshData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                this,
                "Error while removing reservation: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void changeSelectedReservationStatus() {
        int row = reservationTablePanel.getSelectedRowIndex();
        if (row < 0 || currentReservations == null || row >= currentReservations.size()) {
            JOptionPane.showMessageDialog(
                this,
                "Please select a reservation first.",
                "No selection",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        Reservation r = currentReservations.get(row);
        ReservationStatus currentStatus = r.getStatus();
        ReservationStatus[] options = ReservationStatus.values();

        ReservationStatus newStatus = (ReservationStatus) JOptionPane.showInputDialog(
            this,
            "Select a new status for reservation " + r.getReservationId() + ":",
            "Change reservation status",
            JOptionPane.PLAIN_MESSAGE,
            null,
            options,
            currentStatus
        );

        if (newStatus == null || newStatus == currentStatus) {
            return; // cancelled or unchanged
        }

        try {
            agentController.changeReservationStatus(r.getReservationId(), newStatus);
            refreshData();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(
                this,
                ex.getMessage(),
                "Validation error",
                JOptionPane.WARNING_MESSAGE
            );
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(
                this,
                "Error while updating reservation status: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
}


