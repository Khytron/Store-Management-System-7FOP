package service;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import util.FilePath;
import util.Methods;

import javax.swing.*;

public class SalesManager {
    private String keyword;
    private List<String[]> salesData = new ArrayList<>();

    public SalesManager() {
    }

    public void recordNewSale(Scanner input, String outletId, String employeeId, String employeeName) {
        System.out.println("\n=== Record New Sale ===");
        
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dateFT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFT = DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH);
        DateTimeFormatter csvDateFT = DateTimeFormatter.ofPattern("dd-MM-yy");
        
        String dateStr = now.format(dateFT);
        String csvDateStr = now.format(csvDateFT);
        String timeStr = now.format(timeFT).toLowerCase().replace("am", "a.m.").replace("pm", "p.m.");
        
        System.out.println("Date: " + dateStr);
        System.out.println("Time: " + timeStr);
        
        System.out.print("Customer Name: ");
        String customerName = input.nextLine();
        
        System.out.println("Item(s) Purchased:");
        
        // Map to store model -> [quantity, unitPrice]
        Map<String, int[]> itemsPurchased = new LinkedHashMap<>();
        List<String> modelNames = new ArrayList<>();
        List<Integer> quantities = new ArrayList<>();
        int subtotal = 0;
        
        // Load model data for validation and price lookup
        List<List<String>> modelData = Methods.readCsvFile(FilePath.modelDataPath);
        List<String> header = modelData.get(0);
        modelData.remove(0);
        
        // Find outlet column index
        int outletIndex = -1;
        for (int i = 0; i < header.size(); i++) {
            if (header.get(i).equals(outletId)) {
                outletIndex = i;
                break;
            }
        }
        
        while (true) {
            System.out.print("Enter Model: ");
            String modelName = input.nextLine();
            
            // Find model and get price
            int unitPrice = -1;
            int availableStock = 0;
            
            for (List<String> row : modelData) {
                if (row.get(0).equalsIgnoreCase(modelName)) {
                    unitPrice = Integer.parseInt(row.get(1));
                    if (outletIndex != -1) {
                        availableStock = Integer.parseInt(row.get(outletIndex));
                    }
                    
                    modelName = row.get(0); // Use exact casing from CSV
                    break;
                }
            }
            
            if (unitPrice == -1) {
                System.out.println("Model not found. Please try again.");
                continue;
            }
            
            System.out.print("Enter Quantity: ");
            int quantity;
            try {
                quantity = Integer.parseInt(input.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid quantity.");
                continue;
            }
            
            if (quantity <= 0) {
                System.out.println("Invalid quantity.");
                continue;
            }
            
            if (quantity > availableStock) {
                System.out.println("Insufficient stock. Only " + availableStock + " available.");
                continue;
            }
            
            System.out.println("Unit Price: RM" + unitPrice);
            
            // Add to items purchased
            if (itemsPurchased.containsKey(modelName)) {
                int[] existing = itemsPurchased.get(modelName);
                existing[0] += quantity;
            } else {
                itemsPurchased.put(modelName, new int[]{quantity, unitPrice});
                modelNames.add(modelName);
            }
            quantities.add(quantity);
            subtotal += quantity * unitPrice;
            
            System.out.print("Are there more items purchased? (Y/N): ");
            String more = input.nextLine().trim().toUpperCase();
            if (!more.equals("Y")) {
                break;
            }
        }
        
        if (itemsPurchased.isEmpty()) {
            System.out.println("No items added. Sale cancelled.");
            return;
        }
        
        System.out.print("\nEnter transaction method: ");
        String transactionMethod = input.nextLine();
        
        System.out.println("Subtotal: RM" + subtotal);
        
        // Update stock in model.csv
        updateStockAfterSale(itemsPurchased, outletId);
        
        // Generate next sale ID
        String saleId = generateNextSaleId();
        
        // Save to sales.csv (one row per model)
        saveSalesToCsv(saleId, employeeId, itemsPurchased, customerName, transactionMethod, subtotal, csvDateStr, timeStr);
        
        // Update employee performance metrics
        updatePerformanceMetrics(employeeId, employeeName, subtotal);
        
        System.out.println("\nTransaction \u001B[32msuccessful.\u001B[0m");
        System.out.println("Sale recorded \u001B[32msuccessfully.\u001B[0m");
        System.out.println("Model quantities updated \u001B[32msuccessfully.\u001B[0m");
        
        // Generate receipt
        String receiptFile = Methods.generateSalesReceipt(dateStr, timeStr, customerName, 
                                                           itemsPurchased, transactionMethod, subtotal, employeeName);
        System.out.println("Receipt generated: " + receiptFile);
    }
    
    private String generateNextSaleId() {
        List<List<String>> salesData = Methods.readCsvFile(FilePath.salesDataPath);
        int maxId = 0;
        
        for (int i = 1; i < salesData.size(); i++) {
            String saleId = salesData.get(i).get(0);
            if (saleId.startsWith("S")) {
                try {
                    int id = Integer.parseInt(saleId.substring(1));
                    if (id > maxId) {
                        maxId = id;
                    }
                } catch (NumberFormatException e) {
                    // Skip invalid IDs
                }
            }
        }
        
        return "S" + String.format("%02d", maxId + 1);
    }
    
    private void updateStockAfterSale(Map<String, int[]> items, String outletId) {
        List<List<String>> modelData = Methods.readCsvFile(FilePath.modelDataPath);
        if (modelData.isEmpty()) return;
        
        List<String> header = modelData.get(0);
        int outletIndex = -1;
        for (int i = 0; i < header.size(); i++) {
            if (header.get(i).equals(outletId)) {
                outletIndex = i;
                break;
            }
        }
        
        if (outletIndex == -1) return;
        
        // Update quantities
        for (int i = 1; i < modelData.size(); i++) {
            List<String> row = new ArrayList<>(modelData.get(i));
            String modelId = row.get(0);
            
            if (items.containsKey(modelId)) {
                int currentQty = Integer.parseInt(row.get(outletIndex));
                int soldQty = items.get(modelId)[0];
                int newQty = Math.max(0, currentQty - soldQty);
                row.set(outletIndex, String.valueOf(newQty));
                modelData.set(i, row);
            }
        }
        
        // Write back to CSV
        try (PrintWriter writer = new PrintWriter(new FileWriter(FilePath.modelDataPath))) {
            for (List<String> row : modelData) {
                writer.println(String.join(",", row));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void saveSalesToCsv(String saleId, String employeeId, Map<String, int[]> items, 
                                 String customerName, String transactionMethod, int totalPrice,
                                 String date, String time) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FilePath.salesDataPath, true))) {
            // Create comma-separated model names and total quantity
            StringBuilder modelNamesStr = new StringBuilder();
            int totalQty = 0;
            
            for (Map.Entry<String, int[]> entry : items.entrySet()) {
                if (modelNamesStr.length() > 0) {
                    modelNamesStr.append(";");
                }
                modelNamesStr.append(entry.getKey());
                totalQty += entry.getValue()[0];
            }
            
            // Write single row for the sale
            writer.println(saleId + "," + employeeId + "," + modelNamesStr.toString() + "," + 
                          totalQty + "," + customerName + "," + transactionMethod + "," + 
                          totalPrice + "," + date + "," + time);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void searchSalesInfo(Scanner input) {
        //System.out.println("\n=== Search Sales Information ===");
        //System.out.print("Search keyword: ");
        //String searchKeyword = input.next();
        String searchKeyword = JOptionPane.showInputDialog("Search keyword: ");
        this.keyword = searchKeyword;
        //System.out.println("Searching...");

        if (findSales()) {
            displaySalesInfo();
        } else {
            //System.out.println("No matching sales record found.");
            JOptionPane.showMessageDialog(null, "No matching sales record found", null, JOptionPane.WARNING_MESSAGE);
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
            /*
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
             */
            JOptionPane.showMessageDialog(null, "Sales Record Found:" +
                    "\nDate: " + sale[7] +
                    "\nTime: " + sale[8] +
                    "\nCustomer: " + sale[4] +
                    "\nItem(s): " + sale[2] +
                    "\nQuantity: " + sale[3] +
                    "\nTotal: RM" + sale[6] +
                    "\nTransaction Method: " + sale[5] +
                    "\nEmployee: " + getEmployeeName(sale[1]) +
                    "\nStatus: Transaction Verified.")
            ;
        }
        
    }

    private void updatePerformanceMetrics(String employeeId, String employeeName, int saleAmount) {
        List<List<String>> performanceData = Methods.readCsvFile(FilePath.performanceDataPath);
        
        // Check if file is empty or only has header
        if (performanceData.isEmpty()) {
            performanceData.add(List.of("EmployeeID", "EmployeeName", "TotalSalesAmount", "NumberOfTransactions"));
        }
        
        // Find existing employee record
        int employeeRowIndex = -1;
        for (int i = 1; i < performanceData.size(); i++) {
            if (performanceData.get(i).size() >= 1 && performanceData.get(i).get(0).equals(employeeId)) {
                employeeRowIndex = i;
                break;
            }
        }
        
        if (employeeRowIndex != -1) {
            // Update existing record
            List<String> row = performanceData.get(employeeRowIndex);
            int currentTotal = Integer.parseInt(row.get(2));
            int currentCount = Integer.parseInt(row.get(3));
            
            List<String> updatedRow = new ArrayList<>();
            updatedRow.add(employeeId);
            updatedRow.add(employeeName);
            updatedRow.add(String.valueOf(currentTotal + saleAmount));
            updatedRow.add(String.valueOf(currentCount + 1));
            
            performanceData.set(employeeRowIndex, updatedRow);
        } else {
            // Add new record
            List<String> newRow = new ArrayList<>();
            newRow.add(employeeId);
            newRow.add(employeeName);
            newRow.add(String.valueOf(saleAmount));
            newRow.add("1");
            
            performanceData.add(newRow);
        }
        
        // Write back to CSV
        try (PrintWriter writer = new PrintWriter(new FileWriter(FilePath.performanceDataPath))) {
            for (List<String> row : performanceData) {
                writer.println(String.join(",", row));
            }
        } catch (IOException e) {
            System.out.println("Error updating performance metrics: " + e.getMessage());
        }
    }

    public void filterAndSortSalesHistory(Scanner input) {
        /*
        System.out.println("\n=== Filter and Sort Sales History ===");
        System.out.print("Enter Start Date (dd-MM-yy): ");
        String startDate = input.nextLine();
        System.out.print("Enter End Date (dd-MM-yy): ");
        String endDate = input.nextLine();
         */
        String startDate = JOptionPane.showInputDialog("Enter Start Date (dd-MM-yy): ");
        String endDate = JOptionPane.showInputDialog("Enter End Date (dd-MM-yy): ");

        List<List<String>> allSalesData = Methods.readCsvFile(FilePath.salesDataPath);
        
        if (allSalesData.size() <= 1) {
            //System.out.println("No sales records found.");
            JOptionPane.showMessageDialog(null, "No sales records found.", null, JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Filter sales by date range
        List<String[]> filteredSales = new ArrayList<>();
        for (int i = 1; i < allSalesData.size(); i++) {
            List<String> row = allSalesData.get(i);
            if (row.size() >= 9) {
                String saleDate = row.get(7);
                if (isDateInRange(saleDate, startDate, endDate)) {
                    filteredSales.add(row.toArray(new String[0]));
                }
            }
        }

        if (filteredSales.isEmpty()) {
            //System.out.println("No sales records found for the specified date range.");
            JOptionPane.showMessageDialog(null, "No sales records found for the specified date range.", null, JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Calculate total cumulative sales
        int totalCumulativeSales = 0;
        for (String[] sale : filteredSales) {
            try {
                totalCumulativeSales += Integer.parseInt(sale[6]);
            } catch (NumberFormatException e) {
                // Skip invalid entries
            }
        }

        // Ask for sorting preference
        /*
        System.out.println("\nSort by:");
        System.out.println("1. Date (Ascending)");
        System.out.println("2. Date (Descending)");
        System.out.println("3. Amount (Lowest to Highest)");
        System.out.println("4. Amount (Highest to Lowest)");
        System.out.println("5. Customer Name (A-Z)");
        System.out.println("6. Customer Name (Z-A)");
        System.out.print("Choice: ");
        String sortChoice = input.nextLine();
         */
        String sortChoice = JOptionPane.showInputDialog("Sort by:" +
                        "\n1. Date (Ascending)" +
                        "\n2. Date (Descending)" +
                        "\n3. Amount (Lowest to Highest)" +
                        "\n4. Amount (Highest to Lowest)" +
                        "\n5. Customer Name (A-Z)" +
                        "\n6. Customer Name (Z-A)" +
                        "\nChoice: ");

        // Sort based on choice
        switch (sortChoice) {
            case "1": // Date Ascending
                filteredSales.sort((a, b) -> compareDates(a[7], b[7]));
                break;
            case "2": // Date Descending
                filteredSales.sort((a, b) -> compareDates(b[7], a[7]));
                break;
            case "3": // Amount Low to High
                filteredSales.sort((a, b) -> {
                    int amtA = Integer.parseInt(a[6]);
                    int amtB = Integer.parseInt(b[6]);
                    return Integer.compare(amtA, amtB);
                });
                break;
            case "4": // Amount High to Low
                filteredSales.sort((a, b) -> {
                    int amtA = Integer.parseInt(a[6]);
                    int amtB = Integer.parseInt(b[6]);
                    return Integer.compare(amtB, amtA);
                });
                break;
            case "5": // Customer Name A-Z
                filteredSales.sort((a, b) -> a[4].compareToIgnoreCase(b[4]));
                break;
            case "6": // Customer Name Z-A
                filteredSales.sort((a, b) -> b[4].compareToIgnoreCase(a[4]));
                break;
            default:
                //System.out.println("Invalid choice. Displaying unsorted.");
                JOptionPane.showMessageDialog(null, "Invalid choice. Displaying unsorted...", null, JOptionPane.WARNING_MESSAGE);
        }

        // Display results in tabular format
        /*
        System.out.println("\n================================================================================");
        System.out.println("                    Sales History (" + startDate + " to " + endDate + ")");
        System.out.println("================================================================================");
        System.out.printf("%-8s %-12s %-20s %-15s %-10s %-10s\n", 
                          "SaleID", "Date", "Customer", "Model(s)", "Amount", "Method");
        System.out.println("--------------------------------------------------------------------------------");
         */
        String OutputText1 = "<html><pre><br>================================================================================"
                + "<br>                    Sales History (" + startDate + " to " + endDate + ")"
                + "<br>================================================================================";
        OutputText1 += String.format("<br>%-8s %-12s %-20s %-15s %-10s %-10s",
                "SaleID", "Date", "Customer", "Model(s)", "Amount", "Method");
        OutputText1 += "<br>--------------------------------------------------------------------------------";



        for (String[] sale : filteredSales) {
            String saleId = sale[0];
            String date = sale[7];
            String customer = sale[4].length() > 18 ? sale[4].substring(0, 18) + ".." : sale[4];
            String models = sale[2].length() > 13 ? sale[2].substring(0, 13) + ".." : sale[2];
            String amount = "RM" + sale[6];
            String method = sale[5].length() > 8 ? sale[5].substring(0, 8) + ".." : sale[5];

            //System.out.printf("%-8s %-12s %-20s %-15s %-10s %-10s\n", saleId, date, customer, models, amount, method);
            OutputText1 += String.format("<br>%-8s %-12s %-20s %-15s %-10s %-10s", saleId, date, customer, models, amount, method);
        }
        /*
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("Total Transactions: " + filteredSales.size());
        System.out.println("Total Cumulative Sales: RM" + totalCumulativeSales);
        System.out.println("================================================================================");
         */
        OutputText1 += "<br>--------------------------------------------------------------------------------" +
                "<br>Total Transactions: " + filteredSales.size() +
                "<br>Total Cumulative Sales: RM" + totalCumulativeSales +
                "<br>================================================================================</pre></html>";

        //Set font to a monospace font
        Font monospacefont = new Font("Monospaced", Font.PLAIN, 12);
        JLabel label = new JLabel(OutputText1);
        label.setFont(monospacefont);

        JOptionPane.showMessageDialog(null, label, "receipt", JOptionPane.INFORMATION_MESSAGE);
    }

    private boolean isDateInRange(String date, String startDate, String endDate) {
        try {
            int[] dateParts = parseDate(date);
            int[] startParts = parseDate(startDate);
            int[] endParts = parseDate(endDate);

            int dateValue = dateParts[2] * 10000 + dateParts[1] * 100 + dateParts[0];
            int startValue = startParts[2] * 10000 + startParts[1] * 100 + startParts[0];
            int endValue = endParts[2] * 10000 + endParts[1] * 100 + endParts[0];

            return dateValue >= startValue && dateValue <= endValue;
        } catch (Exception e) {
            return false;
        }
    }

    private int[] parseDate(String date) {
        String[] parts = date.split("-");
        return new int[]{
            Integer.parseInt(parts[0]),  // day
            Integer.parseInt(parts[1]),  // month
            Integer.parseInt(parts[2])   // year
        };
    }

    private int compareDates(String date1, String date2) {
        try {
            int[] parts1 = parseDate(date1);
            int[] parts2 = parseDate(date2);

            int value1 = parts1[2] * 10000 + parts1[1] * 100 + parts1[0];
            int value2 = parts2[2] * 10000 + parts2[1] * 100 + parts2[0];

            return Integer.compare(value1, value2);
        } catch (Exception e) {
            return 0;
        }
    }
}
