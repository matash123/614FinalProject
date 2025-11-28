package controller;

import java.math.BigDecimal;
import domain.Flight;

//decorator for applying promo discounts to pricing
public class PromoPricingDecorator implements PricingStrategy {
    private final PricingStrategy inner;
    private final BigDecimal promoPct;

    public PromoPricingDecorator(PricingStrategy inner,BigDecimal promoPct){
        this.inner=inner;
        this.promoPct=promoPct;
    }

    @Override
    public BigDecimal priceFor(Flight flight,int seats){
        //applying the promo discount on top of the base price
        BigDecimal base=inner.priceFor(flight,seats);
        BigDecimal discount=base.multiply(promoPct);
        return base.subtract(discount);
    }

    //todo handle multi stacked promos and edge cases
}

