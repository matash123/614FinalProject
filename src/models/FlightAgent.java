/*
Inherits from User for specifics of being a Flight Agent.
*/
package src.models;

/**
 * Employee who can manage flights and assist customers.
 */
public class FlightAgent extends User {

    private String employeeId;
    private String workLocation;

    public FlightAgent(String agentId,
                       String name,
                       String password,
                       String employeeId,
                       String workLocation) {
        super(agentId, name, password, "AGENT");
        this.employeeId = employeeId;
        this.workLocation = workLocation;
    }

    public FlightAgent(String agentId,
                       String name,
                       String password) {
        this(agentId, name, password, null, null);
    }

    // Standard getters and setters
    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getWorkLocation() {
        return workLocation;
    }

    public void setWorkLocation(String workLocation) {
        this.workLocation = workLocation;
    }
}


