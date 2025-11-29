package src.controllers;

import java.util.List;
import java.util.stream.Collectors;

import app.AppContext;
import src.DTO.FlightDTO;
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

        // Convert to DTOs for UI consumers and publish on the bus.
        List<FlightDTO> dtoResults = results.stream()
            .map(FlightDTO::fromModel)
            .collect(Collectors.toList());

        ControllerBus.getInstance().publish(
            ControllerBus.EventType.FLIGHTS_LOADED,
            dtoResults
        );

        return results;
    }

    //todo filters for airline time range and price range
}

