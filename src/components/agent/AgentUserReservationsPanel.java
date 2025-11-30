package src.components.agent;

import java.awt.BorderLayout;
import java.awt.Font;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
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
    private final JTable reservationTable;
    private final JScrollPane scrollPane;
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
        reservationTable = new JTable();
        scrollPane = new JScrollPane(reservationTable);

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
        add(scrollPane, BorderLayout.CENTER);
        add(bottomBar, BorderLayout.SOUTH);

        // If we were given an initial user ID (e.g., from the user list), seed the field and load
        if (initialUserId != null && !initialUserId.isBlank()) {
            userIdField.setText(initialUserId);
            refreshData();
        } else {
            // Start with an empty model
            setTableData(List.of());
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

        scrollPane.setBackground(t.bg);
        scrollPane.getViewport().setBackground(t.bg);

        reservationTable.setBackground(t.bg);
        reservationTable.setForeground(t.fg);
        reservationTable.setGridColor(t.fg);

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
            setTableData(List.of());
            return;
        }

        try {
            currentReservations = ReservationCRUD.findByUserId(userId.trim());
            setTableData(currentReservations);
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

    private void setTableData(List<Reservation> reservations) {
        currentReservations = reservations;

        String[] cols = {
            "Reservation ID",
            "User ID",
            "Flight ID",
            "Origin",
            "Destination",
            "Date",
            "Seats",
            "Status",
            "Booked At"
        };

        String[][] data = new String[reservations.size()][cols.length];
        for (int i = 0; i < reservations.size(); i++) {
            Reservation r = reservations.get(i);
            data[i][0] = r.getReservationId();
            data[i][1] = r.getUser() != null ? r.getUser().getUserId() : "";
            data[i][2] = r.getFlight() != null ? r.getFlight().getFlightId() : "";
            data[i][3] = r.getFlight() != null ? r.getFlight().getOrigin() : "";
            data[i][4] = r.getFlight() != null ? r.getFlight().getDestination() : "";
            data[i][5] = r.getFlight() != null && r.getFlight().getDate() != null
                ? r.getFlight().getDate().toString()
                : "";
            data[i][6] = Integer.toString(r.getSeats());
            data[i][7] = r.getStatus() != null ? r.getStatus().name() : "";
            data[i][8] = r.getBookingDateTime() != null
                ? r.getBookingDateTime().toString()
                : "";
        }

        DefaultTableModel model = new DefaultTableModel(data, cols) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        reservationTable.setModel(model);
    }

    private void removeSelectedReservation() {
        int row = reservationTable.getSelectedRow();
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
        int row = reservationTable.getSelectedRow();
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


