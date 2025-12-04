import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.*;



class Main {
    static User user;
    static Scanner input = new Scanner(System.in);
    public static void main(String[] args){

        login();

        // If user is not logged in, terminate
        if (!user.isLogged){
            input.close();
            return;
        }

        if (user.isEmployer){
            String choice = "";
            do{
                System.out.print("\nPick One Option\n1. Register New Employee \n2. Logout \nYour choice: ");
            choice = input.next();
           
            if (choice.equals("1")){
                register();
            }
            else if (choice.equals("2")){
                user.logout();
                System.out.println("You have succcessfully logged out");
                
            } else {
                System.out.println("Invalid choice.");
            }
            } while (!"12".contains(choice) || choice.length() != 1);
            
        } else // User is employee 
        {
            String choice = "";
            do{
                System.out.print("\nPick One Option\n1. Logout \nYour choice: ");
            choice = input.next();
           
            if (choice.equals("1")){
                user.logout();
                System.out.println("You have succcessfully logged out");
            }
            else {
                System.out.println("Invalid choice.");
            }
            } while (!"1".contains(choice) || choice.length() != 1);
        }

        input.close();
    }

    public static void login(){

          // Read file data from employee.csv
        String employeeDataPath = "C:\\Users\\MSI GF63 THIN 8RCS\\Documents\\FOP Khy\\Store-Management-System-7FOP\\employee.csv";
        List<List<String>> employeeData = methods.readCsvFile(employeeDataPath);
    

        // removing the first row of employee data (the column name)
        employeeData.remove(0);
        
        // Employee data example:
        //  C6002 | Adam bin Abu | Full-time | d3e4f5 
        // Employee ID | Employee Name | Role | Password

        // Print out the data (test)
        // for (int i = 0; i < employeeData.size(); i++){
        //     for (int j = 0; j < employeeData.get(0).size(); j++) {
        //         System.out.print(employeeData.get(i).get(j) + " | ");}
        //     System.out.println(); }

        // User input
        System.out.println("=== Employee Login ===");
        System.out.print("Enter User ID: ");
        String userId = input.next();
        System.out.print("Enter Password: ");
        String userPassword = input.next();
        String userRole = "";
        String userName = "";

        // Check if user input and user password is valid
        boolean userValid = false;
        for (int i = 0; i < employeeData.size(); i++) {
            String employeeId = employeeData.get(i).get(0);
            String employeePassword = employeeData.get(i).get(3);
    
            if (userId.equals(employeeId) && userPassword.equals(employeePassword)){
                userValid = true;
                userName = employeeData.get(i).get(1);
                userRole = employeeData.get(i).get(2);
            }
        }

        if (userValid){
            System.out.println("\nLogin Successful!");
            System.out.println("Welcome, " + userName + " (" + userId.substring(0,3) + ")");

            // Log in to the system
            user = new User(userId, userName, userRole, userPassword);
            user.login();
            
        } else {
            System.out.println("\nLogin Failed : Invalid User ID or Password");
        }

      
    }

    public static void register(){
        System.out.println("=== Register New Employee ===");
        System.out.print("Enter Employee Name: ");
        String employeeName = input.next();
        System.out.print("Enter Employee ID: ");
        String employeeId = input.next();
        System.out.print("Set Password: ");
        String employeePassword = input.next();
        System.out.print("Set Role: ");
        String employeeRole = input.next();

        String[] newEmployeeData = {employeeId, employeeName, employeeRole, employeePassword};

        //Enter a new employee into the employee.csv file
        String path = "C:\\Users\\MSI GF63 THIN 8RCS\\Documents\\FOP Khy\\Store-Management-System-7FOP\\employee.csv";
        try(PrintWriter writer = new PrintWriter(new FileWriter(path, true))){
            writer.println(String.join(",", newEmployeeData));
            
            System.out.println("\nEmployee Successfully Registered! ");
        } catch (Exception e) {
            System.err.println("Error writing to CSV file: " + e.getMessage());
        }

       
    }
}