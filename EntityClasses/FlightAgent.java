/*

Inherits from user for specifc of being a Flight Agent.

This class inherits from the mother class which ocme sin 

*/

package domain;

/**
 * Employee who can manage flights and assist customers.
 */
public class FlightAgent extends User {

    private String employeeId;
    private String workLocation;


    public FlightAgent(String agentId,
                       String name,
                       String email,
                       String passwordHash,
                       boolean active,
                       String employeeId,
                       String workLocation)

        super(agentId, name, email, passwordHash, active);
        this.employeeId = employeeId;
}

    public FlightAgent(String agentId,
                       String name,
                       String email) {
        this(agentId, name, email, null, true, null, null, null);
    }

    //To be accessed so that we can store it in our Database
    @Override
    public UserRole getRole() {
        return UserRole.FLIGHT_AGENT;
    }

    //Now setters and getters, standard so that they can be accessed 
    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getWorkLocation() {
        return workLocation;
    }

    public void setworkLocation(String officeLocation) {
        this.workLocation = workLocation;
    }
