package service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Locale;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.File;

import model.Model;
import model.Outlet;
import util.FilePath;
import util.Methods;



public class StockManager {
    private String userSearch;
    private int unitPrice;
    private LinkedHashMap<String, Integer> outletStock = new LinkedHashMap<>();
    private String[] outletIds;
    private Map<String, Outlet> outlets = new LinkedHashMap<>();
    private List<Model> models = new ArrayList<>();

    public StockManager() {
        loadOutlets();
        loadModels();
    }

    

    private void loadOutlets() {
        if (!outlets.isEmpty()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(FilePath.outletDataPath))) {
            br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                String[] arr = line.split(",");
                outlets.put(arr[0], new Outlet(arr[0], arr[1]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadModels(){
        // Read file data from model.csv
        List<List<String>> modelData = Methods.readCsvFile(FilePath.modelDataPath);

        // Removing the first row of employee data (the column name)
        List<String> header = modelData.get(0);;
        if (!modelData.isEmpty())
            modelData.remove(0);

        // Clearing the model list
        models.clear();
  

        for (List<String> model : modelData) {
            // Getting data
            String modelId = model.get(0);
            String modelPrice = model.get(1);
            
            Model newModel = new Model(modelId, modelPrice);
            for (int i = 2; i < model.size(); i++ ){
                newModel.setStock(header.get(i), Integer.valueOf(model.get(i)));
            }

            models.add(newModel);
        }
    } 

    public Outlet loadOutletFromId(String outletId){
        return outlets.get(outletId);
    }

    public void searchStockInfo(Scanner input) {
        System.out.println("\n=== Search Stock Information ===");
        System.out.print("Search Model Name: ");
        String search = input.next();
        userSearch = search;
        outletStock.clear();
        System.out.println("Searching...\n");

        if (findStock(search)) {
            displayStockInfo();
        } else {
            System.out.println("Model " + search + " is not found in the database.");
        }
    }

    private boolean findStock(String search) {
        boolean isFound = false;
        try (BufferedReader br = new BufferedReader(new FileReader(FilePath.modelDataPath))) {
            String firstLine = br.readLine();
            String[] arr = firstLine.split(",");
            outletIds = new String[arr.length - 2];
            for (int i = 2; i < arr.length; i++) {
                outletIds[i - 2] = arr[i];
            }

            String line = br.readLine();
            while (line != null) {
                arr = line.split(",");
                if (arr[0].equalsIgnoreCase(search)) {
                    unitPrice = Integer.parseInt(arr[1]);
                    for (int i = 2; i < arr.length; i++) {
                        if (Integer.parseInt(arr[i]) != 0) {
                            outletStock.put(outletIds[i - 2], Integer.parseInt(arr[i]));
                        }
                    }
                    isFound = true;
                    break;
                }
                line = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return isFound;
    }

    private void displayStockInfo() {
        System.out.printf("Model: %s\n", userSearch);
        System.out.printf("Unit Price: RM%d\n", unitPrice);
        System.out.println("Stock by outlet: ");
        for (String key : outletStock.keySet()) {
            Outlet outlet = outlets.get(key);
            String outletName = (outlet != null) ? outlet.getOutletName() : key;
            System.out.printf("%s: %d \n", outletName, outletStock.get(key));
        }
    }

    public void performStockCount(Scanner input, String outletId) {
        System.out.println("\n=== Stock Count === ");

        LocalDateTime now = LocalDateTime.now();
    
        DateTimeFormatter dateFT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFT = DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH);

        System.out.println("Date: " + now.format(dateFT));
        String timeString = now.format(timeFT).toLowerCase().replace("am", "a.m.").replace("pm", "p.m.");
        System.out.println("Time: " + timeString);

        int totalModelsChecked = 0;
        int tallyCorrect = 0;

        for (Model model : models ){
            System.out.print("\nModel: " + model.getModelId() + " - Counted: ");
            int userCount = input.nextInt();
            int actualStockCount = model.getStock(outletId);
            System.out.println("Stock Record: " + actualStockCount);

            if (userCount == actualStockCount){
                System.out.println("\u001B[32m" + "Stock tally correct." +"\u001B[0m");
                tallyCorrect++;
            } else {
                System.out.println("\u001B[31m" + "! Mismatch detected (" + Math.abs(actualStockCount - userCount) + " unit difference)" + "\u001B[0m");
            }

            totalModelsChecked++;

        }

        System.out.println("\nTotal Models Checked: " + totalModelsChecked);
        System.out.println("Tally Correct: " + tallyCorrect);
        System.out.println("Mismatches: " + (totalModelsChecked - tallyCorrect));
        System.out.println("Stock Count Completed.");
        if (totalModelsChecked != tallyCorrect)
            System.out.println("\u001B[31m" + "Warning: Please verify stock." + "\u001B[0m");
    }

    public void stockIn(Scanner input, String currentOutletId, String employeeName) {
        System.out.println("\n=== Stock In ===");
        
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dateFT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFT = DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH);
        
        String dateStr = now.format(dateFT);
        String timeStr = now.format(timeFT).toLowerCase().replace("am", "a.m.").replace("pm", "p.m.");
        
        System.out.println("Date: " + dateStr);
        System.out.println("Time: " + timeStr);
        
        // Select source outlet (excluding current outlet)
        System.out.println("\nAvailable Sources:");
        List<String> outletIdList = new ArrayList<>();
        int idx = 1;
        for (String id : outlets.keySet()) {
            if (!id.equals(currentOutletId)) {
                Outlet o = outlets.get(id);
                System.out.println(idx + ". " + id + " (" + o.getOutletName() + ")");
                outletIdList.add(id);
                idx++;
            }
        }
        System.out.println(idx + ". HQ (Service Center)");
        outletIdList.add("HQ");
        
        System.out.print("Select source (From): ");
        int fromChoice = input.nextInt();
        input.nextLine();
        
        if (fromChoice < 1 || fromChoice > outletIdList.size()) {
            System.out.println("Invalid selection.");
            return;
        }
        
        String fromOutletId = outletIdList.get(fromChoice - 1);
        String fromOutletName = fromOutletId.equals("HQ") ? "Service Center" : outlets.get(fromOutletId).getOutletName();
        
        Outlet currentOutlet = outlets.get(currentOutletId);
        String toOutletName = currentOutlet.getOutletName();
        
        System.out.println("From: " + fromOutletId + " (" + fromOutletName + ")");
        System.out.println("To: " + currentOutletId + " (" + toOutletName + ")");
        
        // Collect models and quantities
        Map<String, Integer> modelsReceived = new LinkedHashMap<>();
        int totalQuantity = 0;
        
        while (true) {
            System.out.print("\nEnter Model Name (or 'done' to finish): ");
            String modelName = input.next();
            
            if (modelName.equalsIgnoreCase("done")) {
                break;
            }
            
            // Validate model exists
            Model foundModel = null;
            for (Model m : models) {
                if (m.getModelId().equalsIgnoreCase(modelName)) {
                    foundModel = m;
                    break;
                }
            }
            
            if (foundModel == null) {
                System.out.println("Model not found. Please try again.");
                continue;
            }
            
            // Check available stock from source (skip for HQ - unlimited supply)
            int availableStock = -1;
            if (!fromOutletId.equals("HQ")) {
                availableStock = foundModel.getStock(fromOutletId);
                System.out.println("Available stock at source: " + availableStock);
            } else {
                System.out.println("Source: HQ (unlimited supply)");
            }
            
            System.out.print("Enter Quantity: ");
            int quantity = input.nextInt();
            input.nextLine();
            
            if (quantity <= 0) {
                System.out.println("Invalid quantity.");
                continue;
            }
            
            // Check if source has enough stock (skip for HQ)
            if (!fromOutletId.equals("HQ") && quantity > availableStock) {
                System.out.println("Insufficient stock at source. Only " + availableStock + " available.");
                continue;
            }
            
            modelsReceived.put(foundModel.getModelId(), modelsReceived.getOrDefault(foundModel.getModelId(), 0) + quantity);
            totalQuantity += quantity;
        }
        
        if (modelsReceived.isEmpty()) {
            System.out.println("No models added. Stock In cancelled.");
            return;
        }
        
        // Update model quantities - decrease from source (if not HQ), increase in current outlet
        if (!fromOutletId.equals("HQ")) {
            updateModelQuantities(modelsReceived, fromOutletId, false);
        }
        updateModelQuantities(modelsReceived, currentOutletId, true);
        
        // Reload models to reflect changes
        loadModels();
        
        System.out.println("\nModels Received:");
        for (Map.Entry<String, Integer> entry : modelsReceived.entrySet()) {
            System.out.println("- " + entry.getKey() + " (Quantity: " + entry.getValue() + ")");
        }
        System.out.println("Total Quantity: " + totalQuantity);
        System.out.println("\u001B[32mModel quantities updated successfully.\u001B[0m");
        System.out.println("\u001B[32mStock In recorded.\u001B[0m");
        
        // Generate receipt
        String receiptFile = generateReceipt("Stock In", dateStr, timeStr, fromOutletId, fromOutletName, 
                                              currentOutletId, toOutletName, modelsReceived, totalQuantity, employeeName);
        System.out.println("Receipt generated: " + receiptFile);
    }

    public void stockOut(Scanner input, String currentOutletId, String employeeName) {
        System.out.println("\n=== Stock Out ===");
        
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dateFT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFT = DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH);
        
        String dateStr = now.format(dateFT);
        String timeStr = now.format(timeFT).toLowerCase().replace("am", "a.m.").replace("pm", "p.m.");
        
        System.out.println("Date: " + dateStr);
        System.out.println("Time: " + timeStr);
        
        Outlet currentOutlet = outlets.get(currentOutletId);
        String fromOutletName = currentOutlet.getOutletName();
        
        // Select destination outlet (including HQ)
        System.out.println("\nAvailable Destinations:");
        List<String> outletIdList = new ArrayList<>();
        int idx = 1;
        for (String id : outlets.keySet()) {
            if (!id.equals(currentOutletId)) {
                Outlet o = outlets.get(id);
                System.out.println(idx + ". " + id + " (" + o.getOutletName() + ")");
                outletIdList.add(id);
                idx++;
            }
        }
        System.out.println(idx + ". HQ (Service Center)");
        outletIdList.add("HQ");
        
        System.out.print("Select destination (To): ");
        int toChoice = input.nextInt();
        input.nextLine();
        
        if (toChoice < 1 || toChoice > outletIdList.size()) {
            System.out.println("Invalid selection.");
            return;
        }
        
        String toOutletId = outletIdList.get(toChoice - 1);
        String toOutletName = toOutletId.equals("HQ") ? "Service Center" : outlets.get(toOutletId).getOutletName();
        
        System.out.println("From: " + currentOutletId + " (" + fromOutletName + ")");
        System.out.println("To: " + toOutletId + " (" + toOutletName + ")");
        
        // Collect models and quantities
        Map<String, Integer> modelsTransferred = new LinkedHashMap<>();
        int totalQuantity = 0;
        
        while (true) {
            System.out.print("\nEnter Model Name (or 'done' to finish): ");
            String modelName = input.next();
            
            if (modelName.equalsIgnoreCase("done")) {
                break;
            }
            
            // Validate model exists
            Model foundModel = null;
            for (Model m : models) {
                if (m.getModelId().equalsIgnoreCase(modelName)) {
                    foundModel = m;
                    break;
                }
            }
            
            if (foundModel == null) {
                System.out.println("Model not found. Please try again.");
                continue;
            }
            
            // Check available stock
            int availableStock = foundModel.getStock(currentOutletId);
            System.out.println("Available stock: " + availableStock);
            
            System.out.print("Enter Quantity: ");
            int quantity = input.nextInt();
            input.nextLine();
            
            if (quantity <= 0) {
                System.out.println("Invalid quantity.");
                continue;
            }
            
            if (quantity > availableStock) {
                System.out.println("Insufficient stock. Only " + availableStock + " available.");
                continue;
            }
            
            modelsTransferred.put(foundModel.getModelId(), modelsTransferred.getOrDefault(foundModel.getModelId(), 0) + quantity);
            totalQuantity += quantity;
        }
        
        if (modelsTransferred.isEmpty()) {
            System.out.println("No models added. Stock Out cancelled.");
            return;
        }
        
        // Update model quantities - decrease from current outlet, only increase in destination if not HQ
        updateModelQuantities(modelsTransferred, currentOutletId, false);
        if (!toOutletId.equals("HQ")) {
            updateModelQuantities(modelsTransferred, toOutletId, true);
        }
        
        // Reload models to reflect changes
        loadModels();
        
        System.out.println("\nModels Transferred:");
        for (Map.Entry<String, Integer> entry : modelsTransferred.entrySet()) {
            System.out.println("- " + entry.getKey() + " (Quantity: " + entry.getValue() + ")");
        }
        System.out.println("Total Quantity: " + totalQuantity);
        System.out.println("\u001B[32mModel quantities updated successfully.\u001B[0m");
        System.out.println("\u001B[32mStock Out recorded.\u001B[0m");
        
        // Generate receipt
        String receiptFile = generateReceipt("Stock Out", dateStr, timeStr, currentOutletId, fromOutletName, 
                                              toOutletId, toOutletName, modelsTransferred, totalQuantity, employeeName);
        System.out.println("Receipt generated: " + receiptFile);
    }

    private void updateModelQuantities(Map<String, Integer> modelChanges, String outletId, boolean isAdd) {
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
            
            if (modelChanges.containsKey(modelId)) {
                int currentQty = Integer.parseInt(row.get(outletIndex));
                int change = modelChanges.get(modelId);
                int newQty = isAdd ? currentQty + change : currentQty - change;
                row.set(outletIndex, String.valueOf(Math.max(0, newQty)));
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

    private String generateReceipt(String transactionType, String date, String time, 
                                   String fromId, String fromName, String toId, String toName,
                                   Map<String, Integer> modelItems, int totalQty, String employeeName) {
        String fileName = "receipts_" + date + ".txt";
        String filePath = FilePath.receiptsFolder + fileName;
        
        // Create receipts folder if it doesn't exist
        File folder = new File(FilePath.receiptsFolder);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        
        StringBuilder receipt = new StringBuilder();
        receipt.append("\n=== ").append(transactionType).append(" ===\n");
        receipt.append("Date: ").append(date).append("\n");
        receipt.append("Time: ").append(time).append("\n");
        receipt.append("From: ").append(fromId).append(" (").append(fromName).append(")\n");
        receipt.append("To: ").append(toId).append(" (").append(toName).append(")\n");
        receipt.append("Models ").append(transactionType.equals("Stock In") ? "Received" : "Transferred").append(":\n");
        
        for (Map.Entry<String, Integer> entry : modelItems.entrySet()) {
            receipt.append("- ").append(entry.getKey()).append(" (Quantity: ").append(entry.getValue()).append(")\n");
        }
        
        receipt.append("Total Quantity: ").append(totalQty).append("\n");
        receipt.append("Employee in Charge: ").append(employeeName).append("\n");
        
        // Append to file (same day receipts go to same file)
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath, true))) {
            writer.print(receipt.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return fileName;
    }
}
