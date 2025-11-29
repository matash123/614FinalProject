package src.strategies;

import java.util.Comparator;
import java.util.List;
import src.models.Flight;

//sorting strategy that orders flights by price
public class SortByPriceStrategy implements SearchSortStrategy {
    @Override
    public List<Flight> sort(List<Flight> flights) {
        //sorting by price from lowest to highest
        if (flights == null) return List.of();
        flights.sort(Comparator.comparingDouble(Flight::getPrice));
        return flights;
    }
}

