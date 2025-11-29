package src.strategies;

import java.util.List;
import src.models.Flight;

//sorting strategy that orders flights by duration
public class SortByDurationStrategy implements SearchSortStrategy {
    @Override
    public List<Flight> sort(List<Flight> flights) {
        //todo use real duration getter from flight
        //for now just returning as is since duration field may not exist
        if (flights == null) return List.of();
        //todo flights.sort(Comparator.comparing(Flight::getDuration));
        return flights;
    }
}

