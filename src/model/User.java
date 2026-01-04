package model;

import util.Methods;

public class User {
    //Attributes
    private String userId;
    private String userName;
    private String userRole;
    private String userPassword;
    public boolean isEmployee = false;
    public boolean isEmployer = false;
    public boolean isLogged = false;
    

    public User(String userId, String userName, String userRole, String userPassword){
        this.userId = userId;
        this.userName = userName; 
        this.userRole = userRole;
        this.userPassword = userPassword;
    }

    public void login(){
        if (!this.isLogged && this.userId != null) { 
            this.isLogged = true; 
            // Check if its an employee or employer
            if (Methods.isEmployerRole(this.userRole)) {
                this.isEmployer = true;
            } else {
                this.isEmployee = true;
            }
        }
    }

    public void logout(){
        if (this.isLogged) { 
            this.isLogged = false; 
            this.isEmployee = false;
            this.isEmployer = false; 
        }
    }

    public void greetUser(){
        System.out.println("Welcome, \u001B[32m" + this.userName + "\u001B[0m");
    }

    public String getUserId(){
        return this.userId;
    }
    public String getUserName(){
        return this.userName;
    }
    public String getUserRole(){
        return this.userRole;
    }
    public String getUserPassword(){
        return this.userPassword;
    }
}
