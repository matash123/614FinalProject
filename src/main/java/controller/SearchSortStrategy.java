package controller;

import java.util.List;
import domain.Flight;

//interface for different sorting strategies
public interface SearchSortStrategy {
    List<Flight> sort(List<Flight> flights);
}

