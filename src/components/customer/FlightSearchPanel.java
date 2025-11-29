package src.components.customer;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

import src.AppActions;
import src.DTO.FlightDTO;
import src.components.DateInputField;
import src.components.ThemeAware;
import src.config.Theme;
import src.events.ControllerBus;
import src.events.Observer;

/**
 * Reusable flight search panel (table + filters) that delegates
 * user actions to the high-level AppActions (AppController or another
 * implementation), and listens for FLIGHTS_LOADED events to refresh
 * the table.
 *
 * This panel is intentionally generic so it can be embedded in
 * customer, agent, or admin dashboards. It displays a table of
 * FlightDTOs provided by the application layer.
 */
public class FlightSearchPanel extends JPanel implements ThemeAware, Observer {

    private final AppActions actions;

    private JTable flightTable;
    private JScrollPane tableScroll;

    private JTextField originField;
    private JTextField destField;
    private DateInputField dateField;
    private JButton searchButton;

    private JPanel searchBar;

    /**
     * Logical context for the search panel – allows future customization
     * (e.g., different actions on row click per role) while sharing the
     * same table + filter UI.
     */
    public enum Mode {
        CUSTOMER,
        AGENT,
        ADMIN
    }

    private final Mode mode;

    public FlightSearchPanel(AppActions actions) {
        this(actions, Mode.CUSTOMER);
    }

    public FlightSearchPanel(AppActions actions, Mode mode) {
        this.actions = actions;
        this.mode = mode;

        setLayout(new BorderLayout());
        setOpaque(true);

        buildResultsTable();
        buildSearchBar();

        // subscribe to flight search results
        ControllerBus.getInstance().subscribe(ControllerBus.EventType.FLIGHTS_LOADED, this);

        // start with an empty table
        updateTableFromFlights(new ArrayList<>());
    }

    // ------------------------------------------------------------
    // RESULTS TABLE
    // ------------------------------------------------------------
    private void buildResultsTable() {
        flightTable = new JTable();
        flightTable.setFillsViewportHeight(true);

        tableScroll = new JScrollPane(flightTable);
        tableScroll.setBorder(null);
        tableScroll.setOpaque(true);
        tableScroll.getViewport().setOpaque(true);

        add(tableScroll, BorderLayout.CENTER);
    }

    // ------------------------------------------------------------
    // SEARCH BAR (BOTTOM)
    // ------------------------------------------------------------
    private void buildSearchBar() {
        searchBar = new JPanel(new GridBagLayout());
        searchBar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        searchBar.setOpaque(true);

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 12, 6, 12);
        c.fill = GridBagConstraints.HORIZONTAL;

        originField = new JTextField();
        destField = new JTextField();
        dateField = new DateInputField("Date");

        searchButton = new JButton("Search");

        int row = 0;

        c.gridy = row; c.gridx = 0; searchBar.add(new JLabel("Origin"), c);
        c.gridx = 1; searchBar.add(originField, c);

        row++;
        c.gridy = row; c.gridx = 0; searchBar.add(new JLabel("Destination"), c);
        c.gridx = 1; searchBar.add(destField, c);

        row++;
        c.gridy = row; c.gridx = 0; searchBar.add(new JLabel("Date"), c);
        c.gridx = 1; c.weightx = 1.0; c.weighty = 0.0;
        c.fill = GridBagConstraints.HORIZONTAL; searchBar.add(dateField, c);

        row++;
        c.gridy = row; c.gridx = 0; c.gridwidth = 2;
        searchBar.add(searchButton, c);

        searchButton.addActionListener(e -> {
            if (actions != null) {
                String origin = originField.getText().trim();
                String dest   = destField.getText().trim();
                String date   = dateField.getText().trim();
                // Agent/admin can plug in their own AppActions implementation
                // to interpret this call differently if needed.
                actions.searchFlights(origin, dest, date, null);
            }
        });

        add(searchBar, BorderLayout.SOUTH);
    }

    // ------------------------------------------------------------
    // TABLE UPDATE FROM CONTROLLER EVENTS
    // ------------------------------------------------------------
    private void updateTableFromFlights(List<FlightDTO> flights) {
        String[] cols = { "Origin", "Destination", "Date", "Airline", "Price" };

        String[][] data = new String[flights.size()][cols.length];
        for (int i = 0; i < flights.size(); i++) {
            FlightDTO f = flights.get(i);
            data[i][0] = f.origin();
            data[i][1] = f.destination();
            data[i][2] = (f.date() != null) ? f.date().toString() : "";
            data[i][3] = f.airlineName();
            data[i][4] = String.format("$%.2f", f.price());
        }

        var model = new javax.swing.table.DefaultTableModel(data, cols) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        flightTable.setModel(model);
    }

    @Override
    public void update(Object event) {
        if (event instanceof List<?>) {
            List<?> raw = (List<?>) event;
            List<FlightDTO> flights = new ArrayList<>();
            for (Object o : raw) {
                if (o instanceof FlightDTO f) {
                    flights.add(f);
                }
            }
            updateTableFromFlights(flights);
        }
    }
    // ------------------------------------------------------------
    // THEME SUPPORT
    // ------------------------------------------------------------
    @Override
    public void refreshTheme(Theme t) {
        setBackground(t.bg);

        // results table
        tableScroll.setBackground(t.bg);
        tableScroll.getViewport().setBackground(t.bg);

        flightTable.setBackground(t.bg);
        flightTable.setForeground(t.fg);
        flightTable.setGridColor(t.fg);

        // search bar
        searchBar.setBackground(t.bg);
        Component[] comps = searchBar.getComponents();
        for (Component comp : comps) {
            if (comp instanceof JLabel lbl) {
                lbl.setForeground(t.fg);
            }
            if (comp instanceof JButton btn) {
                btn.setBackground(t.buttonBg);
                btn.setForeground(t.buttonFg);
            }
            if (comp instanceof ThemeAware ta) {
                ta.refreshTheme(t);
            }
        }

        repaint();
    }
}
