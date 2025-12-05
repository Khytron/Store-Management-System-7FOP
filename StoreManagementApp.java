import java.util.*;



class StoreManagementApp {
 
    static Scanner input = new Scanner(System.in);
    
    public static void main(String[] args){

        UserManager userManager = UserManager.getInstance();

        //Attempting login
        User loggedInUser = null;
        while (true) {
            loggedInUser = userManager.attemptLogin(input);

            
            // If login successful
            if (loggedInUser != null) {
                // If user tries to exit the program
                if (loggedInUser.getUserName().equals("exit")){
                    // Exiting the program
                    System.out.println("Terminating the program..");
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
                    System.out.println("You have succcessfully logged out");
                    loggedInUser = null;
                    break;
                } else {
                    System.out.println("Invalid choice.");
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
                    System.out.println("You have succcessfully logged out");
                    break;
                }
                else {
                    System.out.println("Invalid choice.");
                }

            } 
        }

        input.close();
    }



    

}