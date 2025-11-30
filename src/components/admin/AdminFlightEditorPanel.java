package src.components.admin;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import src.components.DateInputField;
import src.config.Theme;
import src.controllers.AdminFlightController;
import src.factory.ControllerFactory;
import src.models.Airline;
import src.models.Airplane;
import src.models.Flight;
import src.views.DynamicPanel;
import src.views.PageController;

/**
 * Admin workspace for creating and editing flights.
 *
 * Backed by {@link AdminFlightController}, this panel lets admins adjust
 * schedule, routing, aircraft, and pricing, and persists changes via the
 * shared CRUD layer.
 */
public class AdminFlightEditorPanel extends DynamicPanel {

    private final AdminFlightController adminFlightController;
    private final Flight originalFlight; // may be null for "new flight"
    private final Runnable onDone;       // callback to return to search/list

    private PageController pageController;

    // Header
    private JLabel titleLabel;

    // Form container
    private JPanel formPanel;

    // Form fields
    private JTextField flightIdField;
    private JComboBox<Airline> airlineBox;
    private JComboBox<Airplane> airplaneBox;
    private JLabel capacityLabel;
    private JTextField originField;
    private JTextField destinationField;
    private DateInputField dateField;
    private JTextField priceField;

    // Actions
    private JButton saveButton;
    private JButton cancelButton;
    private JButton deleteButton;

    public AdminFlightEditorPanel(Flight flight, Runnable onDone) {
        this.originalFlight = flight;
        this.onDone = onDone;
        this.adminFlightController = ControllerFactory.getInstance().adminFlights();

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        buildHeader();
        buildForm();
        buildActions();

        loadAirlinesAndAirplanes();
        populateFromFlight();
    }

    private void buildHeader() {
        titleLabel = new JLabel(
            originalFlight != null
                ? "Edit flight " + originalFlight.getFlightId()
                : "Create new flight",
            SwingConstants.LEFT
        );
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);
    }

    private void buildForm() {
        formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 8, 6, 8);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0;

        int row = 0;

        // Flight ID (read-only; primary key)
        JLabel idLabel = new JLabel("Flight ID:");
        flightIdField = new JTextField(16);
        flightIdField.setEditable(false);

        c.gridx = 0; c.gridy = row; c.weightx = 0;
        formPanel.add(idLabel, c);
        c.gridx = 1; c.gridy = row; c.weightx = 1.0;
        formPanel.add(flightIdField, c);
        row++;

        // Airline
        JLabel airlineLabel = new JLabel("Airline:");
        airlineBox = new JComboBox<>();

        c.gridx = 0; c.gridy = row; c.weightx = 0;
        formPanel.add(airlineLabel, c);
        c.gridx = 1; c.gridy = row; c.weightx = 1.0;
        formPanel.add(airlineBox, c);
        row++;

        // Airplane + capacity
        JLabel airplaneLabel = new JLabel("Airplane:");
        airplaneBox = new JComboBox<>();
        capacityLabel = new JLabel("Capacity: -");

        c.gridx = 0; c.gridy = row; c.weightx = 0;
        formPanel.add(airplaneLabel, c);
        c.gridx = 1; c.gridy = row; c.weightx = 1.0;
        formPanel.add(airplaneBox, c);
        row++;

        c.gridx = 1; c.gridy = row; c.weightx = 1.0;
        formPanel.add(capacityLabel, c);
        row++;

        // Origin
        JLabel originLabel = new JLabel("Origin (code/city):");
        originField = new JTextField(16);

        c.gridx = 0; c.gridy = row; c.weightx = 0;
        formPanel.add(originLabel, c);
        c.gridx = 1; c.gridy = row; c.weightx = 1.0;
        formPanel.add(originField, c);
        row++;

        // Destination
        JLabel destLabel = new JLabel("Destination (code/city):");
        destinationField = new JTextField(16);

        c.gridx = 0; c.gridy = row; c.weightx = 0;
        formPanel.add(destLabel, c);
        c.gridx = 1; c.gridy = row; c.weightx = 1.0;
        formPanel.add(destinationField, c);
        row++;

        // Date
        JLabel dateLabel = new JLabel("Departure date (yyyy-mm-dd):");
        dateField = new DateInputField("");

        c.gridx = 0; c.gridy = row; c.weightx = 0;
        formPanel.add(dateLabel, c);
        c.gridx = 1; c.gridy = row; c.weightx = 1.0;
        formPanel.add(dateField, c);
        row++;

        // Price
        JLabel priceLabel = new JLabel("Price (USD):");
        priceField = new JTextField(10);

        c.gridx = 0; c.gridy = row; c.weightx = 0;
        formPanel.add(priceLabel, c);
        c.gridx = 1; c.gridy = row; c.weightx = 1.0;
        formPanel.add(priceField, c);

        JScrollPane scroll = new JScrollPane(formPanel);
        scroll.setBorder(null);
        add(scroll, BorderLayout.CENTER);

        // When airplane changes, update capacity label
        airplaneBox.addActionListener(e -> updateCapacityLabel());
    }

    private void buildActions() {
        JPanel bottom = new JPanel(new BorderLayout());

        JPanel right = new JPanel();
        saveButton = new JButton("Save");
        cancelButton = new JButton("Cancel");
        deleteButton = new JButton("Delete flight");
        deleteButton.setEnabled(originalFlight != null);

        saveButton.addActionListener(e -> handleSave());
        cancelButton.addActionListener(e -> navigateBack());
        deleteButton.addActionListener(e -> handleDelete());

        right.add(saveButton);
        right.add(cancelButton);
        right.add(deleteButton);

        bottom.add(right, BorderLayout.EAST);
        add(bottom, BorderLayout.SOUTH);
    }

    private void loadAirlinesAndAirplanes() {
        List<Airline> airlines = adminFlightController.getAllAirlines();
        airlineBox.removeAllItems();
        for (Airline a : airlines) {
            airlineBox.addItem(a);
        }

        List<Airplane> airplanes = adminFlightController.getAllAirplanes();
        airplaneBox.removeAllItems();
        for (Airplane a : airplanes) {
            airplaneBox.addItem(a);
        }

        // Basic renderer to show friendlier labels
        airlineBox.setRenderer((ListCellRenderer<? super Airline>) (list, value, index, isSelected, cellHasFocus) -> {
            JLabel lbl = (JLabel) new DefaultListCellRenderer()
                .getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Airline a) {
                lbl.setText(a.getName() + " (" + a.getAirlineId() + ")");
            }
            return lbl;
        });

        airplaneBox.setRenderer((ListCellRenderer<? super Airplane>) (list, value, index, isSelected, cellHasFocus) -> {
            JLabel lbl = (JLabel) new DefaultListCellRenderer()
                .getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Airplane a) {
                String text = a.getModel() != null
                    ? a.getModel()
                    : a.getAirplaneId();
                lbl.setText(text + " (" + a.getCapacity() + " seats)");
            }
            return lbl;
        });

        updateCapacityLabel();
    }

    private void populateFromFlight() {
        if (originalFlight == null) {
            flightIdField.setText("");
            originField.setText("");
            destinationField.setText("");
            dateField.setText("");
            priceField.setText("");
            updateCapacityLabel();
            return;
        }

        flightIdField.setText(originalFlight.getFlightId());
        originField.setText(originalFlight.getOrigin());
        destinationField.setText(originalFlight.getDestination());
        if (originalFlight.getDate() != null) {
            dateField.setText(originalFlight.getDate().toString());
        }
        priceField.setText(String.format("%.2f", originalFlight.getPrice()));

        // Select matching airline/airplane in the combos
        Airline flightAirline = originalFlight.getAirline();
        if (flightAirline != null) {
            for (int i = 0; i < airlineBox.getItemCount(); i++) {
                Airline a = airlineBox.getItemAt(i);
                if (a != null && a.getAirlineId().equals(flightAirline.getAirlineId())) {
                    airlineBox.setSelectedIndex(i);
                    break;
                }
            }
        }

        Airplane flightAirplane = originalFlight.getAirplane();
        if (flightAirplane != null) {
            for (int i = 0; i < airplaneBox.getItemCount(); i++) {
                Airplane a = airplaneBox.getItemAt(i);
                if (a != null && a.getAirplaneId().equals(flightAirplane.getAirplaneId())) {
                    airplaneBox.setSelectedIndex(i);
                    break;
                }
            }
        }

        updateCapacityLabel();
    }

    private void updateCapacityLabel() {
        Airplane plane = (Airplane) airplaneBox.getSelectedItem();
        if (plane == null) {
            capacityLabel.setText("Capacity: -");
        } else {
            capacityLabel.setText("Capacity: " + plane.getCapacity() + " seats");
        }
    }

    private void handleSave() {
        String flightId = flightIdField.getText() != null
            ? flightIdField.getText().trim()
            : null;
        Airline airline = (Airline) airlineBox.getSelectedItem();
        Airplane airplane = (Airplane) airplaneBox.getSelectedItem();
        String origin = originField.getText() != null ? originField.getText().trim() : "";
        String destination = destinationField.getText() != null ? destinationField.getText().trim() : "";
        String rawDate = dateField.getText();
        String priceText = priceField.getText() != null ? priceField.getText().trim() : "";

        if (flightId == null || flightId.isBlank()) {
            JOptionPane.showMessageDialog(
                this,
                "Flight ID is required.",
                "Validation error",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (airline == null || airplane == null) {
            JOptionPane.showMessageDialog(
                this,
                "Please select both an airline and an airplane.",
                "Validation error",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (origin.isBlank() || destination.isBlank()) {
            JOptionPane.showMessageDialog(
                this,
                "Origin and destination are required.",
                "Validation error",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (rawDate == null || rawDate.contains("%")) {
            JOptionPane.showMessageDialog(
                this,
                "Please enter a full departure date in the form yyyy-mm-dd.",
                "Validation error",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        LocalDate date;
        try {
            date = LocalDate.parse(rawDate);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(
                this,
                "Invalid date format. Please use yyyy-mm-dd.",
                "Validation error",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceText);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                this,
                "Price must be a valid number.",
                "Validation error",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        try {
            adminFlightController.createOrUpdateFlight(
                flightId,
                airline,
                airplane,
                origin,
                destination,
                date,
                price
            );

            JOptionPane.showMessageDialog(
                this,
                "Flight saved successfully.",
                "Success",
                JOptionPane.INFORMATION_MESSAGE
            );

            navigateBack();
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
                "Error while saving flight: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void handleDelete() {
        if (originalFlight == null) {
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Delete flight " + originalFlight.getFlightId() + "? This cannot be undone.",
            "Confirm delete",
            JOptionPane.YES_NO_OPTION
        );
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            adminFlightController.cancelFlight(originalFlight.getFlightId());
            JOptionPane.showMessageDialog(
                this,
                "Flight deleted.",
                "Deleted",
                JOptionPane.INFORMATION_MESSAGE
            );
            navigateBack();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(
                this,
                "Error while deleting flight: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void navigateBack() {
        if (onDone != null) {
            onDone.run();
        } else if (pageController != null) {
            // Optional: if a page controller is present, delegate to it.
            // Callers can decide what "back" means in their context.
        }
    }

    @Override
    public void refreshTheme(Theme t) {
        setBackground(t.bg);

        if (titleLabel != null) {
            titleLabel.setForeground(t.fg);
        }

        if (formPanel != null) {
            formPanel.setBackground(t.bg);
        }

        if (originField != null) {
            originField.setBackground(t.inputBg);
            originField.setForeground(t.inputFg);
        }
        if (destinationField != null) {
            destinationField.setBackground(t.inputBg);
            destinationField.setForeground(t.inputFg);
        }
        if (priceField != null) {
            priceField.setBackground(t.inputBg);
            priceField.setForeground(t.inputFg);
        }
        if (capacityLabel != null) {
            capacityLabel.setForeground(t.fg);
        }
        if (airlineBox != null) {
            airlineBox.setBackground(t.inputBg);
            airlineBox.setForeground(t.inputFg);
        }
        if (airplaneBox != null) {
            airplaneBox.setBackground(t.inputBg);
            airplaneBox.setForeground(t.inputFg);
        }

        if (saveButton != null) {
            saveButton.setBackground(t.buttonBg);
            saveButton.setForeground(t.buttonFg);
        }
        if (cancelButton != null) {
            cancelButton.setBackground(t.buttonBg);
            cancelButton.setForeground(t.buttonFg);
        }
        if (deleteButton != null) {
            deleteButton.setBackground(t.buttonBg);
            deleteButton.setForeground(t.buttonFg);
        }

        if (dateField != null) {
            dateField.refreshTheme(t);
        }

        repaint();
    }

    public void setPageController(PageController pageController) {
        this.pageController = pageController;
    }
}

