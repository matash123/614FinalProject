package src.components.customer;

import java.awt.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.text.MaskFormatter;
import src.AppActions;
import src.components.ThemeAware;
import src.config.Theme;
import src.events.ControllerBus;
import src.events.Observer;
import src.models.Flight;

/**
 * Customer-facing flight search panel.
 * Delegates user actions to the high-level AppActions (AppController),
 * and listens for FLIGHTS_LOADED events to refresh the table.
 */
public class FlightSearchPanel extends JPanel implements ThemeAware, Observer {

    private final AppActions actions;

    private JTable flightTable;
    private JScrollPane tableScroll;

    private JTextField originField;
    private JTextField destField;
    private JFormattedTextField dateStartField;
    private JFormattedTextField dateEndField;
    private JButton searchButton;

    private JPanel searchBar;

    public FlightSearchPanel(AppActions actions) {
        this.actions = actions;

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


        dateStartField = createDateField();
        dateEndField   = createDateField();
    

        searchButton = new JButton("Search");

        int row = 0;

        c.gridy = row; c.gridx = 0; searchBar.add(new JLabel("Origin"), c);
        c.gridx = 1; searchBar.add(originField, c);

        row++;
        c.gridy = row; c.gridx = 0; searchBar.add(new JLabel("Destination"), c);
        c.gridx = 1; searchBar.add(destField, c);

        row++;
        c.gridy = row; c.gridx = 0; searchBar.add(new JLabel("Date Start"), c);
        c.gridx = 1; searchBar.add(dateStartField, c);

        row++; //SEARCH BAR -----------------------
        c.gridy = row; c.gridx = 0; searchBar.add(new JLabel("Date End"), c);
        c.gridx = 1; c.weightx = 1.0; c.weighty = 0.0; 
        c.fill = GridBagConstraints.HORIZONTAL; searchBar.add(dateEndField, c);

        row++;
        c.gridy = row; c.gridx = 0; c.gridwidth = 2;
        searchBar.add(searchButton, c);

        searchButton.addActionListener(e -> {
            if (actions != null) {
                String origin = originField.getText().trim();
                String dest   = destField.getText().trim();
                String start  = dateStartField.getText().trim();
                String end    = dateEndField.getText().trim();
                actions.searchFlights(origin, dest, start, end);
            }
        });

        add(searchBar, BorderLayout.SOUTH);
    }

    // ------------------------------------------------------------
    // TABLE UPDATE FROM CONTROLLER EVENTS
    // ------------------------------------------------------------
    private void updateTableFromFlights(List<Flight> flights) {
        String[] cols = { "Origin", "Destination", "Date", "Airline", "Price" };

        String[][] data = new String[flights.size()][cols.length];
        for (int i = 0; i < flights.size(); i++) {
            Flight f = flights.get(i);
            data[i][0] = f.getOrigin();
            data[i][1] = f.getDestination();
            data[i][2] = (f.getDate() != null) ? f.getDate().toString() : "";
            data[i][3] = (f.getAirline() != null) ? f.getAirline().getName() : "";
            data[i][4] = String.format("$%.2f", f.getPrice());
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
            List<Flight> flights = new ArrayList<>();
            for (Object o : raw) {
                if (o instanceof Flight f) {
                    flights.add(f);
                }
            }
            updateTableFromFlights(flights);
        }
    }



    private JFormattedTextField createDateField() {
        try {
            MaskFormatter formatter = new MaskFormatter("####-##-##");
            formatter.setPlaceholderCharacter('_');
            return new JFormattedTextField(formatter);
        } catch (ParseException e) {
            // Fallback: if mask setup fails, use plain text field
            System.err.println("Failed to create date mask: " + e.getMessage());
            return new JFormattedTextField();
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
            if (comp instanceof JTextField tf) {
                tf.setBackground(t.inputBg);
                tf.setForeground(t.inputFg);
            }
            if (comp instanceof JButton btn) {
                btn.setBackground(t.buttonBg);
                btn.setForeground(t.buttonFg);
            }
        }

        repaint();
    }
}
