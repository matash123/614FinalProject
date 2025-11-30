package src.controllers;

import java.util.List;

import app.AppContext;
import src.database.RepositoryBridge;
import src.events.ControllerBus;
import src.strategies.SearchSortStrategy;
import src.strategies.SortByPriceStrategy;
import src.models.Flight;

//handles flight search logic
public class FlightSearchController {
    private final RepositoryBridge repo;
    private SearchSortStrategy sortStrategy;

    public FlightSearchController() {
        this.repo = AppContext.getInstance().repository();
        this.sortStrategy = new SortByPriceStrategy();
    }

    public void setSortStrategy(SearchSortStrategy s) {
        this.sortStrategy = s;
    }

    /**
     * Searches for flights matching origin, destination, and optional date.
     * Results are sorted using the configured SearchSortStrategy.
     * Publishes FLIGHTS_LOADED event with results.
     */
    public List<Flight> searchFlights(String origin, String destination, String date) {
        //validate inputs
        if (origin == null || destination == null) {
            throw new IllegalArgumentException("origin and destination required");
        }

        //query repository for flights
        List<Flight> results = repo.searchFlights(origin, destination, date);
        //apply sort strategy
        results = sortStrategy.sort(results);

        //publish flights loaded event
        ControllerBus.getInstance().publish(ControllerBus.EventType.FLIGHTS_LOADED, results);
        return results;
    }

    //todo filters for airline time range and price range
}

