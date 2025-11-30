package src.actions;

/**
 * Aggregated application-level actions implemented by the top-level
 * controller. This type is useful when a caller needs to work across
 * multiple roles, but individual views should prefer the narrower
 * role-specific interfaces (LoginActions, CustomerActions, AgentActions, AdminActions).
 */
public interface AppActions extends LoginActions, CustomerActions, AgentActions, AdminActions {
    // Marker interface that composes all role-specific action sets.
}


