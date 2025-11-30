package src.components.customer;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import src.components.DateInputField;
import src.components.LabeledTextField;
import src.components.ThemeAware;
import src.config.Theme;
import src.controllers.FlightSearchController;
import src.factory.ControllerFactory;
import src.models.Flight;
import src.views.DynamicPanel;
import src.views.PageController;


/**
 * Reusable flight search panel (table + filters) concerned only with
 * building and managing UI components. It now talks directly to the
 * dedicated {@link FlightSearchController} (via {@link ControllerFactory})
 * so that search logic no longer flows through the top-level AppController.
 */
public class FlightSearchPanel extends DynamicPanel {

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
    /** Dedicated controller for flight search; obtained from the factory. */
    private final FlightSearchController flightSearchController;
    /** Optional page controller so rows/actions can switch the active panel. */
    private PageController pageController;

    private JTable flightTable;
    private JScrollPane tableScroll;

    private LabeledTextField originField;
    private LabeledTextField destField;
    private LabeledTextField idField;
    private DateInputField dateField;
    private JButton searchButton;

    private JPanel searchBar;
    private int nextSearchRow = 0;

    // keep a parallel list of the flights backing the table so that
    // calling code can retrieve the currently selected flight.
    private List<Flight> currentFlights = new ArrayList<>();

    public FlightSearchPanel(Mode mode) {
        this.mode = mode;
        this.flightSearchController = ControllerFactory.getInstance().flights();

        setLayout(new BorderLayout());
        setOpaque(true);

        buildResultsTable();
        buildSearchBar();

        // start with an empty table
        updateTableFromFlights(new ArrayList<>());
    }

    public void setPageController(PageController pageController) {
        this.pageController = pageController;
    }

    // ------------------------------------------------------------
    // PUBLIC API FOR PARENTS
    // ------------------------------------------------------------

    /**
     * Programmatically replace the flights displayed in the table.
     * Useful for agent/admin flows that perform synchronous searches.
     */
    public void setFlights(List<Flight> flights) {
        updateTableFromFlights(flights != null ? flights : new ArrayList<>());
    }

    /** Exposes the underlying table for row listeners, column tweaks, etc. */
    public JTable getFlightTable() {
        return flightTable;
    }

    /**
     * Returns the {@link Flight} corresponding to the currently
     * selected row in the table, or {@code null} if nothing is
     * selected or the backing data is empty.
     */
    public Flight getSelectedFlight() {
        if (flightTable == null || currentFlights.isEmpty()) {
            return null;
        }
        int viewRow = flightTable.getSelectedRow();
        if (viewRow < 0) {
            return null;
        }
        int modelRow = flightTable.convertRowIndexToModel(viewRow);
        if (modelRow < 0 || modelRow >= currentFlights.size()) {
            return null;
        }
        return currentFlights.get(modelRow);
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
        idField = new LabeledTextField("Flight ID");
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

        // date row (label comes from DateInputField itself)
        c.gridy = nextSearchRow;
        c.gridx = 1;
        c.weightx = 1.0;
        c.weighty = 0.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        searchBar.add(dateField, c);
        nextSearchRow++;

        // flight id row
        c.gridy = nextSearchRow;
        c.gridx = 0;
        searchBar.add(idField, c);
        c.gridx = 1;
        searchBar.add(idField.getInnerField(), c);
        nextSearchRow++;

        // search button row
        c.gridy = nextSearchRow;
        c.gridx = 0;
        c.gridwidth = 2;
        searchBar.add(searchButton, c);
        nextSearchRow++;

        searchButton.addActionListener(e -> {
            String origin = originField.getText().trim();
            String dest   = destField.getText().trim();
            String id     = idField.getText().trim();
            // Let DateInputField decide how to interpret the mask.
            // It returns null for an untouched mask ("____-__-__")
            // and the raw masked value (e.g., "2025-__-__") otherwise.
            String date   = dateField.getText();
            System.out.println("date: " + date);

            List<Flight> flights =
                flightSearchController.searchFlights(origin, dest, date, id);
            setFlights(flights);
        });

        add(searchBar, BorderLayout.SOUTH);
    }

    // ------------------------------------------------------------
    // TABLE UPDATE FROM CONTROLLER EVENTS
    // ------------------------------------------------------------
    private void updateTableFromFlights(List<Flight> flights) {
        currentFlights = new ArrayList<>(flights);
        String[] cols = { "Origin", "Destination", "Date", "Airline", "Price" };

        String[][] data = new String[currentFlights.size()][cols.length];
        for (int i = 0; i < currentFlights.size(); i++) {
            Flight f = currentFlights.get(i);
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
