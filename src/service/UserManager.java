package service;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import model.Employee;
import model.Employer;
import model.User;
import util.FilePath;
import util.Methods;

public class UserManager {
    private User loggedInUser;

    private static UserManager instance;

    private List<Employee> employeeList = new ArrayList<>();
    private List<Employer> employerList = new ArrayList<>();



    private UserManager(){
        loadAllEmployeeData();
    }

    public static UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }

    private void loadAllEmployeeData(){
        // Read file data from employee.csv
        List<List<String>> employeeData = Methods.readCsvFile(FilePath.employeeDataPath);

        // Removing the first row of employee data (the column name)
        if (!employeeData.isEmpty())
            employeeData.remove(0);

        // Clearing the employee and employer list
        employeeList.clear();
        employerList.clear();

        for (List<String> employee : employeeData) {
            // Getting data
            String employeeId = employee.get(0);
            String employeeName = employee.get(1);
            String employeeRole = employee.get(2);
            String employeePassword = employee.get(3);

            // Check if its an employee or employer
            if (Employer.employerIds.contains(employeeId)){
                // Its an employer
                Employer newEmployer = new Employer(employeeId, employeeName, employeeRole, employeePassword);
                // Add it to employer list
                employerList.add(newEmployer);
            } else {
                // Its an employee
                Employee newEmployee = new Employee(employeeId, employeeName, employeeRole, employeePassword);
                // Add it to employee list
                employeeList.add(newEmployee);
            }
        }
    }

    public void setLoggedInUser(User user) {
        user.login();
        this.loggedInUser = user;
    }

    public User getLoggedInUser() {
        return this.loggedInUser;
    }

    public boolean isLoggedIn() {
        return loggedInUser != null &&loggedInUser.isLogged;
    }


    public User attemptLogin(Scanner input){
        // Employee data example:
        //  C6002 | Adam bin Abu | Full-time | d3e4f5 
        // Employee ID | Employee Name | Role | Password

        // Print out the data (test)
        // for (int i = 0; i < employeeList.size(); i++){
        //     System.out.print(employeeList.get(i).getEmployeeId() + " | ");
        //     System.out.print(employeeList.get(i).getEmployeeName() + " | ");
        //     System.out.print(employeeList.get(i).getEmployeeRole() + " | ");
        //     System.out.print(employeeList.get(i).getEmployeePassword() + " | ");
        //     System.out.println(); }

        // User input
        System.out.println("Type 'exit' to exit the program.\n");
        System.out.println("=== Employee Login ===");
        
        System.out.print("Enter User ID: ");
        String userId = input.next();
        System.out.print("Enter Password: ");
        String userPassword = input.next();
        String userRole = "";
        String userName = "";

        // If user wants to exit the program
        if (userId.equals("exit") || userPassword.equals("exit"))
            return new User("exit", "exit", "exit", "exit");

        boolean userValid = false;

        // Check if user input and user password is valid for an employee
        for (int i = 0; i < employeeList.size(); i++) {
            String employeeId = employeeList.get(i).getEmployeeId();
            String employeePassword = employeeList.get(i).getEmployeePassword();
    
            if (userId.equals(employeeId) && userPassword.equals(employeePassword)){
                userValid = true;
                userName = employeeList.get(i).getEmployeeName();
                userRole = employeeList.get(i).getEmployeeRole();
            }
        }

        // Check if user input and user password is valid for an employer
        for (int i = 0; i < employerList.size(); i++) {
            String employerId = employerList.get(i).getEmployerId();
            String employerPassword = employerList.get(i).getEmployerPassword();
    
            if (userId.equals(employerId) && userPassword.equals(employerPassword)){
                userValid = true;
                userName = employerList.get(i).getEmployerName();
                userRole = employerList.get(i).getEmployerRole();
            }
        }

        if (userValid){
            // Logging in
            System.out.println("\nLogin Successful!");

            User newUser = new User(userId, userName, userRole, userPassword);
            newUser.greetUser();
            setLoggedInUser(newUser);
            return newUser;

            
        } else {
            System.out.println("\nLogin Failed : Invalid User ID or Password\n");
            return null;
        }

      
    }

    public void registerNewEmployee(Scanner input){
        System.out.println("=== Register New Employee ===");
        System.out.print("Enter Employee Name: ");
        String employeeName = input.nextLine();
        System.out.print("Enter Employee ID: ");
        String employeeId = input.next();
        System.out.print("Set Password: ");
        String employeePassword = input.next();
        System.out.print("Set Role: ");
        String employeeRole = input.next();

        String[] newEmployeeData = {employeeId, employeeName, employeeRole, employeePassword};

        //Enter a new employee into the employee.csv file
        try(PrintWriter writer = new PrintWriter(new FileWriter(FilePath.employeeDataPath, true))){
            writer.println(String.join(",", newEmployeeData));
            
            System.out.println("\nEmployee Successfully Registered! ");
        } catch (Exception e) {
            System.err.println("Error writing to CSV file: " + e.getMessage());
        }
    }

    public void attemptLogOut(){
        if (loggedInUser == null) 
            return;
        
        loggedInUser.logout();
        loggedInUser = null;
        
    }

}
