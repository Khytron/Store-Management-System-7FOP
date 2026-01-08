package service;

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
