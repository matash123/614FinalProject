package src.views;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.List;

import src.controllers.AdminFlightController;
import src.models.Airline;
import src.models.Airplane;
import src.models.Flight;

/**
 * GUI for Flight Agent:
 * - search flights
 * - add/edit flights (using existing airlines & airplanes)
 * - delete flights
 */
public class AgentPanel extends JPanel {

    private final AdminFlightController controller;

    // search fields
    private final JTextField originSearchField = new JTextField(10);
    private final JTextField destinationSearchField = new JTextField(10);
    private final JTextField dateSearchField = new JTextField(10); // yyyy-MM-dd

    // table
    private final DefaultTableModel tableModel;
    private final JTable flightTable;

    // form fields for add/edit
    private final JTextField flightIdField = new JTextField(10);
    private final JTextField originField = new JTextField(10);
    private final JTextField destinationField = new JTextField(10);
    private final JTextField dateField = new JTextField(10); // yyyy-MM-dd
    private final JTextField priceField = new JTextField(10);

    private final JComboBox<Airline> airlineCombo = new JComboBox<>();
    private final JComboBox<Airplane> airplaneCombo = new JComboBox<>();

    // keep current list backing the table
    private List<Flight> currentFlights;

    public AgentPanel(AdminFlightController controller) {
        this.controller = controller;

        // ----- table model -----
        tableModel = new DefaultTableModel(
                new Object[]{"Flight ID", "Airline", "Origin", "Destination", "Date", "Airplane", "Price"},
                0
        ) {
            // make cells non-editable directly
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        flightTable = new JTable(tableModel);
        flightTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        setLayout(new BorderLayout());
        add(buildSearchPanel(), BorderLayout.NORTH);
        add(new JScrollPane(flightTable), BorderLayout.CENTER);
        add(buildFormPanel(), BorderLayout.SOUTH);

        hookTableSelection();
        loadDropdowns();
    }

    // -----------------------------------------
    // Top search bar
    // -----------------------------------------
    private JComponent buildSearchPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton searchButton = new JButton("Search Flights");
        searchButton.addActionListener(e -> doSearch());

        panel.add(new JLabel("Origin:"));
        panel.add(originSearchField);
        panel.add(new JLabel("Destination:"));
        panel.add(destinationSearchField);
        panel.add(new JLabel("Date (yyyy-MM-dd, optional):"));
        panel.add(dateSearchField);
        panel.add(searchButton);

        return panel;
    }

    // -----------------------------------------
    // Bottom form (add/edit/delete)
    // -----------------------------------------
    private JComponent buildFormPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(2, 2, 2, 2);
        gc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // row 0: flight ID
        gc.gridx = 0; gc.gridy = row;
        panel.add(new JLabel("Flight ID:"), gc);
        gc.gridx = 1;
        panel.add(flightIdField, gc);

        // row 1: airline
        row++;
        gc.gridx = 0; gc.gridy = row;
        panel.add(new JLabel("Airline:"), gc);
        gc.gridx = 1;
        panel.add(airlineCombo, gc);

        // row 2: airplane
        row++;
        gc.gridx = 0; gc.gridy = row;
        panel.add(new JLabel("Airplane:"), gc);
        gc.gridx = 1;
        panel.add(airplaneCombo, gc);

        // row 3: origin
        row++;
        gc.gridx = 0; gc.gridy = row;
        panel.add(new JLabel("Origin:"), gc);
        gc.gridx = 1;
        panel.add(originField, gc);

        // row 4: destination
        row++;
        gc.gridx = 0; gc.gridy = row;
        panel.add(new JLabel("Destination:"), gc);
        gc.gridx = 1;
        panel.add(destinationField, gc);

        // row 5: date
        row++;
        gc.gridx = 0; gc.gridy = row;
        panel.add(new JLabel("Date (yyyy-MM-dd):"), gc);
        gc.gridx = 1;
        panel.add(dateField, gc);

        // row 6: price
        row++;
        gc.gridx = 0; gc.gridy = row;
        panel.add(new JLabel("Price:"), gc);
        gc.gridx = 1;
        panel.add(priceField, gc);

        // row 7: buttons
        row++;
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save Flight");
        JButton deleteButton = new JButton("Delete Flight");
        JButton clearButton = new JButton("Clear Form");

        saveButton.addActionListener(e -> doSaveFlight());
        deleteButton.addActionListener(e -> doDeleteFlight());
        clearButton.addActionListener(e -> clearForm());

        buttons.add(saveButton);
        buttons.add(deleteButton);
        buttons.add(clearButton);

        gc.gridx = 0; gc.gridy = row;
        gc.gridwidth = 2;
        panel.add(buttons, gc);

        return panel;
    }

    // -----------------------------------------
    // Logic: search, load dropdowns, table selection
    // -----------------------------------------

    private void doSearch() {
        String origin = originSearchField.getText();
        String destination = destinationSearchField.getText();
        String date = dateSearchField.getText();

        currentFlights = controller.searchFlights(origin, destination, date);
        refreshTable();
    }

    private void refreshTable() {
        tableModel.setRowCount(0); // clear

        if (currentFlights == null) return;

        for (Flight f : currentFlights) {
            tableModel.addRow(new Object[]{
                    f.getFlightId(),
                    f.getAirline().getName(),
                    f.getOrigin(),
                    f.getDestination(),
                    f.getDate() != null ? f.getDate().toString() : "",
                    f.getAirplane().getModel(),
                    f.getPrice()
            });
        }
    }

    private void loadDropdowns() {
        airlineCombo.removeAllItems();
        for (Airline a : controller.getAllAirlines()) {
            airlineCombo.addItem(a);
        }

        airplaneCombo.removeAllItems();
        for (Airplane ap : controller.getAllAirplanes()) {
            airplaneCombo.addItem(ap);
        }

        // optional: nicer display text if toString() is not overridden
        airlineCombo.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = new JLabel();
            if (value != null) {
                label.setText(value.getAirlineId() + " - " + value.getName());
            }
            if (isSelected) {
                label.setOpaque(true);
                label.setBackground(list.getSelectionBackground());
                label.setForeground(list.getSelectionForeground());
            }
            return label;
        });

        airplaneCombo.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = new JLabel();
            if (value != null) {
                label.setText(value.getAirplaneId() + " - " + value.getModel());
            }
            if (isSelected) {
                label.setOpaque(true);
                label.setBackground(list.getSelectionBackground());
                label.setForeground(list.getSelectionForeground());
            }
            return label;
        });
    }

    private void hookTableSelection() {
        flightTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = flightTable.getSelectedRow();
                if (row < 0 || currentFlights == null || row >= currentFlights.size()) {
                    return;
                }
                Flight f = currentFlights.get(row);
                populateFormFromFlight(f);
            }
        });
    }

    private void populateFormFromFlight(Flight f) {
        flightIdField.setText(f.getFlightId());
        originField.setText(f.getOrigin());
        destinationField.setText(f.getDestination());
        dateField.setText(f.getDate() != null ? f.getDate().toString() : "");
        priceField.setText(Double.toString(f.getPrice()));

        // select the right airline / airplane in the combos
        selectComboItem(airlineCombo, f.getAirline());
        selectComboItem(airplaneCombo, f.getAirplane());
    }

    private <T> void selectComboItem(JComboBox<T> combo, T value) {
        if (value == null) return;
        ComboBoxModel<T> model = combo.getModel();
        for (int i = 0; i < model.getSize(); i++) {
            T item = model.getElementAt(i);
            if (item != null && item.equals(value)) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    private void doSaveFlight() {
        try {
            String flightId = flightIdField.getText();
            Airline airline = (Airline) airlineCombo.getSelectedItem();
            Airplane airplane = (Airplane) airplaneCombo.getSelectedItem();
            String origin = originField.getText();
            String destination = destinationField.getText();
            String dateStr = dateField.getText();
            String priceStr = priceField.getText();

            LocalDate date = LocalDate.parse(dateStr.trim()); // assume valid format
            double price = Double.parseDouble(priceStr.trim());

            Flight saved = controller.addOrUpdateFlight(
                    flightId,
                    airline,
                    airplane,
                    origin,
                    destination,
                    date,
                    price
            );

            // refresh table after save
            doSearch();
            populateFormFromFlight(saved); // so UI shows normalized values
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "Error saving flight: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void doDeleteFlight() {
        int row = flightTable.getSelectedRow();
        if (row < 0 || currentFlights == null || row >= currentFlights.size()) {
            JOptionPane.showMessageDialog(this, "Select a flight to delete.");
            return;
        }

        Flight f = currentFlights.get(row);

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Delete flight " + f.getFlightId() + "?",
                "Confirm delete",
                JOptionPane.YES_NO_OPTION
        );

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            controller.deleteFlight(f.getFlightId());
            doSearch();    // refresh table
            clearForm();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "Error deleting flight: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void clearForm() {
        flightIdField.setText("");
        originField.setText("");
        destinationField.setText("");
        dateField.setText("");
        priceField.setText("");
        flightTable.clearSelection();
    }
}
