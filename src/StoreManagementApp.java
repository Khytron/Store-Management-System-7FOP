import java.util.*;

import model.User;
import model.Outlet;
import service.AttendanceManager;
import service.UserManager;
import service.StockManager;
import service.SalesManager;



class StoreManagementApp {
 
    static Scanner input = new Scanner(System.in);
    
    public static void main(String[] args){

        UserManager userManager = UserManager.getInstance();
        StockManager stockManager = new StockManager();
        SalesManager salesManager = new SalesManager();
        AttendanceManager attendanceManager = new AttendanceManager();
        Outlet currentOutlet;

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

            currentOutlet = stockManager.loadOutletFromId(loggedInUser.getUserId().substring(0,3));
            

            if (loggedInUser.isEmployer){
                String choice = "";
                while (true) {
                    System.out.print("\nPick One Option\n1. Register New Employee\n2. Search Stock Information\n3. Search Sales Information\n4. Record New Sale\n5. Perform Stock Count\n6. Stock In\n7. Stock Out\n8. Clock In/Clock Out\n9. View Attendance \n10. Logout \n\nYour choice: ");
                    choice = input.next();
                    
                    // To fix a small error where if u do input.nextLine() it reads \n immediately
                    input.nextLine();

                    if (choice.equals("1")){
                        userManager.registerNewEmployee(input);
                        continue;
                    }
                    else if (choice.equals("2")){
                        stockManager.searchStockInfo(input);
                        continue;
                    } else if (choice.equals("3")){
                        salesManager.searchSalesInfo(input);
                        continue;
                    }
                    else if (choice.equals("4")){
                        salesManager.recordNewSale(input, currentOutlet.getOutletId(), loggedInUser.getUserId(), loggedInUser.getUserName());
                        continue;
                    }
                    else if (choice.equals("5")){
                        stockManager.performStockCount(input, currentOutlet.getOutletId());
                        continue;
                    }
                    else if (choice.equals("6")){
                        stockManager.stockIn(input, currentOutlet.getOutletId(), loggedInUser.getUserName());
                        continue;
                    }
                    else if (choice.equals("7")){
                        stockManager.stockOut(input, currentOutlet.getOutletId(), loggedInUser.getUserName());
                        continue;
                    }
                    else if (choice.equals("8")){
                        handleClockInOut(input, attendanceManager, loggedInUser.getUserId(), currentOutlet.getOutletId());
                        continue;
                    }
                    else if (choice.equals("9")){
                        System.out.print("\n1. View Employee Attendance\n2. View Today's Attendance\nChoice: ");
                        String viewChoice = input.nextLine();
                        if (viewChoice.equals("1")){
                            attendanceManager.viewAttendance(input);
                        } else if (viewChoice.equals("2")){
                            attendanceManager.viewTodayAttendance(currentOutlet.getOutletId());
                        }
                        continue;
                    }
                    else if (choice.equals("10")){
                        userManager.attemptLogOut();
                        System.out.println("\n\u001B[31mLogging Out..\u001B[0m\n");
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
                    System.out.print("\nPick One Option\n1. Search Stock Information \n2. Search Sales Information\n3. Record New Sale\n4. Perform Stock Count\n5. Stock In\n6. Stock Out\n7. Clock In/Clock Out\n8. Logout \n\nYour choice: ");
                    choice = input.next();

                    // To fix a small error where if u do input.nextLine() it reads \n immediately
                    input.nextLine();
                    
                    if (choice.equals("1")){
                        stockManager.searchStockInfo(input);
                        continue;
                    } 
                    else if (choice.equals("2")){
                        salesManager.searchSalesInfo(input);
                        continue;
                    }
                    else if (choice.equals("3")){
                        salesManager.recordNewSale(input, currentOutlet.getOutletId(), loggedInUser.getUserId(), loggedInUser.getUserName());
                        continue;
                    }
                    else if (choice.equals("4")){
                        stockManager.performStockCount(input, currentOutlet.getOutletId());
                        continue;
                    } 
                    else if (choice.equals("5")){
                        stockManager.stockIn(input, currentOutlet.getOutletId(), loggedInUser.getUserName());
                        continue;
                    }
                    else if (choice.equals("6")){
                        stockManager.stockOut(input, currentOutlet.getOutletId(), loggedInUser.getUserName());
                        continue;
                    }
                    else if (choice.equals("7")){
                        handleClockInOut(input, attendanceManager, loggedInUser.getUserId(), currentOutlet.getOutletId());
                        continue;
                    }
                    else if (choice.equals("8")){
                        userManager.attemptLogOut();
                        loggedInUser = null;
                        System.out.println("\n\u001B[31mLogging Out..\u001B[0m\n");
                        
                        break;
                    }

                    else {
                        System.out.println("\nInvalid choice.");
                    }

                }
            }
        }
        

        input.close();
    }

    // Helper method to handle Clock In/Clock Out menu
    private static void handleClockInOut(Scanner input, AttendanceManager attendanceManager, String userId, String outletId) {
        System.out.print("\n1. Clock In\n2. Clock Out\nChoice: ");
        String clockChoice = input.nextLine();
        if (clockChoice.equals("1")) {
            attendanceManager.clockIn(userId, outletId);
        } else if (clockChoice.equals("2")) {
            attendanceManager.clockOut(userId, outletId);
        }
    }
}