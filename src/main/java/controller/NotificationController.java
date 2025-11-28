package controller;

import app.AppContext;
import controller.ControllerBus.EventType;

//handles notifications and promotions
public class NotificationController {
    private final RepositoryBridge repo;
    private long lastPromoSentTimestamp;

    public NotificationController(){
        this.repo=AppContext.getInstance().repository();
        this.lastPromoSentTimestamp=0;
    }

    public void sendMonthlyPromos(){
        //checking when we last sent promos
        long now=System.currentTimeMillis();
        long oneMonthAgo=now-(30L*24*60*60*1000);

        if(lastPromoSentTimestamp>oneMonthAgo){
            //already sent recently so skip
            return;
        }

        //figuring out which users are eligible
        //todo query repo for eligible customers
        //telling everyone promos were sent
        ControllerBus.getInstance().publish(EventType.PROMO_SENT,"monthly_promo");
        lastPromoSentTimestamp=now;
    }

    //todo channel delivery like email or sms and user eligibility logic
}

