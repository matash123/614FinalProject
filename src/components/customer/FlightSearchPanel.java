package src.components.customer;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import src.components.ThemeAware;
import src.config.Theme;
import javax.swing.text.MaskFormatter;
import javax.swing.JFormattedTextField;
import java.text.ParseException;

public class FlightSearchPanel extends JPanel implements ThemeAware {

    private JTable flightTable;
    private JScrollPane tableScroll;

    private JTextField originField;
    private JTextField destField;
    private JFormattedTextField dateStartField;
    private JFormattedTextField dateEndField;
    private JButton searchButton;

    private JPanel searchBar;

    public FlightSearchPanel() {
        setLayout(new BorderLayout());
        setOpaque(true);

        buildResultsTable();
        buildSearchBar();

        updateTable(getFlights());
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
            updateTable(applyFilters(getFlights()));
        });

        add(searchBar, BorderLayout.SOUTH);
    }

    // ------------------------------------------------------------
    // DATA + FILTERING
    // ------------------------------------------------------------
    private List<String[]> getFlights() {
        List<String[]> flights = new ArrayList<>();

        flights.add(new String[]{"YYC", "YVR", "2025-12-01", "Air Canada", "$199"});
        flights.add(new String[]{"YYC", "LAX", "2025-12-05", "WestJet", "$249"});
        flights.add(new String[]{"YYC", "JFK", "2025-12-07", "Delta", "$310"});
        flights.add(new String[]{"YVR", "NRT", "2026-01-03", "ANA", "$840"});
        flights.add(new String[]{"YYC", "SEA", "2025-11-29", "Alaska", "$180"});

        return flights;
    }

    private List<String[]> applyFilters(List<String[]> flights) {
        String origin = originField.getText().trim().toUpperCase();
        String dest   = destField.getText().trim().toUpperCase();
        String start  = dateStartField.getText().trim();
        String end    = dateEndField.getText().trim();

        List<String[]> result = new ArrayList<>();

        for (String[] f : flights) {
            String fOrigin = f[0];
            String fDest   = f[1];
            String fDate   = f[2];

            if (!origin.isEmpty() && !fOrigin.contains(origin)) continue;
            if (!dest.isEmpty()   && !fDest.contains(dest)) continue;

            if (!start.isEmpty() && fDate.compareTo(start) < 0) continue;
            if (!end.isEmpty()   && fDate.compareTo(end) > 0) continue;

            result.add(f);
        }

        return result;
    }

    private void updateTable(List<String[]> flights) {
        String[] cols = { "Origin", "Destination", "Date", "Airline", "Price" };
        String[][] data = flights.toArray(new String[0][]);

        flightTable.setModel(new javax.swing.table.DefaultTableModel(
                data, cols
        ) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        });
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
