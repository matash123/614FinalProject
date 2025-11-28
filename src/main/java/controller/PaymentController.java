package controller;

import controller.ControllerBus.EventType;
import java.math.BigDecimal;

//handles payment operations
public class PaymentController {
    private final PaymentGateway gateway;

    public PaymentController(PaymentGateway gateway){
        this.gateway=gateway;
    }

    public boolean authorizeAndCapture(String cardToken,BigDecimal amount){
        //delegating to the gateway
        if(cardToken==null || amount==null || amount.compareTo(BigDecimal.ZERO)<=0){
            throw new IllegalArgumentException("invalid payment parameters");
        }

        boolean authOk=gateway.authorize(cardToken,amount);
        if(!authOk){
            return false;
        }

        String authId="auth_"+System.currentTimeMillis();
        boolean captureOk=gateway.capture(authId,amount);
        if(captureOk){
            ControllerBus.getInstance().publish(EventType.PAYMENT_SUCCEEDED,amount);
        }
        return captureOk;
    }

    public boolean refund(String reservationId){
        //calling the gateway for refund
        //todo implement refund flow
        //todo publish PaymentRefunded event
        ControllerBus.getInstance().publish(EventType.PAYMENT_REFUNDED,reservationId);
        return true;
    }

    //todo refund flow implementation
}

