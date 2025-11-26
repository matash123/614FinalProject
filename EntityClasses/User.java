

/**
 * Abstract base class for all system users. -> our 3 subclasses will inherit from them and implement their own specific functionality.
 * Should have basic members, that are consistent between all 3 subclasses to help ensure the most cose resuability and prevent code from being repeated.
 */
public abstract class User {

    // We want this line up with our SQL classes.
    private final String userId;
    private String name;
    private String email;
    private String passwordHash;   // store hashed passwords, not plain text, so this is just gonna be the length of the password.
    private boolean active; // stores if the class is active or not suspended ect, ect.

    protected User(String userId,
                   String name,
                   String email,
                   String passwordHash,
                   boolean active) {

        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("userId cannot be null or blank");
        }
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.active = active;
    }

    // This behaviour should just stay common.

    public void deactivate() {
        this.active = false;
    }

    public void activate() {
        this.active = true;
    }

    public boolean isActive() {
        return active;
    }

    //This is something we may have to remove if need be.
    public void changeEmail(String newEmail) {
        // could add validation here later
        this.email = newEmail;
    }

    public void changePasswordHash(String newHash) {
        this.passwordHash = newHash;
    }

    /** 
     * Each concrete subclass must say what role it represents.
     * This is a simple use of polymorphism.
     */
    public abstract UserRole getRole();

    // ---- Getters (no public setter for id) ----

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    protected String getPasswordHash() {
        // protected so controllers or auth services (in same package) can access if needed
        return passwordHash;
    }

    // optional: allow subclasses or same-package classes to adjust basic info
    public void setName(String name) {
        this.name = name;
    }

    // ---- Utility ----

    @Override
    public String toString() {
        return getRole() + "{" +
                "id='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", active=" + active +
                '}';
    }
}
