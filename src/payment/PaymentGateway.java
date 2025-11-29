package src.payment;

import java.math.BigDecimal;

//interface for payment processing
public interface PaymentGateway {
    boolean authorize(String cardToken, BigDecimal amount);
    boolean capture(String authId, BigDecimal amount);
    //todo refund void and getStatus
}

