package src.components.customer;

import java.awt.BorderLayout;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import src.config.Theme;
import src.models.Promotion;
import src.views.DynamicPanel;

/**
 * Center-panel view for showing the details of a single promotion.
 *
 * Used by {@link src.views.CustomerPanel} when the customer clicks on the
 * latest promotion in the header or chooses one from the dropdown.
 */
public class CustomerPromotionDetailsPanel extends DynamicPanel {

    private final Promotion promotion;

    private JLabel titleLabel;
    private JLabel dateRangeLabel;
    private JTextArea messageArea;

    public CustomerPromotionDetailsPanel(Promotion promotion) {
        this.promotion = promotion;

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        buildHeader();
        buildBody();
    }

    private void buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        String title = promotion != null && promotion.getTitle() != null
            ? promotion.getTitle()
            : "Promotion details";

        titleLabel = new JLabel(title, JLabel.LEFT);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 18f));

        StringBuilder dates = new StringBuilder();
        if (promotion != null) {
            if (promotion.getStartDate() != null) {
                dates.append("From ").append(promotion.getStartDate());
            }
            if (promotion.getEndDate() != null) {
                if (dates.length() > 0) {
                    dates.append(" ");
                }
                dates.append("until ").append(promotion.getEndDate());
            }
        }

        dateRangeLabel = new JLabel(dates.toString(), JLabel.LEFT);

        header.add(titleLabel, BorderLayout.NORTH);
        header.add(dateRangeLabel, BorderLayout.SOUTH);

        add(header, BorderLayout.NORTH);
    }

    private void buildBody() {
        messageArea = new JTextArea();
        messageArea.setEditable(false);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);

        String message = promotion != null && promotion.getMessage() != null
            ? promotion.getMessage()
            : "No additional details are available for this promotion.";
        messageArea.setText(message);

        JScrollPane scroll = new JScrollPane(messageArea);
        scroll.setBorder(BorderFactory.createTitledBorder("Promotion details"));

        add(scroll, BorderLayout.CENTER);
    }

    @Override
    public void refreshTheme(Theme t) {
        setBackground(t.bg);

        if (titleLabel != null) {
            titleLabel.setForeground(t.fg);
        }
        if (messageArea != null) {
            messageArea.setBackground(t.inputBg);
            messageArea.setForeground(t.inputFg);
        }

        repaint();
    }
}


