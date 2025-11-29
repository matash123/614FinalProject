package src.controllers;

import src.payment.PaymentGateway;
import src.database.RepositoryBridge;
import src.payment.SimulatedPaymentGateway;

public final class AppContext {
    private static AppContext INSTANCE;
    private final PaymentGateway paymentGateway;
    private final RepositoryBridge repositoryBridge;
    //todo add repositories once detected

    private AppContext() {
        //setting up the basic stuff we need
        this.paymentGateway = new SimulatedPaymentGateway();
        this.repositoryBridge = new RepositoryBridge();
        //todo init repo handles via RepositoryBridge
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

    public RepositoryBridge repository() {
        return repositoryBridge;
    }

    //todo add getters for other services
}

