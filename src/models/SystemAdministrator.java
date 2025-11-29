package src.models;

/**
 * System administrator – highest-privilege user in the system.
 */
public class SystemAdministrator extends User {

    public SystemAdministrator(String adminId,
                               String name,
                               String password) {
        super(adminId, name, password, "ADMIN");
    }

    // Convenience constructor with no password yet
    public SystemAdministrator(String adminId,
                               String name) {
        this(adminId, name, null);
    }
}


