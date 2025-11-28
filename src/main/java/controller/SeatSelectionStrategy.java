package controller;

import java.util.List;

//interface for different seat selection strategies
public interface SeatSelectionStrategy {
    List<String> selectSeats(int requestedSeats,List<String> availableSeats);
}

