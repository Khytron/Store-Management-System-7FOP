import java.util.*;

import model.User;
import model.Outlet;
import service.AttendanceManager;
import service.UserManager;
import service.StockManager;
import service.SalesManager;
import service.EditManager;
import service.PerformanceManager;
import util.Methods;
import javax.swing.JOptionPane;


// Main application class for the Store Management System.
// Handles the main execution loop, user login, and navigation between different menus based on user roles.
class StoreManagementApp {
 
    static Scanner input = new Scanner(System.in);
    
    public static void main(String[] args){

        // Initialize service managers
        UserManager userManager = UserManager.getInstance();
        StockManager stockManager = new StockManager();
        SalesManager salesManager = new SalesManager();
        AttendanceManager attendanceManager = new AttendanceManager();
        EditManager editManager = new EditManager();
        PerformanceManager performanceManager = new PerformanceManager();
        Outlet currentOutlet;

        JOptionPane.showMessageDialog(null, "Store Management Operation" +
                        "\nby Group 7 OCC1");

       
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

            // Load the outlet associated with the logged-in user
            currentOutlet = stockManager.loadOutletFromId(loggedInUser.getUserId().substring(0,3));
            

            // Display menu based on user role
            if (loggedInUser.isEmployer){
                // Employer Menu Loop
                String choice = "";
                while (true) {
                    choice = Methods.showInputDialog("\nPick One Option\n1. Register New Employee\n2. Search Stock Information\n3. Search Sales Information\n4. Filter/Sort Sales History\n5. Record New Sale\n6. Perform Stock Count\n7. Stock In\n8. Stock Out\n9. Edit Information\n10. Employee Performance Metrics\n11. Clock In/Clock Out\n12. View Attendance \n13. Logout \n\nYour choice: ", "Menu");
                    
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
                        salesManager.filterAndSortSalesHistory(input);
                        continue;
                    }
                    else if (choice.equals("5")){
                        salesManager.recordNewSale(input, currentOutlet.getOutletId(), loggedInUser.getUserId(), loggedInUser.getUserName());
                        continue;
                    }
                    else if (choice.equals("6")){
                        stockManager.performStockCount(input, currentOutlet.getOutletId());
                        continue;
                    }
                    else if (choice.equals("7")){
                        stockManager.stockIn(input, currentOutlet.getOutletId(), loggedInUser.getUserName());
                        continue;
                    }
                    else if (choice.equals("8")){
                        stockManager.stockOut(input, currentOutlet.getOutletId(), loggedInUser.getUserName());
                        continue;
                    }
                    else if (choice.equals("9")){
                        handleEditInfo(input, editManager, currentOutlet.getOutletId());
                        continue;
                    }
                    else if (choice.equals("10")){
                        performanceManager.viewPerformanceMetrics(input);
                        continue;
                    }
                    else if (choice.equals("11")){
                        handleClockInOut(input, attendanceManager, loggedInUser.getUserId(), currentOutlet.getOutletId());
                        continue;
                    }
                    else if (choice.equals("12")){
                        String viewChoice = Methods.showInputDialog("1. View Employee Attendance\n2. View Today's Attendance\nChoice: ");
                        if (viewChoice.equals("1")){
                            attendanceManager.viewAttendance(input);
                        } else if (viewChoice.equals("2")){
                            attendanceManager.viewTodayAttendance(currentOutlet.getOutletId());
                        }
                        continue;
                    }
                    else if (choice.equals("13")){
                        userManager.attemptLogOut();
                        JOptionPane.showMessageDialog(null, "Account Logged Out Successfully.", "Logged Out", JOptionPane.INFORMATION_MESSAGE);
                        loggedInUser = null;
                        break;
                    } else {
                        JOptionPane.showMessageDialog(null, "Invalid Choice", null, JOptionPane.WARNING_MESSAGE);
                    }
                }
                
            } else // User is employee
            {
                // Employee Menu Loop
                String choice = "";
                while (true){
                    choice = Methods.showInputDialog("\nPick One Option\n1. Search Stock Information \n2. Search Sales Information\n3. Filter/Sort Sales History\n4. Record New Sale\n5. Perform Stock Count\n6. Stock In\n7. Stock Out\n8. Edit Information\n9. Clock In/Clock Out\n10. Logout \n\nYour choice: ");

                    // To fix a small error where if u do input.nextLine() it reads \n immediately
                    //input.nextLine(); irrelevant with JOptionPane
                    
                    if (choice.equals("1")){
                        stockManager.searchStockInfo(input);
                        continue;
                    } 
                    else if (choice.equals("2")){
                        salesManager.searchSalesInfo(input);
                        continue;
                    }
                    else if (choice.equals("3")){
                        salesManager.filterAndSortSalesHistory(input);
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
                        handleEditInfo(input, editManager, currentOutlet.getOutletId());
                        continue;
                    }
                    else if (choice.equals("9")){
                        handleClockInOut(input, attendanceManager, loggedInUser.getUserId(), currentOutlet.getOutletId());
                        continue;
                    }
                    else if (choice.equals("10")){
                        userManager.attemptLogOut();
                        loggedInUser = null;
                        JOptionPane.showMessageDialog(null, "Account Logged Out Successfully.", "Logged Out", JOptionPane.INFORMATION_MESSAGE);
                        break;
                    }

                    else {
                        JOptionPane.showMessageDialog(null, "Invalid Choice", null, JOptionPane.WARNING_MESSAGE);
                    }

                }
            }
        }
        

        input.close();
    }

    // Helper method to handle Clock In/Clock Out menu
    private static void handleClockInOut(Scanner input, AttendanceManager attendanceManager, String userId, String outletId) {
        String clockChoice = Methods.showInputDialog("1. Clock In\n2. Clock Out\nChoice: ");
        if (clockChoice.equals("1")) {
            attendanceManager.clockIn(userId, outletId);
        } else if (clockChoice.equals("2")) {
            attendanceManager.clockOut(userId, outletId);
        }
    }

    // Helper method to handle Edit Information menu
    private static void handleEditInfo(Scanner input, EditManager editManager, String outletId) {
        String editChoice = Methods.showInputDialog("\n1. Edit Stock Information\n2. Edit Sales Information\nChoice: ");
        if (editChoice.equals("1")) {
            editManager.editStockInfo(input, outletId);
        } else if (editChoice.equals("2")) {
            editManager.editSalesInfo(input);
        }
    }
}