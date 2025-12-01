package src.components;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import src.config.Theme;
import src.controllers.AppController;
import src.controllers.UserController;
import src.database.CustomerCRUD;
import src.database.userCRUD;
import src.factory.ControllerFactory;
import src.models.Customer;
import src.models.User;
import src.views.DynamicPanel;
import src.views.PageController;

/**
 * Shared account editor for both end-users and agents.
 *
 * <ul>
 *   <li>In {@link Mode#SELF} the panel shows the currently logged-in user and
 *       only allows editing their own account (via {@link UserController}).</li>
 *   <li>In {@link Mode#AGENT} the panel shows a search box for user ID so
 *       agents can load and edit another user's account.</li>
 * </ul>
 *
 * It displays fields from both {@link User} and {@link Customer} where
 * applicable (for non-customer roles, the customer-specific fields are
 * simply left blank).
 */
public class AccountEditorPanel extends DynamicPanel {

    @FunctionalInterface
    private interface FieldRowAdder {
        void accept(String label, JTextField field, int rowActive);
    }

    public enum Mode {
        SELF,
        AGENT
    }

    private final Mode mode;

    private final UserController userController;

    private PageController pageController;

    // Agent-only search controls
    private JTextField searchUserIdField;
    private JButton loadUserButton;

    // Core account fields
    private JTextField userIdField;
    private JTextField roleField;
    private JTextField nameField;
    private JTextField emailField;

    // Customer-specific fields
    private JTextField rewardsField;
    private JTextField addressField;
    private JTextField phoneField;
    private JTextField creditCardField;

    // Password change (self only)
    private JPasswordField currentPasswordField;
    private JPasswordField newPasswordField;

    private JButton saveButton;
    private JButton changePasswordButton;

    // Currently loaded user for edit (in AGENT mode)
    private String loadedUserId;

    public AccountEditorPanel(Mode mode) {
        this.mode = mode;
        this.userController = ControllerFactory.getInstance().user();

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        buildHeader();
        buildForm();
        buildActions();

        refreshData();
    }

    public void setPageController(PageController pageController) {
        this.pageController = pageController;
    }

    private void buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        // Allow the themed parent background to show through for both light and dark modes.
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        String titleText = (mode == Mode.SELF)
            ? "My account details"
            : "Edit user account";
        JLabel titleLabel = new JLabel(titleText, JLabel.LEFT);

        header.add(titleLabel, BorderLayout.WEST);

        if (mode == Mode.AGENT) {
            JPanel searchBar = new JPanel(new BorderLayout(4, 0));
            JLabel searchLabel = new JLabel("User ID:");
            searchUserIdField = new JTextField(12);
            loadUserButton = new JButton("Load");
            loadUserButton.addActionListener(e -> loadUserForAgent());

            searchBar.add(searchLabel, BorderLayout.WEST);
            searchBar.add(searchUserIdField, BorderLayout.CENTER);
            searchBar.add(loadUserButton, BorderLayout.EAST);

            header.add(searchBar, BorderLayout.EAST);
        }

        add(header, BorderLayout.NORTH);
    }

    private void buildForm() {
        JPanel form = new JPanel(new GridBagLayout());
        // Transparent so the surrounding themed panel controls the background color.
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.0;

        int row = 0;

        // Helper to add a labeled field at a specific row
        FieldRowAdder addRow = (label, field, rowActive) -> {
            gbc.gridx = 0;
            gbc.gridy = rowActive;
            gbc.weightx = 0.0;
            form.add(new JLabel(label), gbc);

            gbc.gridx = 1;
            gbc.weightx = 1.0;
            form.add(field, gbc);
        };

        userIdField = new JTextField(20);
        userIdField.setEditable(false);
        addRow.accept("User ID:", userIdField, row);
        row++;

        roleField = new JTextField(20);
        roleField.setEditable(false);
        addRow.accept("Role:", roleField, row);
        row++;

        nameField = new JTextField(20);
        addRow.accept("Name:", nameField, row);
        row++;

        emailField = new JTextField(20);
        addRow.accept("Email:", emailField, row);
        row++;

        rewardsField = new JTextField(20);
        addRow.accept("Rewards number:", rewardsField, row);
        row++;

        addressField = new JTextField(20);
        addRow.accept("Address:", addressField, row);
        row++;

        phoneField = new JTextField(20);
        addRow.accept("Phone:", phoneField, row);
        row++;

        creditCardField = new JTextField(20);
        addRow.accept("Credit card:", creditCardField, row);
        row++;

        if (mode == Mode.SELF) {
            currentPasswordField = new JPasswordField(20);
            newPasswordField = new JPasswordField(20);

            gbc.gridx = 0;
            gbc.gridy = row;
            gbc.weightx = 0.0;
            form.add(new JLabel("Current password:"), gbc);

            gbc.gridx = 1;
            gbc.weightx = 1.0;
            form.add(currentPasswordField, gbc);
            row++;

            gbc.gridx = 0;
            gbc.gridy = row;
            gbc.weightx = 0.0;
            form.add(new JLabel("New password:"), gbc);

            gbc.gridx = 1;
            gbc.weightx = 1.0;
            form.add(newPasswordField, gbc);
            row++;
        }

        add(form, BorderLayout.CENTER);
    }

    private void buildActions() {
        JPanel actions = new JPanel();
        actions.setOpaque(false);

        saveButton = new JButton("Save profile");
        saveButton.addActionListener(e -> saveProfile());
        actions.add(saveButton);

        if (mode == Mode.SELF) {
            changePasswordButton = new JButton("Change password");
            changePasswordButton.addActionListener(e -> changePassword());
            actions.add(changePasswordButton);
        }

        add(actions, BorderLayout.SOUTH);
    }

    @Override
    public void refreshTheme(Theme t) {
        setBackground(t.bg);

        // Apply theme to text fields and buttons
        JTextField[] fields = {
            userIdField,
            roleField,
            nameField,
            emailField,
            rewardsField,
            addressField,
            phoneField,
            creditCardField
        };
        for (JTextField f : fields) {
            if (f == null) continue;
            f.setBackground(t.inputBg);
            f.setForeground(t.inputFg);
        }

        if (currentPasswordField != null) {
            currentPasswordField.setBackground(t.inputBg);
            currentPasswordField.setForeground(t.inputFg);
        }
        if (newPasswordField != null) {
            newPasswordField.setBackground(t.inputBg);
            newPasswordField.setForeground(t.inputFg);
        }

        JButton[] buttons = {
            saveButton,
            changePasswordButton,
            loadUserButton
        };
        for (JButton b : buttons) {
            if (b == null) continue;
            b.setBackground(t.buttonBg);
            b.setForeground(t.buttonFg);
        }

        // Ensure all labels in this panel use the themed foreground color,
        // including the header title and field labels.
        applyThemeToLabels(this, t);

        repaint();
    }

    private void applyThemeToLabels(Container root, Theme t) {
        for (Component c : root.getComponents()) {
            if (c instanceof JLabel lbl) {
                lbl.setForeground(t.fg);
            }
            if (c instanceof Container child) {
                applyThemeToLabels(child, t);
            }
        }
    }

    @Override
    public void refreshData() {
        if (mode == Mode.SELF) {
            loadCurrentUser();
        } else {
            // In agent mode we only load after an explicit search
            if (loadedUserId != null && !loadedUserId.isBlank()) {
                loadUserById(loadedUserId);
            } else {
                clearFields();
            }
        }
    }

    private void loadCurrentUser() {
        User current = userController.getCurrentUser();
        if (current == null) {
            JOptionPane.showMessageDialog(
                this,
                "No user is currently logged in.",
                "Not logged in",
                JOptionPane.WARNING_MESSAGE
            );
            clearFields();
            return;
        }

        loadedUserId = current.getUserId();
        populateFromUserAndCustomer(current, CustomerCRUD.getCustomerById(current.getUserId()));
    }

    private void loadUserForAgent() {
        String userId = searchUserIdField != null ? searchUserIdField.getText() : null;
        if (userId == null || userId.trim().isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "Please enter a user ID to load.",
                "Missing user ID",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        loadUserById(userId.trim());
    }

    private void loadUserById(String userId) {
        try {
            User u = userCRUD.getUserById(userId);
            if (u == null) {
                JOptionPane.showMessageDialog(
                    this,
                    "No user found with ID: " + userId,
                    "Not found",
                    JOptionPane.WARNING_MESSAGE
                );
                clearFields();
                loadedUserId = null;
                return;
            }
            loadedUserId = u.getUserId();
            Customer c = CustomerCRUD.getCustomerById(userId);
            populateFromUserAndCustomer(u, c);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(
                this,
                "Error while loading user: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void populateFromUserAndCustomer(User u, Customer c) {
        userIdField.setText(u.getUserId());
        roleField.setText(u.getRole());
        nameField.setText(u.getName());
        emailField.setText(u.getEmail());

        if (c != null) {
            rewardsField.setText(c.getCustomerRewardsNumber());
            addressField.setText(c.getAddress());
            phoneField.setText(c.getPhoneNumber());
            creditCardField.setText(c.getCreditCardNumber());
        } else {
            rewardsField.setText("");
            addressField.setText("");
            phoneField.setText("");
            creditCardField.setText("");
        }

        if (mode == Mode.SELF && currentPasswordField != null && newPasswordField != null) {
            currentPasswordField.setText("");
            newPasswordField.setText("");
        }
    }

    private void clearFields() {
        if (userIdField != null) userIdField.setText("");
        if (roleField != null) roleField.setText("");
        if (nameField != null) nameField.setText("");
        if (emailField != null) emailField.setText("");
        if (rewardsField != null) rewardsField.setText("");
        if (addressField != null) addressField.setText("");
        if (phoneField != null) phoneField.setText("");
        if (creditCardField != null) creditCardField.setText("");
        if (currentPasswordField != null) currentPasswordField.setText("");
        if (newPasswordField != null) newPasswordField.setText("");
    }

    private void saveProfile() {
        if (loadedUserId == null || loadedUserId.isBlank()) {
            JOptionPane.showMessageDialog(
                this,
                "No user is loaded.",
                "Nothing to save",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        String newName = nameField.getText();
        String newEmail = emailField.getText();
        String rewards = rewardsField.getText();
        String address = addressField.getText();
        String phone   = phoneField.getText();
        String credit  = creditCardField.getText();

        try {
            if (mode == Mode.SELF) {
                // Use controller methods so ownership checks are enforced.
                userController.updateName(loadedUserId, newName);
                userController.updateEmail(loadedUserId, newEmail);
            } else {
                // Agent: bypass self-check and update directly via CRUD helpers.
                if (newName != null && !newName.isBlank()) {
                    userCRUD.updateName(loadedUserId, newName);
                }
                if (newEmail != null && !newEmail.isBlank()) {
                    userCRUD.updateEmail(loadedUserId, newEmail);
                }
            }

            // Customer-specific profile (for customer users only; safe as a no-op)
            CustomerCRUD.upsertCustomerProfile(
                loadedUserId,
                rewards,
                address,
                phone,
                credit
            );

            JOptionPane.showMessageDialog(
                this,
                "Profile updated successfully.",
                "Success",
                JOptionPane.INFORMATION_MESSAGE
            );

            // Reload from DB to reflect any changes.
            if (mode == Mode.SELF) {
                loadCurrentUser();
            } else {
                loadUserById(loadedUserId);
            }

            // Also refresh the outer app view so header UserBox components pick
            // up the updated name/email/role for the current session user.
            AppController app = AppController.getInstance();
            if (app != null) {
                app.updateAppView();
            }

        } catch (SecurityException ex) {
            JOptionPane.showMessageDialog(
                this,
                ex.getMessage(),
                "Permission denied",
                JOptionPane.WARNING_MESSAGE
            );
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
                "Error while saving profile: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void changePassword() {
        if (mode != Mode.SELF) {
            return;
        }
        if (loadedUserId == null || loadedUserId.isBlank()) {
            JOptionPane.showMessageDialog(
                this,
                "No user is loaded.",
                "Nothing to update",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        String current = currentPasswordField != null
            ? new String(currentPasswordField.getPassword())
            : "";
        String next = newPasswordField != null
            ? new String(newPasswordField.getPassword())
            : "";

        try {
            userController.updatePassword(loadedUserId, current, next);
            JOptionPane.showMessageDialog(
                this,
                "Password updated successfully.",
                "Success",
                JOptionPane.INFORMATION_MESSAGE
            );
            if (currentPasswordField != null) currentPasswordField.setText("");
            if (newPasswordField != null) newPasswordField.setText("");
        } catch (IllegalArgumentException | SecurityException ex) {
            JOptionPane.showMessageDialog(
                this,
                ex.getMessage(),
                "Validation error",
                JOptionPane.WARNING_MESSAGE
            );
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(
                this,
                "Error while updating password: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
}


