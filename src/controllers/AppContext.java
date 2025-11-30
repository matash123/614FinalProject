package src.controllers;

import src.payment.PaymentGateway;
import src.payment.SimulatedPaymentGateway;

public final class AppContext {
    private static AppContext INSTANCE;
    private final PaymentGateway paymentGateway;

    private AppContext() {
        //setting up the basic stuff we need
        this.paymentGateway = new SimulatedPaymentGateway();
    }

    public static synchronized AppContext getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AppContext();
        }
        return INSTANCE;
    }

    public PaymentGateway paymentGateway() {
        return paymentGateway;
    }

    //todo add getters for other services
}

