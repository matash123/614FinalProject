package src.controllers;

import src.database.ReservationCRUD;
import src.events.ControllerBus;
import src.events.ControllerBus.EventType;
import src.models.ReservationStatus;

/**
 * Controller for agent-facing operations, currently focused on
 * managing reservation status from the agent tools.
 */
public class AgentController {

    /**
     * Change the status of a reservation.
     *
     * Seat adjustments, refunds, etc. can be layered on later if needed.
     */
    public void changeReservationStatus(String reservationId, ReservationStatus newStatus) {
        if (reservationId == null || reservationId.isBlank()) {
            throw new IllegalArgumentException("reservationId is required");
        }
        if (newStatus == null) {
            throw new IllegalArgumentException("newStatus is required");
        }

        ReservationCRUD.updateStatus(reservationId.trim(), newStatus);

        if (newStatus == ReservationStatus.CANCELLED) {
            ControllerBus.getInstance().publish(EventType.RESERVATION_CANCELLED, reservationId.trim());
        } else {
            // Reuse RESERVATION_CREATED as a generic "reservation updated" signal for now.
            ControllerBus.getInstance().publish(EventType.RESERVATION_CREATED, reservationId.trim());
        }
    }
}


