package src.actions;

/**
 * Actions that a customer-facing view can trigger. This extends the
 * shared {@link UserActions} contract so customers automatically gain
 * access to common capabilities such as flight search.
 */
public interface CustomerActions extends UserActions {

    // Future customer-only extension points:
    // - viewMyReservations(...)
    // - bookFlight(...)
    // - cancelReservation(...)
}


