package controller;

import app.AppContext;
import domain.User;
import controller.ControllerBus.EventType;
import controller.RepositoryBridge;

//handles authentication logic
public class AuthController {
    private final RepositoryBridge repo;
    private User currentUser;

    public AuthController(){
        this.repo=AppContext.getInstance().repository();
        this.currentUser=null;
    }

    public User login(String email,String password){
        //checking the inputs first
        if(email==null || email.isBlank()){
            throw new IllegalArgumentException("email cannot be blank");
        }
        if(password==null || password.isBlank()){
            throw new IllegalArgumentException("password cannot be blank");
        }

        //asking the repo to find the user
        User user=repo.findUserByEmail(email);
        if(user==null){
            throw new ControllerExceptions.ControllerException(
                ControllerExceptions.ErrorCode.INVALID_CREDENTIALS,
                "user not found"
            );
        }

        //todo proper password hash check
        //for now assuming password check happens in repo or user entity
        //todo password policy checks
        if(!user.isActive()){
            throw new ControllerExceptions.ControllerException(
                ControllerExceptions.ErrorCode.UNAUTHORIZED_ACCESS,
                "user account is inactive"
            );
        }

        this.currentUser=user;
        //telling everyone the user logged in
        ControllerBus.getInstance().publish(EventType.USER_LOGGED_IN,user);
        return user;
    }

    public void logout(){
        //clearing the session
        if(currentUser!=null){
            ControllerBus.getInstance().publish(EventType.USER_LOGGED_OUT,currentUser);
            currentUser=null;
        }
        //todo clear session in AppContext if session tracking exists
    }

    public User getCurrentUser(){
        return currentUser;
    }

    //todo password policy checks and session management
}

