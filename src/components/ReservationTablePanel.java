package src.components;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import src.config.Theme;
import src.models.Reservation;

/**
 * Shared, read-only reservation table component.
 *
 * This panel owns a {@link JTable} and exposes small helper methods for:
 * - Setting the backing list of {@link Reservation} objects
 * - Retrieving the currently selected row index
 *
 * It does NOT perform any domain actions (delete, edit, etc); those are left
 * to the parent panel (agent, customer, admin) so permissions remain clear.
 */
public class ReservationTablePanel extends javax.swing.JPanel implements ThemeAware {

    private final JTable table;
    private final JScrollPane scrollPane;

    // Optional cached snapshot of the last data set purely for convenience;
    // parents should still keep their own domain lists for actions.
    private List<Reservation> currentReservations = new ArrayList<>();

    public ReservationTablePanel() {
        setLayout(new BorderLayout());
        setOpaque(true);

        table = new JTable();
        table.setFillsViewportHeight(true);

        scrollPane = new JScrollPane(table);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setOpaque(true);

        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Replace the rows in the table with a new list of reservations.
     * The table itself is read-only; callers should use the selected
     * row index to map back into their own reservation list.
     */
    public void setReservations(List<Reservation> reservations) {
        currentReservations = reservations != null ? new ArrayList<>(reservations)
                                                   : new ArrayList<>();

        String[] cols = {
            "Reservation ID",
            "User ID",
            "User Name",
            "Flight ID",
            "Origin",
            "Destination",
            "Date",
            "Seats",
            "Status",
            "Booked At"
        };

        String[][] data = new String[currentReservations.size()][cols.length];
        for (int i = 0; i < currentReservations.size(); i++) {
            Reservation r = currentReservations.get(i);
            var user = r.getUser();
            var flight = r.getFlight();

            data[i][0] = r.getReservationId();
            data[i][1] = user != null ? user.getUserId() : "";
            data[i][2] = user != null ? user.getName() : "";
            data[i][3] = flight != null ? flight.getFlightId() : "";
            data[i][4] = flight != null ? flight.getOrigin() : "";
            data[i][5] = flight != null ? flight.getDestination() : "";
            data[i][6] = (flight != null && flight.getDate() != null)
                ? flight.getDate().toString()
                : "";
            data[i][7] = Integer.toString(r.getSeats());
            data[i][8] = r.getStatus() != null ? r.getStatus().name() : "";
            data[i][9] = r.getBookingDateTime() != null
                ? r.getBookingDateTime().toString()
                : "";
        }

        DefaultTableModel model = new DefaultTableModel(data, cols) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table.setModel(model);
    }

    /**
     * Returns the currently selected row index in the table's view,
     * or -1 if nothing is selected.
     *
     * Callers should map this index back into their own reservation list.
     */
    public int getSelectedRowIndex() {
        return table.getSelectedRow();
    }

    @Override
    public void refreshTheme(Theme t) {
        setBackground(t.bg);

        scrollPane.setBackground(t.bg);
        scrollPane.getViewport().setBackground(t.bg);

        table.setBackground(t.bg);
        table.setForeground(t.fg);
        table.setGridColor(t.fg);

        repaint();
    }
}


