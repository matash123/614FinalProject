package src.controllers;

import src.events.ControllerBus;
import src.events.ControllerBus.EventType;

//handles notifications and promotions
public class NotificationController {
    private long lastPromoSentTimestamp;

    public NotificationController() {
        this.lastPromoSentTimestamp = 0;
    }

    public void sendMonthlyPromos() {
        //checking when we last sent promos
        long now = System.currentTimeMillis();
        long oneMonthAgo = now - (30L * 24 * 60 * 60 * 1000);

        if (lastPromoSentTimestamp > oneMonthAgo) {
            //already sent recently so skip
            return;
        }

        //figuring out which users are eligible
        //todo query DB for eligible customers
        //telling everyone promos were sent
        ControllerBus.getInstance().publish(EventType.PROMO_SENT, "monthly_promo");
        lastPromoSentTimestamp = now;
    }
}

