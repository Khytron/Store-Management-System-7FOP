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

import javax.swing.JOptionPane;

// Manages user authentication (login/logout) and registration of new employees.
// Implements the Singleton pattern to ensure a single instance manages the session.
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
        return loggedInUser != null && loggedInUser.isLogged;
    }


    public User attemptLogin(Scanner input){

        // User input
        String userRole = "";
        String userName = "";


        String userId = Methods.showInputDialog("Employee Login" +
                "\nEnter User ID:", "Login");
        String userPassword = Methods.showInputDialog("Enter password:", "Login");

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
                break;
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
                break;
            }
        }

        if (userValid){
            // Logging in


            User newUser = new User(userId, userName, userRole, userPassword);
            newUser.greetUser();
            setLoggedInUser(newUser);
            return newUser;

            
        } else {
            JOptionPane.showMessageDialog(null,"Login Failed : Invalid User ID or Password", "Login Failed", JOptionPane.WARNING_MESSAGE);
            return null;
        }

      
    }

    public void registerNewEmployee(Scanner input){
        String employeeId = Methods.showInputDialog("Enter Employee ID: ");
        String employeeName = Methods.showInputDialog("Enter Employee Name: ");
        String employeeRole = Methods.showInputDialog("Set Role: ");
        String employeePassword = Methods.showInputDialog("Set Password: ");
        
        String[] newEmployeeData = {employeeId, employeeName, employeeRole, employeePassword};

        //Enter a new employee into the employee.csv file
        try(PrintWriter writer = new PrintWriter(new FileWriter(FilePath.employeeDataPath, true))){
            writer.println(String.join(",", newEmployeeData));
            
            JOptionPane.showMessageDialog(null, "Successfully Registered!", null, JOptionPane.INFORMATION_MESSAGE);
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
