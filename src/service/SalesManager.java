package service;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import util.FilePath;
import util.Methods;

public class SalesManager {
    private String keyword;
    private List<String[]> salesData = new ArrayList<>();

    public SalesManager() {
    }

    public void searchSalesInfo(Scanner input) {
        System.out.println("\n=== Search Sales Information ===");
        System.out.print("Search keyword: ");
        String searchKeyword = input.next();
        this.keyword = searchKeyword;
        System.out.println("Searching...");

        if (findSales()) {
            displaySalesInfo();
        } else {
            System.out.println("No matching sales record found.");
        }
    }

    private boolean findSales(){
        salesData.clear();
        boolean isFound = false;
        
        List<List<String>> data = Methods.readCsvFile(FilePath.salesDataPath);
        
        // Remove header
        if (!data.isEmpty())
            data.remove(0);
        
        for (List<String> row : data) {
            for (String field : row) {
                if (field.toLowerCase().contains(this.keyword.toLowerCase())) {
                    salesData.add(row.toArray(new String[0]));
                    isFound = true;
                    break;
                }
            }
        }
        
        return isFound;
    }

    private String getEmployeeName(String employeeID){
        List<List<String>> data = Methods.readCsvFile(FilePath.employeeDataPath);
        
        // Remove header
        if (!data.isEmpty())
            data.remove(0);
        
        for (List<String> row : data) {
            if (row.get(0).equals(employeeID)) {
                return row.get(1);
            }
        }
        
        return "Unknown Employee";
    }

    private void displaySalesInfo(){
        for(String[] sale : salesData){
            System.out.println("\nSales Record Found:");
        
            System.out.println("Date: " + sale[7]);
            System.out.println("Time: " + sale[8]);
            System.out.println("Customer: " + sale[4]);
            System.out.println("Item(s): " + sale[2]);
            System.out.println("Quantity: " + sale[3]);
            System.out.println("Total: RM" + sale[6]);
            System.out.println("Transaction Method: " + sale[5]);
            System.out.println("Employee: " + getEmployeeName(sale[1]));
            System.out.println("Status: Transaction Verified.");
        }
        
    }
}
