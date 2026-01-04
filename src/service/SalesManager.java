package service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import util.FilePath;

public class SalesManager {
    private String keyword;
    private List<String[]> SalesData = new ArrayList<>();

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
        SalesData.clear();
        boolean isFound=false;
        try(BufferedReader br = new BufferedReader(new FileReader(FilePath.salesDataPath))) {
            String firstLine = br.readLine(); // Skip header
            String line;
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");
                for (String field : fields) {
                    if (field.toLowerCase().contains(this.keyword.toLowerCase())) {
                        SalesData.add(fields);
                        isFound = true;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isFound;
    }

    private String getEmployeeName(String employeeID){
        try(BufferedReader br = new BufferedReader(new FileReader(FilePath.employeeDataPath))) {
            String firstLine = br.readLine(); // Skip header
            String line;
            while ((line = br.readLine()) != null) {
                String[] row = line.split(",");
                if (row[0].equals(employeeID)) {
                    return row[1]; // Return employee name
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Unknown Employee";
    }

    private void displaySalesInfo(){
        for(String[] salesData : SalesData){
            System.out.println("\nSales Record Found:");
        
            System.out.println("Date: " + salesData[7]);
            System.out.println("Time: " + salesData[8]);
            System.out.println("Customer: " + salesData[4]);
            System.out.println("Item(s): " + salesData[2]);
            System.out.println("Quantity: " + salesData[3]);
            System.out.println("Total: RM" + salesData[6]);
            System.out.println("Transaction Method: " + salesData[5]);
            System.out.println("Employee: " + getEmployeeName(salesData[1]));
            System.out.println("Status: Transaction Verified.");
        }
        
    }
}
