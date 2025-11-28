package controller;

import java.math.BigDecimal;
import domain.Flight;

//interface for different pricing strategies
public interface PricingStrategy {
    BigDecimal priceFor(Flight flight,int seats);
}

