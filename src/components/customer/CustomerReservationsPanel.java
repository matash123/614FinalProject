package src.components.customer;

import java.awt.BorderLayout;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import src.components.ReservationTablePanel;
import src.config.Theme;
import src.controllers.UserController;
import src.database.ReservationCRUD;
import src.factory.ControllerFactory;
import src.models.Reservation;
import src.models.User;
import src.views.DynamicPanel;
import src.views.PageController;

/**
 * Customer-facing reservation panel for the dashboard header.
 *
 * - Shows a table of reservations for the currently logged-in user
 * - Allows the user to edit or delete the selected reservation
 *
 * All domain work (loading & deleting) is delegated to ReservationCRUD
 * and controllers obtained from {@link ControllerFactory}.
 */
 public class CustomerReservationsPanel extends DynamicPanel {
 
     private final ReservationTablePanel reservationTable;
     private final UserController userController;
 
     private PageController pageController;
     private List<Reservation> currentReservations = List.of();
 
     private JLabel titleLabel;
     private JButton editButton;
     private JButton deleteButton;

    public CustomerReservationsPanel() {
        this.userController = ControllerFactory.getInstance().user();

        setLayout(new BorderLayout());
        setOpaque(true);
        setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        reservationTable = new ReservationTablePanel();

        buildHeader();
        buildBottomActions();

        add(reservationTable, BorderLayout.CENTER);
    }

     public void setPageController(PageController pageController) {
         this.pageController = pageController;
     }

    private void buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        titleLabel = new JLabel("My reservations", JLabel.LEFT);
        header.add(titleLabel, BorderLayout.WEST);

        add(header, BorderLayout.NORTH);
    }

    private void buildBottomActions() {
        JPanel actions = new JPanel();
        actions.setOpaque(false);

        editButton = new JButton("Edit selected");
        deleteButton = new JButton("Delete selected");

        editButton.addActionListener(e -> editSelectedReservation());
        deleteButton.addActionListener(e -> deleteSelectedReservation());

        actions.add(editButton);
        actions.add(deleteButton);

        add(actions, BorderLayout.SOUTH);
    }

    private Reservation resolveSelectedReservation() {
        int row = reservationTable.getSelectedRowIndex();
        if (row < 0 || currentReservations == null || row >= currentReservations.size()) {
            JOptionPane.showMessageDialog(
                this,
                "Please select a reservation first.",
                "No selection",
                JOptionPane.WARNING_MESSAGE
            );
            return null;
        }
        return currentReservations.get(row);
    }

    private void editSelectedReservation() {
        User currentUser = userController.getCurrentUser();
        if (currentUser == null) {
            JOptionPane.showMessageDialog(
                this,
                "You must be logged in as a customer to edit reservations.",
                "Not logged in",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        Reservation r = resolveSelectedReservation();
        if (r == null) {
            return;
        }

        if (r.getUser() == null
            || r.getUser().getUserId() == null
            || !r.getUser().getUserId().equals(currentUser.getUserId())) {
            JOptionPane.showMessageDialog(
                this,
                "You can only edit your own reservations.",
                "Permission denied",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (pageController == null) {
            JOptionPane.showMessageDialog(
                this,
                "Unable to open editor – page controller not set.",
                "Configuration error",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }

         // Capture this panel so we can refresh its data after editing;
         // navigation between main views is handled via the PageController.
         CustomerReservationEditor editor = new CustomerReservationEditor(r);
        editor.setPageController(pageController);
        pageController.show(editor);
    }

    private void deleteSelectedReservation() {
        User currentUser = userController.getCurrentUser();
        if (currentUser == null) {
            JOptionPane.showMessageDialog(
                this,
                "You must be logged in as a customer to delete reservations.",
                "Not logged in",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        Reservation r = resolveSelectedReservation();
        if (r == null) {
            return;
        }

        if (r.getUser() == null
            || r.getUser().getUserId() == null
            || !r.getUser().getUserId().equals(currentUser.getUserId())) {
            JOptionPane.showMessageDialog(
                this,
                "You can only delete your own reservations.",
                "Permission denied",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Delete reservation " + r.getReservationId() + "?",
            "Confirm deletion",
            JOptionPane.YES_NO_OPTION
        );
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            ReservationCRUD.deleteById(r.getReservationId());

            JOptionPane.showMessageDialog(
                this,
                "Reservation deleted.",
                "Deleted",
                JOptionPane.INFORMATION_MESSAGE
            );

            refreshData();

            // Also ask app controller to refresh other views (e.g., badges)
            src.controllers.AppController app = src.controllers.AppController.getInstance();
            if (app != null) {
                app.updateAppView();
            }

            // After deletion, send the customer back to flight search view.
            if (pageController != null) {
                FlightSearchPanel searchPanel =
                    new FlightSearchPanel(FlightSearchPanel.Mode.CUSTOMER);
                searchPanel.setPageController(pageController);
                pageController.show(searchPanel);
            }
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(
                this,
                "Error while deleting reservation: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    @Override
    public void refreshTheme(Theme t) {
        setBackground(t.bg);

        if (titleLabel != null) {
            titleLabel.setForeground(t.fg);
        }
        if (editButton != null) {
            editButton.setBackground(t.buttonBg);
            editButton.setForeground(t.buttonFg);
        }
        if (deleteButton != null) {
            deleteButton.setBackground(t.buttonBg);
            deleteButton.setForeground(t.buttonFg);
        }

        reservationTable.refreshTheme(t);
        repaint();
    }

    @Override
    public void refreshData() {
        User currentUser = userController.getCurrentUser();
        if (currentUser == null || currentUser.getUserId() == null) {
            currentReservations = List.of();
            reservationTable.setReservations(currentReservations);
            return;
        }

        String userId = currentUser.getUserId();
        try {
            currentReservations = ReservationCRUD.findByUserId(userId);
            reservationTable.setReservations(currentReservations);
        } catch (RuntimeException ex) {
            currentReservations = List.of();
            reservationTable.setReservations(currentReservations);
        }
    }
}


