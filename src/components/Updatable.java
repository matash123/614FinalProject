package src.components;

/**
 * Simple lifecycle hook for panels/components that need to refresh
 * their data from controllers/services without being recreated.
 *
 * This is intentionally similar in spirit to {@link ThemeAware} but
 * focused on domain/data changes instead of styling.
 */
public interface Updatable {
    /**
     * Refresh this component's data from the current application state.
     * Implementors should hard-code any required update calls to their
     * child components.
     */
    void refreshData();
}


