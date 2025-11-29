package src.models;

/**
 * Airport represents a departure/arrival location for flights.
 * Which after putting in Airport in the Flight entity we will access this directly through getter methods to store city of departure and destination for flighys
 * Will help keep things consistent and ease input for our Flight Agent and prevent invalid entries as a check
 */

public class Airport {

    // Airpory code just like we in real life for example Calgary is "YYC"
    private final String code;


    private String name;      // "Calgary International Airport"
    private String city;      // "Calgary"
    private String country;   // "Canada"

    public Airport(String code,
                   String name,
                   String city,
                   String country) {

        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Airport code cannot be null or blank");
        }

        this.code = code;
        this.name = name;
        this.city = city;
        this.country = country;
    }

    // Convenience constructor if you only know code, which in most cases is good enough
    public Airport(String code) {
        // When only the code is known, other fields can be set later
        this(code, null, null, null);
    }

    // Our default getters so we can aces

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    //Full display of our system

    public String getDisplayName() {
        // e.g., "YYC - Calgary, Canada"
        StringBuilder sb = new StringBuilder(code);
        if (city != null && !city.isBlank()) {
            sb.append(" - ").append(city);
        }
        if (country != null && !country.isBlank()) {
            sb.append(", ").append(country);
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return "Airport{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", city='" + city + '\'' +
                ", country='" + country + '\'' +
                '}';
    }
}
