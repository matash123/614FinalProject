package src.models;

/**
 * Customer – extends the core {@link User} model with customer-specific fields.
 */
public class Customer extends User {

    private String customerRewardsNumber; // in case we want to apply rewards
    private String address;               // billing / contact address
    private String phoneNumber;
    private String creditCardNumber;      // optional stored card reference

    public Customer(String customerId,
                    String name,
                    String password,
                    String customerRewardsNumber,
                    String address,
                    String phoneNumber) {

        // store role as simple string for now to match existing User model
        super(customerId, name, password, "CUSTOMER");
        this.customerRewardsNumber = customerRewardsNumber;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }

    // Convenience constructor if we only know id, name and password for now
    public Customer(String customerId,
                    String name,
                    String password) {
        this(customerId, name, password, null, null, null);
    }

    // Additional getters and setters for all these fields.

    public String getCustomerRewardsNumber() {
        return customerRewardsNumber;
    }

    public void setCustomerRewardsNumber(String customerRewardsNumber) {
        this.customerRewardsNumber = customerRewardsNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCreditCardNumber() {
        return creditCardNumber;
    }

    public void setCreditCardNumber(String creditCardNumber) {
        this.creditCardNumber = creditCardNumber;
    }

    // To check if they have a customer rewards number so they can be prompted to set one up
    public boolean hasCustomerRewardsNumber() {
        return customerRewardsNumber != null && !customerRewardsNumber.isBlank();
    }
}
