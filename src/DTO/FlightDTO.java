package src.DTO;

import java.time.LocalDate;

import src.models.Flight;

/**
 * Lightweight data-transfer object for exposing flight information
 * to the UI layer (tables, search panels, etc.) without leaking the
 * full domain model or database concerns.
 */
public record FlightDTO(
    String flightId,
    String origin,
    String destination,
    LocalDate date,
    String airlineName,
    double price
) {

    /**
     * Convenience mapper from the rich domain model to this DTO.
     * Safe to call with a null airline – airlineName will be "".
     */
    public static FlightDTO fromModel(Flight f) {
        if (f == null) {
            throw new IllegalArgumentException("Flight cannot be null");
        }

        String airlineName = (f.getAirline() != null) ? f.getAirline().getName() : "";

        return new FlightDTO(
            f.getFlightId(),
            f.getOrigin(),
            f.getDestination(),
            f.getDate(),
            airlineName,
            f.getPrice()
        );
    }
}


