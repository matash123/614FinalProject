package src.strategies;

import java.math.BigDecimal;
import src.models.Flight;

//interface for different pricing strategies
public interface PricingStrategy {
    BigDecimal priceFor(Flight flight, int seats);
}

