package src.components;

import javax.swing.*;
import java.awt.FlowLayout;
import java.awt.Label;
import java.awt.TextField;
import java.awt.*;
import java.awt.event.ActionListener;

import src.database.userCRUD;

import src.entities.User;


public class LoginPanel extends JPanel{
    private JPanel panel;
    private Label usrNameLabel;
    private Label usrPassLabel;

    private TextField usrNameField;
    private TextField usrPassField;

    private JButton submitButton;
    

   
    public LoginPanel(){
        panel = new JPanel(new FlowLayout());
        usrNameLabel = new Label("Enter account user name");
        usrPassLabel = new Label("Enter account Password");

        usrNameField = new TextField(20);
        usrPassField = new TextField(20);


        submitButton = new JButton("Login!");

        panel.add(usrNameLabel);
        panel.add(usrNameField);

        panel.add(usrPassLabel);
        panel.add(usrPassField);

        ActionListener submitHandler = e -> handleSubmit(usrNameField.getText(), usrPassField.getText());
        submitButton.addActionListener(submitHandler);
        
        panel.add(submitButton);
        add(panel);
    }

    private void handleSubmit(String usrName, String usrPass){
        //function to handle login check 
        // what should we do here? This is the only place to login so could hard code login here, 
        //but take advantage of the boundary layer witht he database. 
        System.out.println("attempting to login to: " + usrName);
        User usr = userCRUD.getUser(usrName);
        if(usr  != null){
            if(usr.checkPassword(usrPass)){
                System.out.println("Login Success!");
            } else {
                System.out.println("invalid password");
                throw new RuntimeException("Invalid password");
            }
        } else {
            throw new RuntimeException("No user matches the username or some other error");
        }

    }

}
