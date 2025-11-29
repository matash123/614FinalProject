package src.strategies;

import java.util.List;
import src.models.Flight;

//interface for different sorting strategies
public interface SearchSortStrategy {
    List<Flight> sort(List<Flight> flights);
}

