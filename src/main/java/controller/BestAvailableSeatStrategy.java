package controller;

import java.util.ArrayList;
import java.util.List;

//strategy for selecting the best available seats
public class BestAvailableSeatStrategy implements SeatSelectionStrategy {
    @Override
    public List<String> selectSeats(int requestedSeats,List<String> availableSeats){
        //just returning the first N available seats for now
        if(availableSeats==null || availableSeats.size()<requestedSeats){
            throw new IllegalArgumentException("not enough seats available");
        }
        List<String> selected=new ArrayList<>();
        for(int i=0;i<requestedSeats;i++){
            selected.add(availableSeats.get(i));
        }
        return selected;
    }

    //todo add logic for best seats like window or aisle preferences
}

