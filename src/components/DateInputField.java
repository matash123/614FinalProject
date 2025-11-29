package src.components;

import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.text.ParseException;

import src.components.ThemeAware;
import src.config.Theme;

/**
 * Reusable masked date input field (yyyy-MM-dd) with optional label,
 * used by flight search panels and other date-based filters.
 */
public class DateInputField extends JPanel implements ThemeAware {

    private final JLabel label;
    private final JFormattedTextField field;

    public DateInputField(String labelText) {
        setLayout(new BorderLayout(4, 0));

        this.label = new JLabel(labelText);
        this.field = createDateField();

        add(this.label, BorderLayout.WEST);
        add(this.field, BorderLayout.CENTER);
    }

    private JFormattedTextField createDateField() {
        try {
            MaskFormatter formatter = new MaskFormatter("####-##-##");
            formatter.setPlaceholderCharacter('_');
            return new JFormattedTextField(formatter);
        } catch (ParseException e) {
            System.err.println("Failed to create date mask: " + e.getMessage());
            return new JFormattedTextField();
        }
    }

    public String getText() {
        return field.getText();
    }

    public void setText(String text) {
        field.setText(text);
    }

    @Override
    public void refreshTheme(Theme t) {
        setBackground(t.bg);
        label.setForeground(t.fg);

        field.setBackground(t.inputBg);
        field.setForeground(t.inputFg);
    }
}


