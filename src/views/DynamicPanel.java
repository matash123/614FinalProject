package src.views;

import javax.swing.*;
import src.components.ThemeAware;
import src.components.Updatable;
import src.config.Theme;

/**
 * Base class for top-level and nested "page" panels.
 *
 * Implements both {@link ThemeAware} and {@link Updatable} so that
 * containers can refresh styling and data consistently.
 *
 * This was previously named {@code MainPanel}; it has been renamed to
 * better reflect its role as a reusable dynamic content panel that can
 * be used by any view or sub-component.
 */
public abstract class DynamicPanel extends JPanel implements ThemeAware, Updatable {

    @Override
    public abstract void refreshTheme(Theme t);

    @Override
    public void refreshData() {
        // Default no-op; concrete panels can override if they need
        // to reload their data or update child components.
    }
}


