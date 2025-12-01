package src.components.customer;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import src.config.Theme;
import src.controllers.PromotionController;
import src.factory.ControllerFactory;
import src.models.Promotion;
import src.views.DynamicPanel;
import src.views.PageController;

public class CustomerPromotionsPanel extends DynamicPanel {

    private final PromotionController promotionController;
    private JPanel promotionsContainer;
    private JLabel titleLabel;

    public CustomerPromotionsPanel() {
        this.promotionController = ControllerFactory.getInstance().promotions();

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        buildHeader();
        buildPromotionsList();
        loadActivePromotions();
    }

    private void buildHeader() {
        titleLabel = new JLabel("Current Promotions", SwingConstants.LEFT);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);
    }

    private void buildPromotionsList() {
        promotionsContainer = new JPanel();
        promotionsContainer.setLayout(new GridBagLayout());
        promotionsContainer.setBorder(BorderFactory.createTitledBorder("Available Promotions"));

        JScrollPane scrollPane = new JScrollPane(promotionsContainer);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadActivePromotions() {
        promotionsContainer.removeAll();

        try {
            List<Promotion> activePromotions = promotionController.getActivePromotions();

            if (activePromotions.isEmpty()) {
                JLabel noPromosLabel = new JLabel("No active promotions at this time.", SwingConstants.CENTER);
                noPromosLabel.setFont(new Font("SansSerif", Font.ITALIC, 14));
                GridBagConstraints c = new GridBagConstraints();
                c.gridx = 0;
                c.gridy = 0;
                c.weightx = 1.0;
                c.insets = new Insets(20, 20, 20, 20);
                promotionsContainer.add(noPromosLabel, c);
            } else {
                GridBagConstraints c = new GridBagConstraints();
                c.gridx = 0;
                c.gridy = 0;
                c.weightx = 1.0;
                c.fill = GridBagConstraints.HORIZONTAL;
                c.insets = new Insets(10, 10, 10, 10);

                for (Promotion promo : activePromotions) {
                    JPanel promoPanel = createPromotionCard(promo);
                    c.gridy++;
                    promotionsContainer.add(promoPanel, c);
                }
            }

            promotionsContainer.revalidate();
            promotionsContainer.repaint();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                this,
                "Error loading promotions: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private JPanel createPromotionCard(Promotion promo) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createRaisedBevelBorder(),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel titleLabel = new JLabel(promo.getTitle(), SwingConstants.LEFT);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        card.add(titleLabel, BorderLayout.NORTH);

        JTextArea messageArea = new JTextArea(promo.getMessage());
        messageArea.setEditable(false);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        messageArea.setOpaque(false);
        card.add(messageArea, BorderLayout.CENTER);

        String dateRange = String.format("Valid: %s to %s", 
            promo.getStartDate().toString(), 
            promo.getEndDate().toString());
        JLabel infoLabel = new JLabel(
            String.format("Promotion ID: %s | %s", promo.getPromotionId(), dateRange),
            SwingConstants.LEFT
        );
        infoLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        card.add(infoLabel, BorderLayout.SOUTH);

        return card;
    }

    @Override
    public void refreshTheme(Theme t) {
        setBackground(t.bg);
        if (titleLabel != null) {
            titleLabel.setForeground(t.fg);
        }
        if (promotionsContainer != null) {
            promotionsContainer.setBackground(t.bg);
            for (java.awt.Component comp : promotionsContainer.getComponents()) {
                if (comp instanceof JPanel) {
                    comp.setBackground(t.bg);
                    for (java.awt.Component child : ((JPanel) comp).getComponents()) {
                        if (child instanceof JLabel) {
                            ((JLabel) child).setForeground(t.fg);
                        } else if (child instanceof JTextArea) {
                            ((JTextArea) child).setForeground(t.fg);
                            ((JTextArea) child).setBackground(t.bg);
                        }
                    }
                }
            }
        }
        repaint();
    }

    public void setPageController(PageController pageController) {
        // Method kept for compatibility but pageController is not used
    }
}

