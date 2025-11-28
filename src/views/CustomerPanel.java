package src.views;

import javax.swing.*;
import java.awt.*;
import src.AppActions;
import src.config.Theme;

/**
 * CustomerPanel – the main customer dashboard.
 * Shows: search flights, book/cancel/modify bookings, view history.
 */
public class CustomerPanel extends MainPanel {

    private final AppActions actions;

    private JLabel title;
    private JTextField originField, destField, dateField;
    private JButton searchBtn, bookBtn, cancelBtn, modifyBtn, historyBtn;

    private JTable resultsTable;
    private JScrollPane resultsScroll;

    public CustomerPanel(AppActions actions) {
        this.actions = actions;

        setLayout(new BorderLayout());

        // --- Top Title ---
        title = new JLabel("Customer Dashboard", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        add(title, BorderLayout.NORTH);

        // --- Search Form Panel ---
        JPanel searchPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;

        originField = new JTextField();
        destField = new JTextField();
        dateField = new JTextField();

        searchBtn = new JButton("Search Flights");

        c.gridy = 0; c.gridx = 0; searchPanel.add(new JLabel("Origin:"), c);
        c.gridx = 1; searchPanel.add(originField, c);

        c.gridy = 1; c.gridx = 0; searchPanel.add(new JLabel("Destination:"), c);
        c.gridx = 1; searchPanel.add(destField, c);

        c.gridy = 2; c.gridx = 0; searchPanel.add(new JLabel("Date (YYYY-MM-DD):"), c);
        c.gridx = 1; searchPanel.add(dateField, c);

        c.gridy = 3; c.gridx = 0; c.gridwidth = 2;
        searchPanel.add(searchBtn, c);

        add(searchPanel, BorderLayout.WEST);

        // --- Flight Results Table ---
        resultsTable = new JTable(); // placeholder; controller fills model
        resultsScroll = new JScrollPane(resultsTable);
        add(resultsScroll, BorderLayout.CENTER);

        // --- Action Buttons ---
        JPanel actionPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        bookBtn = new JButton("Book Selected Flight");
        cancelBtn = new JButton("Cancel Reservation");
        modifyBtn = new JButton("Modify Reservation");
        historyBtn = new JButton("View Booking History");

        actionPanel.add(bookBtn);
        actionPanel.add(cancelBtn);
        actionPanel.add(modifyBtn);
        actionPanel.add(historyBtn);

        add(actionPanel, BorderLayout.EAST);

        // --- Callbacks (to controller) ---
        searchBtn.addActionListener(e -> {
            // actions.onCustomerSearch(originField.getText(), destField.getText(), dateField.getText());
        });

        bookBtn.addActionListener(e -> {
            // int row = resultsTable.getSelectedRow();
            // actions.onCustomerBook(row);
        });

        cancelBtn.addActionListener(e -> {
            // actions.onCustomerCancel();
        });

        modifyBtn.addActionListener(e -> {
            // actions.onCustomerModify();
        });

        historyBtn.addActionListener(e -> {
            // actions.onCustomerHistory();
        });
    }

    @Override
    public void refreshTheme(Theme t) {
        setBackground(t.bg);
        title.setForeground(t.fg);

        originField.setBackground(t.inputBg);
        originField.setForeground(t.inputFg);
        destField.setBackground(t.inputBg);
        destField.setForeground(t.inputFg);
        dateField.setBackground(t.inputBg);
        dateField.setForeground(t.inputFg);

        searchBtn.setBackground(t.buttonBg);
        searchBtn.setForeground(t.buttonFg);
        bookBtn.setBackground(t.buttonBg);
        bookBtn.setForeground(t.buttonFg);
        cancelBtn.setBackground(t.buttonBg);
        cancelBtn.setForeground(t.buttonFg);
        modifyBtn.setBackground(t.buttonBg);
        modifyBtn.setForeground(t.buttonFg);
        historyBtn.setBackground(t.buttonBg);
        historyBtn.setForeground(t.buttonFg);

        repaint();
    }
}
