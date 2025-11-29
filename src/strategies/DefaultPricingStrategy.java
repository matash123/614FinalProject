package src.strategies;

import java.math.BigDecimal;
import src.models.Flight;

//default pricing strategy implementation
public class DefaultPricingStrategy implements PricingStrategy {
    @Override
    public BigDecimal priceFor(Flight flight, int seats) {
        //basic calculation is base price times number of seats
        //todo pull base from flight entity or repo
        if (flight == null) throw new IllegalArgumentException("flight cannot be null");
        BigDecimal base = BigDecimal.valueOf(flight.getPrice());
        return base.multiply(BigDecimal.valueOf(seats));
    }
}

