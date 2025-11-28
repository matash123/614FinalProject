package domain;

/**
 * Class System Administrator
 * This inherits from User, and respresents one of the 3 child classes -> System Administrator,
 * This is the class with the most access and functionality
 */
public class SystemAdministrator extends User {


    public SystemAdministrator(String adminId,
                               String name,
                               String email,
                               String passwordHash,
                               boolean active) {

        super(adminId, name, email, passwordHash, active);
    }



    public SystemAdministrator(String adminId,
                               String name,
                               String email) {
        this(adminId, name, email, null, true);
    }

    @Override
    public UserRole getRole() {
        return UserRole.SYSTEM_ADMIN;
    }

