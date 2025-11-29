package src.events;

//simple observable interface
public interface Observable {
    void subscribe(Observer observer);
    void unsubscribe(Observer observer);
    void notifyObservers(Object event);
}

