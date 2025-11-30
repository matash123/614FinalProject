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
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import src.components.DateInputField;
import src.config.Theme;
import src.controllers.PromotionController;
import src.models.Promotion;
import src.views.DynamicPanel;
import src.views.PageController;

//admin panel for managing promotional news items
public class PromotionalNewsEditor extends DynamicPanel {

    private final PromotionController promotionController;
    private PageController pageController;

    //header
    private JLabel titleLabel;

    //list panel
    private JPanel listPanel;
    private JList<Promotion> promotionList;
    private DefaultListModel<Promotion> listModel;
    private JButton newButton;
    private JButton deleteButton;

    //form panel
    private JPanel formPanel;
    private JTextField idField;
    private JTextField titleField;
    private JTextArea messageArea;
    private DateInputField startDateField;
    private DateInputField endDateField;
    private JButton saveButton;
    private JButton cancelButton;

    private Promotion selectedPromotion;

    public PromotionalNewsEditor() {
        this.promotionController = new PromotionController();

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        buildHeader();
        buildListPanel();
        buildFormPanel();

        loadPromotions();
    }

    private void buildHeader() {
        titleLabel = new JLabel("Promotional News Editor", SwingConstants.LEFT);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);
    }

    private void buildListPanel() {
        listPanel = new JPanel(new BorderLayout());
        listPanel.setBorder(BorderFactory.createTitledBorder("Promotions"));

        listModel = new DefaultListModel<>();
        promotionList = new JList<>(listModel);
        promotionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        promotionList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                handlePromotionSelected();
            }
        });

        JScrollPane listScroll = new JScrollPane(promotionList);
        listPanel.add(listScroll, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        newButton = new JButton("New Promotion");
        deleteButton = new JButton("Delete");
        deleteButton.setEnabled(false);

        newButton.addActionListener(e -> handleNew());
        deleteButton.addActionListener(e -> handleDelete());

        buttonPanel.add(newButton);
        buttonPanel.add(deleteButton);
        listPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(listPanel, BorderLayout.WEST);
    }

    private void buildFormPanel() {
        formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Edit Promotion"));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 8, 6, 8);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0;

        int row = 0;

        //ID field (read-only)
        JLabel idLabel = new JLabel("Promotion ID:");
        idField = new JTextField(16);
        idField.setEditable(false);

        c.gridx = 0; c.gridy = row; c.weightx = 0;
        formPanel.add(idLabel, c);
        c.gridx = 1; c.gridy = row; c.weightx = 1.0;
        formPanel.add(idField, c);
        row++;

        //Title field
        JLabel titleLabel = new JLabel("Title:");
        titleField = new JTextField(30);

        c.gridx = 0; c.gridy = row; c.weightx = 0;
        formPanel.add(titleLabel, c);
        c.gridx = 1; c.gridy = row; c.weightx = 1.0;
        formPanel.add(titleField, c);
        row++;

        //Message area
        JLabel messageLabel = new JLabel("Message:");
        messageArea = new JTextArea(5, 30);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        JScrollPane messageScroll = new JScrollPane(messageArea);

        c.gridx = 0; c.gridy = row; c.weightx = 0;
        formPanel.add(messageLabel, c);
        c.gridx = 1; c.gridy = row; c.weightx = 1.0;
        formPanel.add(messageScroll, c);
        row++;

        //Start date
        JLabel startDateLabel = new JLabel("Start Date:");
        startDateField = new DateInputField("");

        c.gridx = 0; c.gridy = row; c.weightx = 0;
        formPanel.add(startDateLabel, c);
        c.gridx = 1; c.gridy = row; c.weightx = 1.0;
        formPanel.add(startDateField, c);
        row++;

        //End date
        JLabel endDateLabel = new JLabel("End Date:");
        endDateField = new DateInputField("");

        c.gridx = 0; c.gridy = row; c.weightx = 0;
        formPanel.add(endDateLabel, c);
        c.gridx = 1; c.gridy = row; c.weightx = 1.0;
        formPanel.add(endDateField, c);
        row++;

        //Buttons
        JPanel buttonPanel = new JPanel();
        saveButton = new JButton("Save");
        cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> handleSave());
        cancelButton.addActionListener(e -> handleCancel());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        c.gridx = 0; c.gridy = row; c.gridwidth = 2;
        formPanel.add(buttonPanel, c);

        JScrollPane formScroll = new JScrollPane(formPanel);
        formScroll.setBorder(null);
        add(formScroll, BorderLayout.CENTER);
    }

    private void loadPromotions() {
        listModel.clear();
        try {
            List<Promotion> promotions = promotionController.getAllPromotions();
            for (Promotion p : promotions) {
                listModel.addElement(p);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                this,
                "Error loading promotions: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void handlePromotionSelected() {
        selectedPromotion = promotionList.getSelectedValue();
        if (selectedPromotion != null) {
            populateForm(selectedPromotion);
            deleteButton.setEnabled(true);
        } else {
            clearForm();
            deleteButton.setEnabled(false);
        }
    }

    private void populateForm(Promotion promo) {
        idField.setText(promo.getPromotionId());
        titleField.setText(promo.getTitle());
        messageArea.setText(promo.getMessage());
        if (promo.getStartDate() != null) {
            startDateField.setText(promo.getStartDate().toString());
        } else {
            startDateField.setText("");
        }
        if (promo.getEndDate() != null) {
            endDateField.setText(promo.getEndDate().toString());
        } else {
            endDateField.setText("");
        }
    }

    private void clearForm() {
        idField.setText("");
        titleField.setText("");
        messageArea.setText("");
        startDateField.setText("");
        endDateField.setText("");
        selectedPromotion = null;
    }

    private void handleNew() {
        clearForm();
        promotionList.clearSelection();
        deleteButton.setEnabled(false);
    }

    private void handleSave() {
        String title = titleField.getText() != null ? titleField.getText().trim() : "";
        String message = messageArea.getText() != null ? messageArea.getText().trim() : "";
        String startDateStr = startDateField.getText();
        String endDateStr = endDateField.getText();

        if (title.isBlank()) {
            JOptionPane.showMessageDialog(
                this,
                "Title is required.",
                "Validation error",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (message.isBlank()) {
            JOptionPane.showMessageDialog(
                this,
                "Message is required.",
                "Validation error",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (startDateStr == null || startDateStr.contains("%")) {
            JOptionPane.showMessageDialog(
                this,
                "Please enter a full start date in the form yyyy-mm-dd.",
                "Validation error",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (endDateStr == null || endDateStr.contains("%")) {
            JOptionPane.showMessageDialog(
                this,
                "Please enter a full end date in the form yyyy-mm-dd.",
                "Validation error",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        LocalDate startDate;
        LocalDate endDate;
        try {
            startDate = LocalDate.parse(startDateStr);
            endDate = LocalDate.parse(endDateStr);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(
                this,
                "Invalid date format. Please use yyyy-mm-dd.",
                "Validation error",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        try {
            if (selectedPromotion != null) {
                //update existing
                promotionController.updatePromotion(
                    selectedPromotion.getPromotionId(),
                    title,
                    message,
                    startDate,
                    endDate
                );
                JOptionPane.showMessageDialog(
                    this,
                    "Promotion updated successfully.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
                );
            } else {
                //create new
                promotionController.createPromotion(title, message, startDate, endDate);
                JOptionPane.showMessageDialog(
                    this,
                    "Promotion created successfully.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
                );
            }
            loadPromotions();
            clearForm();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(
                this,
                ex.getMessage(),
                "Validation error",
                JOptionPane.WARNING_MESSAGE
            );
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                this,
                "Error saving promotion: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void handleDelete() {
        if (selectedPromotion == null) {
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Delete promotion \"" + selectedPromotion.getTitle() + "\"? This cannot be undone.",
            "Confirm delete",
            JOptionPane.YES_NO_OPTION
        );
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            promotionController.deletePromotion(selectedPromotion.getPromotionId());
            JOptionPane.showMessageDialog(
                this,
                "Promotion deleted.",
                "Deleted",
                JOptionPane.INFORMATION_MESSAGE
            );
            loadPromotions();
            clearForm();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                this,
                "Error deleting promotion: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void handleCancel() {
        clearForm();
        promotionList.clearSelection();
    }

    @Override
    public void refreshTheme(Theme t) {
        setBackground(t.bg);

        if (titleLabel != null) {
            titleLabel.setForeground(t.fg);
        }

        if (listPanel != null) {
            listPanel.setBackground(t.bg);
        }
        if (promotionList != null) {
            promotionList.setBackground(t.bg);
            promotionList.setForeground(t.fg);
        }

        if (formPanel != null) {
            formPanel.setBackground(t.bg);
        }
        if (idField != null) {
            idField.setBackground(t.inputBg);
            idField.setForeground(t.inputFg);
        }
        if (titleField != null) {
            titleField.setBackground(t.inputBg);
            titleField.setForeground(t.inputFg);
        }
        if (messageArea != null) {
            messageArea.setBackground(t.inputBg);
            messageArea.setForeground(t.inputFg);
        }
        if (startDateField != null) {
            startDateField.refreshTheme(t);
        }
        if (endDateField != null) {
            endDateField.refreshTheme(t);
        }

        if (newButton != null) {
            newButton.setBackground(t.buttonBg);
            newButton.setForeground(t.buttonFg);
        }
        if (deleteButton != null) {
            deleteButton.setBackground(t.buttonBg);
            deleteButton.setForeground(t.buttonFg);
        }
        if (saveButton != null) {
            saveButton.setBackground(t.buttonBg);
            saveButton.setForeground(t.buttonFg);
        }
        if (cancelButton != null) {
            cancelButton.setBackground(t.buttonBg);
            cancelButton.setForeground(t.buttonFg);
        }

        repaint();
    }

    public void setPageController(PageController pageController) {
        this.pageController = pageController;
    }
}

