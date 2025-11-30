package src.components.customer;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import src.DTO.FlightDTO;
import src.components.DateInputField;
import src.components.LabeledTextField;
import src.components.ThemeAware;
import src.config.Theme;


/**
 * Reusable flight search panel (table + filters) concerned only with
 * building and managing UI components. It exposes simple callbacks so
 * higher-level panels (customer, agent, admin) can plug in their own
 * behaviors.
 */
public class FlightSearchPanel extends JPanel implements ThemeAware {

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

    /**
     * Callback interface used to notify the parent when the user presses
     * the Search button. Implementations can route this to AppActions or
     * any other controller.
     */
    @FunctionalInterface
    public interface SearchHandler {
        void onSearch(String origin, String destination, String date);
    }

    private final Mode mode;

    private JTable flightTable;
    private JScrollPane tableScroll;

    private LabeledTextField originField;
    private LabeledTextField destField;
    private DateInputField dateField;
    private JButton searchButton;

    private JPanel searchBar;
    private int nextSearchRow = 0;

    private SearchHandler searchHandler;

    public FlightSearchPanel() {
        this(Mode.CUSTOMER, null);
    }

    public FlightSearchPanel(Mode mode) {
        this(mode, null);
    }

    public FlightSearchPanel(Mode mode, SearchHandler handler) {
        this.mode = mode;
        this.searchHandler = handler;

        setLayout(new BorderLayout());
        setOpaque(true);

        buildResultsTable();
        buildSearchBar();

        // start with an empty table
        updateTableFromFlights(new ArrayList<>());
    }

    // ------------------------------------------------------------
    // PUBLIC API FOR PARENTS
    // ------------------------------------------------------------

    public void setSearchHandler(SearchHandler handler) {
        this.searchHandler = handler;
    }

    /**
     * Programmatically replace the flights displayed in the table.
     * Useful for agent/admin flows that perform synchronous searches.
     */
    public void setFlights(List<FlightDTO> flights) {
        updateTableFromFlights(flights != null ? flights : new ArrayList<>());
    }

    /** Exposes the underlying table for row listeners, column tweaks, etc. */
    public JTable getFlightTable() {
        return flightTable;
    }

    /** Exposes the search bar container so callers can inspect or arrange components if needed. */
    public JPanel getSearchBarPanel() {
        return searchBar;
    }

    /**
     * Add an extra control below the built-in filters/search button.
     * This is primarily intended for agent/admin-only tools.
     */
    public void addExtraSearchControl(Component comp) {
        if (searchBar == null || comp == null) return;

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 12, 6, 12);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = nextSearchRow++;
        c.gridwidth = 2;

        searchBar.add(comp, c);
        searchBar.revalidate();
        searchBar.repaint();
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

        originField = new LabeledTextField("Origin");
        destField = new LabeledTextField("Destination");
        dateField = new DateInputField("Date");

        searchButton = new JButton("Search");

        // origin row
        c.gridy = nextSearchRow;
        c.gridx = 0;
        searchBar.add(originField, c);
        c.gridx = 1;
        searchBar.add(originField.getInnerField(), c);
        nextSearchRow++;

        // destination row
        c.gridy = nextSearchRow;
        c.gridx = 0;
        searchBar.add(destField, c);
        c.gridx = 1;
        searchBar.add(destField.getInnerField(), c);
        nextSearchRow++;

        // date row
        c.gridy = nextSearchRow;
        c.gridx = 0;
        searchBar.add(new JLabel("Date"), c);
        c.gridx = 1;
        c.weightx = 1.0;
        c.weighty = 0.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        searchBar.add(dateField, c);
        nextSearchRow++;

        // search button row
        c.gridy = nextSearchRow;
        c.gridx = 0;
        c.gridwidth = 2;
        searchBar.add(searchButton, c);
        nextSearchRow++;

        searchButton.addActionListener(e -> {
            if (searchHandler != null) {
                String origin = originField.getText().trim();
                String dest   = destField.getText().trim();

                // Let DateInputField decide how to interpret the mask.
                // It returns null for an untouched mask ("____-__-__")
                // and the raw masked value (e.g., "2025-__-__") otherwise.
                String date = dateField.getText();
                System.out.println("date: " + date);

                searchHandler.onSearch(origin, dest, date);
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
