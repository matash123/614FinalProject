package src.strategies;

import java.math.BigDecimal; //[2] "BigDecimal Class in Java," GeeksforGeeks. (2025). Retrieved November 29, 2025, from https://www.geeksforgeeks.org/java/bigdecimal-class-java/
import src.models.Flight;

public class PromoPricingDecorator implements PricingStrategy{
    private final PricingStrategy promo; //the base pricing strategy to wrap
    private final BigDecimal fprice; //discount percentage 

    //constructor
    public PromoPricingDecorator(PricingStrategy promo, BigDecimal promoPct){
        this.promo = promo;
        this.fprice = promoPct;
    }

    @Override
    public BigDecimal priceFor(Flight flight, int seats){
        BigDecimal base = promo.priceFor(flight, seats); //get base price from wrapped strategy
        BigDecimal discount = base.multiply(fprice); //calculate discount amount
        return base.subtract(discount); //return price minus discount
    }
}

