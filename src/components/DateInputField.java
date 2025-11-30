package src.components;

import java.awt.*;
import javax.swing.*;
import javax.swing.text.*;
import src.config.Theme;

/**
 * Simpler DateInputField:
 * - Starts with example template "yyyy-mm-dd"
 * - Allows digits and dashes only
 * - User can freely enter partial dates (YYYY, YYYY-MM, YYYY-MM-D)
 * - No template enforcement, no caret manipulation
 * - Preserves original getText() logic
 */
public class DateInputField extends JPanel implements ThemeAware {

    private final JLabel label;
    private final JTextField field;

    public DateInputField(String labelText) {
        setLayout(new BorderLayout(4, 0));

        this.label = new JLabel(labelText);
        this.field = new JTextField(10);

        field.setText("yyyy-mm-dd");  // Only a visual hint
        field.setFont(new Font("Monospaced", Font.PLAIN, 14));

        ((AbstractDocument) field.getDocument())
                .setDocumentFilter(new DigitsAndDashesOnly());

        add(this.label, BorderLayout.WEST);
        add(this.field, BorderLayout.CENTER);
    }

    /**
     * Same normalization rules as before.
     */
   
    public String getText() {
        String raw = field.getText();
        if (raw == null) return null;

        String trimmed = raw.trim();
        if (trimmed.isEmpty()) return null;

        // Strip letters from template hint
        String cleaned = trimmed.replaceAll("[A-Za-z]", "");

        // Keep only digits
        String digits = cleaned.replaceAll("[^0-9]", "");
        if (digits.isEmpty()) return null;

        if (digits.length() <= 4) {
            String year = digits.substring(0, Math.min(4, digits.length()));
            return year + "%";
        }

        if (digits.length() <= 6) {
            String year = digits.substring(0, 4);
            String month = digits.substring(4, Math.min(6, digits.length()));
            return year + "-" + month + "%";
        }

        String year = digits.substring(0, 4);
        String month = digits.substring(4, 6);
        String day = digits.substring(6, 8);
        return year + "-" + month + "-" + day;
    }


    public void setText(String text) {
        if (text == null || text.isEmpty()) {
            field.setText("yyyy-mm-dd");
            return;
        }
        field.setText(text);
    }

    @Override
    public void refreshTheme(Theme t) {
        setBackground(t.bg);
        label.setForeground(t.fg);

        field.setBackground(t.inputBg);
        field.setForeground(t.inputFg);
    }

    /**
     * Extremely simple filter:
     * allow only digits, dash, and delete operations.
     */
    private static class DigitsAndDashesOnly extends DocumentFilter {

        @Override
        public void insertString(FilterBypass fb, int offset, String str, AttributeSet a)
                throws BadLocationException {

            if (str != null && str.matches("[0-9\\-]+"))
                super.insertString(fb, offset, str, a);
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String str, AttributeSet a)
                throws BadLocationException {

            if (str != null && str.matches("[0-9\\-]+"))
                super.replace(fb, offset, length, str, a);
        }
    }
}
