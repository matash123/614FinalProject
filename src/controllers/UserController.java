package src.controllers;

import src.database.userCRUD;
import src.models.User;
import src.schemas.loginResult;

//handles user account management and self-service updates
public class UserController {
    public UserController(){};
    public User user;

    public User getCurrentUser() {
        //need to first reget user from db ffrom user id to update data
        user = userCRUD.getUserById(user.getUserId());
        if (user == null) {
            throw new IllegalArgumentException("user not found");
        }
        return user;
    }

    public loginResult attemptLogin(String username, String password) {
        System.out.println(username);
        User usr = userCRUD.getUser(username);

        if (usr == null)
            return new loginResult(false, "Unknown username", null);

        if (!usr.checkPassword(password))
            return new loginResult(false, "Invalid password", null);

        this.user = usr;
        return new loginResult(true, "", usr);
    }

    /**
     * Register a new customer user with the given username and password.
     * 
     * Reference to CHATGPT, as we modifed this again and needed help with consistent implementation
     * from our login and signup panel (which is called from login panel)
     */
    public loginResult registerCustomer(String username, String password) {
        try {
            User created = userCRUD.createCustomer(username, password);
            this.user = created;

            // No need for a message here – use empty string on success.
            return new loginResult(true, "", created);

        } catch (IllegalArgumentException ex) {
            // Validation / business-rule errors from userCRUD
            return new loginResult(false, ex.getMessage(), null);

        } catch (RuntimeException ex) {
            // More generic DB / system errors
            return new loginResult(false, "Could not create user: " + ex.getMessage(), null);
        }
    }


    //update user name with ownership check
    public boolean updateName(String currentUserId, String newName) {
        if (currentUserId == null || currentUserId.isBlank()) {
            throw new IllegalArgumentException("currentUserId is required");
        }
        if (newName == null || newName.isBlank()) {
            throw new IllegalArgumentException("newName is required");
        }
        //check ownership - user can only update their own account
        if (user == null || !user.getUserId().equals(currentUserId)) {
            throw new SecurityException("can only update own account");
        }

        boolean success = userCRUD.updateName(currentUserId, newName);
        if (success) {
            //reload user from db to get updated data
            User updated = userCRUD.getUserById(currentUserId);
            if (updated != null) {
                this.user = updated;
            }
        }
        return success;
    }

    //update user email with ownership check
    public boolean updateEmail(String currentUserId, String newEmail) {
        if (currentUserId == null || currentUserId.isBlank()) {
            throw new IllegalArgumentException("currentUserId is required");
        }
        if (newEmail == null || newEmail.isBlank()) {
            throw new IllegalArgumentException("newEmail is required");
        }
        //check ownership
        if (user == null || !user.getUserId().equals(currentUserId)) {
            throw new SecurityException("can only update own account");
        }

        boolean success = userCRUD.updateEmail(currentUserId, newEmail);
        if (success) {
            User updated = userCRUD.getUserById(currentUserId);
            if (updated != null) {
                this.user = updated;
            }
        }
        return success;
    }

    //update user password with ownership check
    public boolean updatePassword(String currentUserId, String oldPassword, String newPassword) {
        if (currentUserId == null || currentUserId.isBlank()) {
            throw new IllegalArgumentException("currentUserId is required");
        }
        if (oldPassword == null || oldPassword.isBlank()) {
            throw new IllegalArgumentException("oldPassword is required");
        }
        if (newPassword == null || newPassword.isBlank()) {
            throw new IllegalArgumentException("newPassword is required");
        }
        //check ownership and verify old password
        if (user == null || !user.getUserId().equals(currentUserId)) {
            throw new SecurityException("can only update own account");
        }
        if (!user.checkPassword(oldPassword)) {
            throw new SecurityException("old password incorrect");
        }

        boolean success = userCRUD.updatePassword(currentUserId, newPassword);
        if (success) {
            User updated = userCRUD.getUserById(currentUserId);
            if (updated != null) {
                this.user = updated;
            }
        }
        return success;
    }

    //toggle active status (only if business rules allow)
    public boolean toggleActive(String currentUserId) {
        if (currentUserId == null || currentUserId.isBlank()) {
            throw new IllegalArgumentException("currentUserId is required");
        }
        //check ownership
        if (user == null || !user.getUserId().equals(currentUserId)) {
            throw new SecurityException("can only update own account");
        }
        //for now allow users to deactivate themselves but not reactivate
        //todo add business rule check if needed
        boolean success = userCRUD.toggleActive(currentUserId, false);
        if (success) {
            User updated = userCRUD.getUserById(currentUserId);
            if (updated != null) {
                this.user = updated;
            }
        }
        return success;
    }

    /**
     * Clear the current logged-in user (used on logout).
     */
    public void clearCurrentUser() {
        this.user = null;
    }
}

