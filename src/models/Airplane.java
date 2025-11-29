package src.models;

/**
 * Airplane represents an aircraft model/instance used for flights.
 * This will be a one to one relationship with each flight and has an airplane
 * Also many to one for the relationship with Airline, who will ultimately own 
 *
 */
public class Airplane {

    // Unique ID inside your system. Could be a tail number or internal code.
    private final String airplaneId;

    // Descriptive fields
    private String model;         // e.g., "Boeing 737-800"
    private String manufacturer;  // e.g., "Boeing", "Airbus"
    private int capacity;         // total number of seats available

    public Airplane(String airplaneId,
                    String model,
                    String manufacturer,
                    int capacity) {

        if (airplaneId == null || airplaneId.isBlank()) {
            throw new IllegalArgumentException("airplaneId cannot be null or blank");
        }
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity must be positive");
        }

        this.airplaneId = airplaneId;
        this.model = model;
        this.manufacturer = manufacturer;
        this.capacity = capacity;
    }

    // Convenience constructor if you don't care about manufacturer yet
    public Airplane(String airplaneId, String model, int capacity) {
        this(airplaneId, model, null, capacity);
    }

    // ---- Getters / setters ----

    public String getAirplaneId() {
        return airplaneId;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    /**
     * Total seating capacity of this airplane.
     */
    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity must be positive");
        }
        this.capacity = capacity;
    }

    @Override
    public String toString() {
        return "Airplane{" +
                "airplaneId='" + airplaneId + '\'' +
                ", model='" + model + '\'' +
                ", manufacturer='" + manufacturer + '\'' +
                ", capacity=" + capacity +
                '}';
    }
}
