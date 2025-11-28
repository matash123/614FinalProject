package controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//observer hub for gui updates
public class ControllerBus {
    private static ControllerBus INSTANCE;
    private final Map<EventType,List<Observer>> subscribers;

    public enum EventType {
        USER_LOGGED_IN,
        USER_LOGGED_OUT,
        RESERVATION_CREATED,
        RESERVATION_CANCELLED,
        PAYMENT_SUCCEEDED,
        PAYMENT_REFUNDED,
        FLIGHTS_LOADED,
        PROMO_SENT
    }

    private ControllerBus(){
        this.subscribers=new HashMap<>();
        //setting up all the event types
        for(EventType type:EventType.values()){
            subscribers.put(type,new ArrayList<>());
        }
    }

    public static synchronized ControllerBus getInstance(){
        if(INSTANCE==null){
            INSTANCE=new ControllerBus();
        }
        return INSTANCE;
    }

    public void subscribe(EventType type,Observer observer){
        //adding a new subscriber
        if(observer!=null){
            subscribers.get(type).add(observer);
        }
    }

    public void unsubscribe(EventType type,Observer observer){
        //removing a subscriber
        subscribers.get(type).remove(observer);
    }

    public void publish(EventType type,Object event){
        //telling everyone about this event
        for(Observer obs:subscribers.get(type)){
            obs.update(event);
        }
    }

    //todo add thread safety and error handling
}

