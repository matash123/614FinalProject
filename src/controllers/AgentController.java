package src.controllers;

import java.util.List;
import src.database.FlightCrud;
import src.database.ReservationCRUD;
import src.events.ControllerBus;
import src.events.ControllerBus.EventType;
import src.factory.ControllerFactory;
import src.models.Flight;
import src.models.ReservationStatus;
import src.models.User;

//handles agent operations for customer bookings and flight management
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

    //nice callable functions for agent pages

    //find flights by criteria
    public List<Flight> findFlights(String origin, String destination, String date, String flightId) {
        return FlightCrud.searchFlights(origin, destination, date, flightId);
    }

    //book flight for a customer
    public boolean bookForCustomer(String customerId, String flightId, int seats, String cardToken) {
        if (customerId == null || flightId == null || seats <= 0) {
            throw new IllegalArgumentException("invalid booking parameters");
        }
        //todo implement - would need to get User and Flight objects, then call BookingController
        //for now return false as placeholder
        return false;
    }

    //cancel customer booking
    public boolean cancelCustomerBooking(String reservationId) {
        if (reservationId == null || reservationId.isBlank()) {
            throw new IllegalArgumentException("reservationId required");
        }
        try {
            ReservationCRUD.updateStatus(reservationId, ReservationStatus.CANCELLED);
            ControllerBus.getInstance().publish(EventType.RESERVATION_CANCELLED, reservationId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    //reassign customer to different flight
    public boolean reassignCustomerFlight(String reservationId, String newFlightId) {
        if (reservationId == null || reservationId.isBlank()) {
            throw new IllegalArgumentException("reservationId required");
        }
        if (newFlightId == null || newFlightId.isBlank()) {
            throw new IllegalArgumentException("newFlightId required");
        }
        //todo implement - would need to cancel old reservation and create new one
        //for now return false as placeholder
        return false;
    }


    public void updateReservationSeats(String reservationId, int newSeats, User actor) {
        ControllerFactory
            .getInstance()
            .booking()
            .updateReservationSeats(reservationId, newSeats, actor);
    }
}


