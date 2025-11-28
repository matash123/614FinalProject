package controller;

import java.math.BigDecimal;

//simulated payment gateway implementation
public class SimulatedPaymentGateway implements PaymentGateway {
    @Override
    public boolean authorize(String cardToken,BigDecimal amount){
        //just checking if the inputs are valid
        return amount.compareTo(BigDecimal.ZERO)>0 && cardToken!=null && !cardToken.isBlank();
    }

    @Override
    public boolean capture(String authId,BigDecimal amount){
        //simulating the capture step
        return authId!=null && !authId.isBlank() && amount.compareTo(BigDecimal.ZERO)>=0;
    }

    //todo refund void and getStatus
}

