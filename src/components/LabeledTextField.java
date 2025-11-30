package src.components;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import src.config.Theme;

/**
 * Simple reusable labeled text input that participates in the theme system.
 * Mirrors the structure of {@link DateInputField}, but for free-form text.
 */
public class LabeledTextField extends JPanel implements ThemeAware {

    private final JLabel label;
    private final JTextField field;

    public LabeledTextField(String labelText) {
        setLayout(new BorderLayout(4, 0));

        this.label = new JLabel(labelText);
        this.field = new JTextField();

        add(this.label, BorderLayout.WEST);
        add(this.field, BorderLayout.CENTER);
    }

    public String getText() {
        return field.getText();
    }

    public void setText(String text) {
        field.setText(text);
    }

    public JTextField getInnerField() {
        return field;
    }

    @Override
    public void refreshTheme(Theme t) {
        setBackground(t.bg);
        label.setForeground(t.fg);

        field.setBackground(t.inputBg);
        field.setForeground(t.inputFg);
    }
}


