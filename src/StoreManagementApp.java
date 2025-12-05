import java.util.*;

import model.User;
import service.UserManager;



class StoreManagementApp {
 
    static Scanner input = new Scanner(System.in);
    
    public static void main(String[] args){

        UserManager userManager = UserManager.getInstance();

        System.out.println("=== Store Management Operation System ===");
        System.out.println("============== By Group 7 ===============\n");
       
        User loggedInUser = null;

        // The Employee Login Loop (if user logout, go back here)
        while (loggedInUser == null){
            while (true) {
                loggedInUser = userManager.attemptLogin(input);

                
                // If login successful
                if (loggedInUser != null) {
                    // If user tries to exit the program
                    if (loggedInUser.getUserName().equals("exit")){
                        // Exiting the program
                        System.out.println("\nTerminating the program...\n");
                        input.close();
                        return;
                    }
                    break;
                }
                
            }
            

            if (loggedInUser.isEmployer){
                String choice = "";
                while (true) {
                    System.out.print("\nPick One Option\n1. Register New Employee \n2. Logout \nYour choice: ");
                    choice = input.next();
                    
                    // To fix a small error where if u do input.nextLine() it reads \n immediately
                    input.nextLine();

                    if (choice.equals("1")){
                        userManager.registerNewEmployee(input);
                        break;
                    }
                    else if (choice.equals("2")){
                        userManager.attemptLogOut();
                        loggedInUser.logout();
                        System.out.println("\nYou have succcessfully logged out\n");
                        loggedInUser = null;
                        break;
                    } else {
                        System.out.println("\nInvalid choice.");
                    }
                }
                
            } else // User is employee 
            {
                String choice = "";
                while (true){
                    System.out.print("\nPick One Option\n1. Logout \nYour choice: ");
                    choice = input.next();

                    // To fix a small error where if u do input.nextLine() it reads \n immediately
                    input.nextLine();
                
                    if (choice.equals("1")){
                        userManager.attemptLogOut();
                        loggedInUser.logout();
                        loggedInUser = null;
                        System.out.println("\nYou have succcessfully logged out\n");
                        
                        break;
                    }
                    else {
                        System.out.println("Invalid choice.");
                    }

                } 
            }
        }
        

        input.close();
    }



    

}