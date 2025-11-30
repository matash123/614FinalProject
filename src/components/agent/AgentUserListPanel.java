package src.components.agent;

import java.awt.BorderLayout;
import java.awt.Font;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import src.config.Theme;
import src.database.userCRUD;
import src.models.User;
import src.views.DynamicPanel;
import src.views.PageController;

/**
 * Simple panel that shows all active users in a table.
 * Intended to be used from the agent dashboard via {@link PageController}.
 */
public class AgentUserListPanel extends DynamicPanel {

    private final JLabel titleLabel;
    private final JTable userTable;
    private final JScrollPane scrollPane;
    private final JButton viewReservationsButton;

    private PageController pageController;
    private List<User> currentUsers;

    public AgentUserListPanel() {
        setLayout(new BorderLayout());

        titleLabel = new JLabel("All Active Users", JLabel.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));

        userTable = new JTable();
        scrollPane = new JScrollPane(userTable);

        viewReservationsButton = new JButton("View reservations for selected user");
        viewReservationsButton.addActionListener(e -> openReservationsForSelectedUser());

        JPanel bottomBar = new JPanel(new BorderLayout());
        bottomBar.add(viewReservationsButton, BorderLayout.EAST);

        add(titleLabel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomBar, BorderLayout.SOUTH);

        // Initial load
        refreshData();
    }

    public void setPageController(PageController pageController) {
        this.pageController = pageController;
    }

    @Override
    public void refreshTheme(Theme t) {
        setBackground(t.bg);
        titleLabel.setForeground(t.fg);

        scrollPane.setBackground(t.bg);
        scrollPane.getViewport().setBackground(t.bg);

        userTable.setBackground(t.bg);
        userTable.setForeground(t.fg);
        userTable.setGridColor(t.fg);

        viewReservationsButton.setBackground(t.buttonBg);
        viewReservationsButton.setForeground(t.buttonFg);

        repaint();
    }

    @Override
    public void refreshData() {
        currentUsers = userCRUD.findAllActive();

        String[] cols = { "ID", "Username", "Role" };
        String[][] data = new String[currentUsers.size()][cols.length];

        for (int i = 0; i < currentUsers.size(); i++) {
            User u = currentUsers.get(i);
            data[i][0] = u.getUserId();
            data[i][1] = u.getName(); // we store username in the name field
            data[i][2] = u.getRole();
        }

        DefaultTableModel model = new DefaultTableModel(data, cols) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        userTable.setModel(model);
    }

    private void openReservationsForSelectedUser() {
        int row = userTable.getSelectedRow();
        if (row < 0 || currentUsers == null || row >= currentUsers.size()) {
            JOptionPane.showMessageDialog(
                this,
                "Please select a user first.",
                "No selection",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        User selected = currentUsers.get(row);
        if (pageController != null) {
            AgentUserReservationsPanel panel =
                new AgentUserReservationsPanel(selected.getUserId());
            pageController.show(panel);
        }
    }
}


