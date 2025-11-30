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

    //setters for self-service updates
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }

    public boolean checkPassword(String guess) {
        boolean rslt;
        
        if(guess.equals(this.password)) {
            rslt = true; 
        } else {
            rslt = false;
            System.out.println("pass: " + password + " guess: " + guess);
        }
        
        return rslt;
    }
}
