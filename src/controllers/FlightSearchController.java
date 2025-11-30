package src.controllers;

import java.util.List;
import src.database.FlightCrud;
import src.events.ControllerBus;
import src.models.Flight;
import src.strategies.SearchSortStrategy;
import src.strategies.SortByPriceStrategy;

//handles flight search logic
public class FlightSearchController {
    private SearchSortStrategy sortStrategy;

    public FlightSearchController() {
        this.sortStrategy = new SortByPriceStrategy();
    }

    public void setSortStrategy(SearchSortStrategy s) {
        this.sortStrategy = s;
    }

    public List<Flight> searchFlights(String origin, String destination, String date, String id) {
        // Allow any combination of origin, destination, and date filters.
        // Nulls are treated the same as empty strings; the CRUD layer decides
        // which filters to apply based on blank checks.
        String safeOrigin = (origin != null) ? origin : "";
        String safeDestination = (destination != null) ? destination : "";

        List<Flight> results = FlightCrud.searchFlights(safeOrigin, safeDestination, date, id);
        //applying the sort strategy
        results = sortStrategy.sort(results);

        ControllerBus.getInstance().publish(
            ControllerBus.EventType.FLIGHTS_LOADED,
            results
        );
        return results;
    }

    //todo filters for airline time range and price range
}

