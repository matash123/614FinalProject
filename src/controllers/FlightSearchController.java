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

    public List<Flight> searchFlights(String origin, String destination, String date) {
        //doing a basic search
        if (origin == null || destination == null) {
            throw new IllegalArgumentException("origin and destination required");
        }

        List<Flight> results = repo.searchFlights(origin, destination, date);
        //applying the sort strategy
        results = sortStrategy.sort(results);

        //telling everyone flights were loaded
        ControllerBus.getInstance().publish(ControllerBus.EventType.FLIGHTS_LOADED, results);
        return results;
    }

    //todo filters for airline time range and price range
}

