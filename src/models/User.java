package src.models;

public class User {
    private String Id, name, email, role, password;

    public User(String userId, String name, String password, String role) {
        this.Id = userId;
        this.name = name;
        this.role = role;
        this.password = password;
    }

    public String getUserId() { return Id; }
    public String getName()   { return name; }
    public String getEmail()  { return email; }
    public String getRole()   { return role; }
    public boolean checkPassword(String guess) {
        boolean rslt;
        
        if(guess == this.password) {
            rslt = true; 
        } else {
            rslt = false;
            System.out.println("pass: " + password + " guess: " + guess);
        }
        
        return rslt;
    }
}
