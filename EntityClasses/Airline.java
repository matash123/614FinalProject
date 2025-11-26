
/**
 * Airline, this class hold any neccesary Airline information, whats going to be key is that the way we conceptualized
 * This project is that each Airplane is owned by an airline (this makes sense when it comes to branding ect), so this key
 * "Airline ID", is a foreign key for an Airplane and because of our getters this allows access through and through.
 */
public class Airline {

    private final String airlineId; // This is going to be an abbreviation that cannot be changed (shoutout to ChatGPT for the Final idea, this is good and needed)
    private String name; // Name of the Airline

    public Airline(String airlineId, String name) {
        if (airlineId == null || airlineId.isBlank()) {
            throw new IllegalArgumentException("airlineId cannot be null or blank");
        }
        this.airlineId = airlineId;
        this.name = name;
    }

    /**
     * Now that constructor is done time to set our getters for all important methods
     * Very straightforward
     */

    public String getAirlineId() {
        return airlineId;
    }

    public String getName() {
        return name;
    }

    /**
     * I cant think of a reason we would need this in the scope of the class, but in case an Airline is bought would be good to change
     * the name.
     * 
    */
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Airline{" +
                "airlineId='" + airlineId + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
