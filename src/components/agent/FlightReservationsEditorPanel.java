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
import javax.swing.table.DefaultTableModel;
import src.config.Theme;
import src.database.ReservationCRUD;
import src.models.Flight;
import src.models.Reservation;
import src.views.DynamicPanel;

/**
 * Agent-facing panel that shows all reservations for a given flight
 * and allows the agent to remove (cancel) bookings.
 */
public class FlightReservationsEditorPanel extends DynamicPanel {

    private final Flight flight;
    private final JLabel titleLabel;
    private final JTable reservationTable;
    private final JScrollPane scrollPane;
    private final JButton removeButton;

    private List<Reservation> currentReservations;

    public FlightReservationsEditorPanel(Flight flight) {
        this.flight = flight;

        setLayout(new BorderLayout());

        String titleText = String.format(
            "Reservations for flight %s (%s → %s)",
            flight.getFlightId(),
            flight.getOrigin(),
            flight.getDestination()
        );
        titleLabel = new JLabel(titleText, JLabel.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));

        reservationTable = new JTable();
        scrollPane = new JScrollPane(reservationTable);

        removeButton = new JButton("Remove selected booking");
        removeButton.addActionListener(e -> removeSelectedReservation());

        JPanel bottomBar = new JPanel(new BorderLayout());
        bottomBar.add(removeButton, BorderLayout.EAST);

        add(titleLabel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomBar, BorderLayout.SOUTH);

        refreshData();
    }

    @Override
    public void refreshTheme(Theme t) {
        setBackground(t.bg);
        titleLabel.setForeground(t.fg);

        scrollPane.setBackground(t.bg);
        scrollPane.getViewport().setBackground(t.bg);

        reservationTable.setBackground(t.bg);
        reservationTable.setForeground(t.fg);
        reservationTable.setGridColor(t.fg);

        removeButton.setBackground(t.buttonBg);
        removeButton.setForeground(t.buttonFg);

        repaint();
    }

    @Override
    public void refreshData() {
        currentReservations = ReservationCRUD.findByFlightId(flight.getFlightId());

        String[] cols = { "Reservation ID", "User ID", "User Name", "Seats", "Status", "Booked At" };
        String[][] data = new String[currentReservations.size()][cols.length];

        for (int i = 0; i < currentReservations.size(); i++) {
            Reservation r = currentReservations.get(i);
            data[i][0] = r.getReservationId();
            data[i][1] = r.getUser() != null ? r.getUser().getUserId() : "";
            data[i][2] = r.getUser() != null ? r.getUser().getName() : "";
            data[i][3] = Integer.toString(r.getSeats());
            data[i][4] = r.getStatus() != null ? r.getStatus().name() : "";
            data[i][5] = r.getBookingDateTime() != null ? r.getBookingDateTime().toString() : "";
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
                (r.getUser() != null ? r.getUser().getName() : "") + "?",
            "Confirm removal",
            JOptionPane.YES_NO_OPTION
        );
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            ReservationCRUD.deleteById(r.getReservationId());
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
}


