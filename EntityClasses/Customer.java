package domain;

/**
 * Customer this is the quintessential entity of our entire system, an extends user so that we can the functionality needed
 * for the customer who in our interface is the one that can make a reseravation depicted through the FlightCustomerReservation
 * 
 */

//This of course extends user so that we have all the user field plus new functionality and members needed.
public class Customer extends User {

    private String customerRewardsNumber; //this in case we want to apply rewards
    private String address; //customer address to be stored when booking the flight could be useful for payment information so that it is stored during billing and can be loaded
    private String phoneNumber; 
    private int creditCardNumer; // this can be null and if so we will do a check if there is a credit card on file and if not they can add one in there, we will figure it out
    // it can also have a prompt like credit card on file and if not Cheque, Paypal ect -> nat be too much.

    public Customer(String customerId,
                    String name,
                    String email,
                    String passwordHash,
                    boolean active,
                    String loyaltyNumber,
                    String address,
                    String phoneNumber) {

        super(customerId, name, email, passwordHash, active); //calling super to create the parent portion
        this.loyaltyNumber = loyaltyNumber;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }

    // Convenience constructor if we also want to just pass the use that exist into CUSTOMER (not sure about design yet)
    public Customer(String customerId,
                    String name,
                    String email) {
        this(customerId, name, email, null, true, null, null, null);
    }

    //way to store the role in our Database
    @Override
    public UserRole getRole() {
        return UserRole.CUSTOMER;
    }

    // Additional getter and a feww setters for all thses fields.

    public String getCustomerRewardsNumber() {
        return customerRewardsNumber;
    }

    public void setcustomerRewardsNumber(String customerRewardsNumber) {
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

    // To check if they have a customer rewards number so they can be prompted ot set up
    public boolean hascustomerRewardsNumber() {
        return customerRewardsNumber!= null && !customerRewardsNumber.isBlank();
    }
}
