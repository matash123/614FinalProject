package src.components;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import src.config.Theme;

public class BookingList extends JPanel implements ThemeAware {

    private JPanel list;
    private JScrollPane scroll;

    public BookingList() {
        setLayout(new BorderLayout());
        setOpaque(true);

        list = new JPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setOpaque(true);

        scroll = new JScrollPane(list);
        scroll.setBorder(null);
        scroll.getViewport().setOpaque(true);

        add(scroll, BorderLayout.CENTER);
    }

    public void setBookings(List<String> bookings) {
        list.removeAll();

        for (String s : bookings) {
            JLabel row = new JLabel(s);
            row.setBorder(BorderFactory.createEmptyBorder(3, 6, 3, 6));
            row.setFont(new Font("Arial", Font.PLAIN, 13));
            row.setOpaque(true);
            list.add(row);
        }

        list.revalidate();
        list.repaint();
    }

    @Override
    public void refreshTheme(Theme t) {
        setBackground(t.bg);
        list.setBackground(t.bg);
        scroll.setBackground(t.bg);
        scroll.getViewport().setBackground(t.bg);

        for (Component c : list.getComponents()) {
            c.setForeground(t.fg);
            c.setBackground(t.bg);
        }
    }
}
