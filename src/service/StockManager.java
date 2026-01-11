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

import model.Model;
import model.Outlet;
import util.FilePath;
import util.Methods;

import javax.swing.*;


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
        // Read file data from outlet.csv
        List<List<String>> outletData = Methods.readCsvFile(FilePath.outletDataPath);

        // Removing the first row (the column name)
        if (!outletData.isEmpty())
            outletData.remove(0);

        // Clearing the outlets map
        outlets.clear();

        for (List<String> outlet : outletData) {
            String outletId = outlet.get(0);
            String outletName = outlet.get(1);
            outlets.put(outletId, new Outlet(outletId, outletName));
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
        /*System.out.println("\n=== Search Stock Information ===");
        System.out.print("Search Model Name: ");
        String search = input.next();
        userSearch = search;
        outletStock.clear();
        System.out.println("Searching...\n");
         */

        String search = Methods.showInputDialog("Search Model Name: ");
        userSearch = search;
        outletStock.clear();

        if (findStock(search)) {
            displayStockInfo();
        } else {
            //System.out.println("Model " + search + " is not found in the database.");
            JOptionPane.showMessageDialog(null, "Model " + search + " is not found in the database",null, JOptionPane.WARNING_MESSAGE);
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

        //System.out.printf("Model: %s\n", userSearch);
        //System.out.printf("Unit Price: RM%d\n", unitPrice);
        //System.out.println("Stock by outlet: ");
        String outputMessage = String.format("Model: %s\nUnit Price: RM%d\nStock by outlet:\n", userSearch, unitPrice);

        for (String key : outletStock.keySet()) {
            Outlet outlet = outlets.get(key);
            String outletName = (outlet != null) ? outlet.getOutletName() : key;
            //System.out.printf("%s: %d \n", outletName, outletStock.get(key));
            outputMessage += String.format("%s: %d \n", outletName, outletStock.get(key));

        }
        JOptionPane.showMessageDialog(null, outputMessage);
    }

    public void performStockCount(Scanner input, String outletId) {
        //System.out.println("\n=== Stock Count === ");
        //put at titles

        LocalDateTime now = LocalDateTime.now();
    
        DateTimeFormatter dateFT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFT = DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH);

        /* System.out.println("Date: " + now.format(dateFT));
        String timeString = now.format(timeFT).toLowerCase().replace("am", "a.m.").replace("pm", "p.m.");
        System.out.println("Time: " + timeString); */
        JOptionPane.showMessageDialog(null,
                "Date: " + now.format(dateFT)
        + "\nTime: " + now.format(timeFT).toLowerCase().replace("am", "a.m.").replace("pm", "p.m."),
                null, JOptionPane.INFORMATION_MESSAGE);

        int totalModelsChecked = 0;
        int tallyCorrect = 0;

        for (Model model : models ){
            //System.out.print("\nModel: " + model.getModelId() + " - Counted: ");
            //int userCount = input.nextInt();
            int userCount = Integer.parseInt(Methods.showInputDialog("\nModel: " + model.getModelId() + " - Counted: "));
            int actualStockCount = model.getStock(outletId);
            //System.out.println("Stock Record: " + actualStockCount);

            if (userCount == actualStockCount){
                //System.out.println("\u001B[32m" + "Stock tally correct." +"\u001B[0m");
                JOptionPane.showMessageDialog(null,
                        "Stock Record: " + actualStockCount
                        + "\nStock tally correct.",
                        null, JOptionPane.INFORMATION_MESSAGE);
                tallyCorrect++;
            } else {
                //System.out.println("\u001B[31m" + "! Mismatch detected (" + Math.abs(actualStockCount - userCount) + " unit difference)" + "\u001B[0m");
                JOptionPane.showMessageDialog(null,
                        "Stock Record: " + actualStockCount
                        + "\n! Mismatch detected (" + Math.abs(actualStockCount - userCount) + " unit difference)",
                        null, JOptionPane.WARNING_MESSAGE);
            }

            totalModelsChecked++;
        }
        /*
        System.out.println("\nTotal Models Checked: " + totalModelsChecked);
        System.out.println("Tally Correct: " + tallyCorrect);
        System.out.println("Mismatches: " + (totalModelsChecked - tallyCorrect));
        System.out.println("Stock Count Completed.");
         */
        String tallyIncorrect = "";
        if (totalModelsChecked != tallyCorrect) {
            //System.out.println("\u001B[31m" + "Warning: Please verify stock." + "\u001B[0m");
            tallyIncorrect = "\n!!! Warning: Please verify stock. !!!";
        }
        JOptionPane.showMessageDialog(null,
                "\nTotal Models Checked: " + totalModelsChecked
                + "\nTally Correct: " + tallyCorrect
                + "\nMismatches: " + (totalModelsChecked - tallyCorrect)
                + "\nStock Count Completed."
                + tallyIncorrect, null, JOptionPane.INFORMATION_MESSAGE);
    }

    public void stockIn(Scanner input, String currentOutletId, String employeeName) {
        //System.out.println("\n=== Stock In ===");
        
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dateFT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFT = DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH);
        
        String dateStr = now.format(dateFT);
        String timeStr = now.format(timeFT).toLowerCase().replace("am", "a.m.").replace("pm", "p.m.");
        
        //System.out.println("Date: " + dateStr);
        //System.out.println("Time: " + timeStr);
        JOptionPane.showMessageDialog(null, "Date: " + dateStr + "\nTime: " + timeStr, null, JOptionPane.INFORMATION_MESSAGE);
        
        // Select source outlet (excluding current outlet)
        //System.out.println("\nAvailable Sources:");
        String outputstr = "Available Sources: ";
        List<String> outletIdList = new ArrayList<>();
        int idx = 1;
        for (String id : outlets.keySet()) {
            if (!id.equals(currentOutletId)) {
                Outlet o = outlets.get(id);
                //System.out.println(idx + ". " + id + " (" + o.getOutletName() + ")");
                outputstr += "\n" + idx + ". " + id + " (" + o.getOutletName() + ")";
                outletIdList.add(id);
                idx++;
            }
        }
        //System.out.println(idx + ". HQ (Service Center)");
        outputstr += "\n" + idx + ". HQ (Service Center)";
        outletIdList.add("HQ");
        
        //System.out.print("Select source (From): ");
        outputstr += "\n" + "Select source (From): ";
        //int fromChoice = input.nextInt();
        int fromChoice = Integer.parseInt(Methods.showInputDialog(outputstr));
        //input.nextLine(); obsolete
        
        if (fromChoice < 1 || fromChoice > outletIdList.size()) {
            //System.out.println("Invalid selection.");
            JOptionPane.showMessageDialog(null, "Invalid Selection.", null, JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String fromOutletId = outletIdList.get(fromChoice - 1);
        String fromOutletName = fromOutletId.equals("HQ") ? "Service Center" : outlets.get(fromOutletId).getOutletName();
        
        Outlet currentOutlet = outlets.get(currentOutletId);
        String toOutletName = currentOutlet.getOutletName();
        
        //System.out.println("From: " + fromOutletId + " (" + fromOutletName + ")");
        //System.out.println("To: " + currentOutletId + " (" + toOutletName + ")");
        outputstr = "From: " + fromOutletId + " (" + fromOutletName + ")" +
                    "\nTo: " + currentOutletId + " (" + toOutletName + ")";
        
        // Collect models and quantities
        Map<String, Integer> modelsReceived = new LinkedHashMap<>();
        int totalQuantity = 0;
        
        while (true) {
            //System.out.print("\nEnter Model Name (or 'done' to finish): ");
            //String modelName = input.next();
            String modelName = Methods.showInputDialog(outputstr + "\nEnter Model Name (or 'done' to finish): ");
            
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
                //System.out.println("Model not found. Please try again.");
                JOptionPane.showMessageDialog(null, "Model not found. Please try again.", null, JOptionPane.WARNING_MESSAGE);
                continue;
            }

            outputstr = ""; //reset outputstr to use in another JOptionPane
            // Check available stock from source (skip for HQ - unlimited supply)
            int availableStock = -1;
            if (!fromOutletId.equals("HQ")) {
                availableStock = foundModel.getStock(fromOutletId);
                //System.out.println("Available stock at source: " + availableStock);
                outputstr += "Available stock at source: " + availableStock;
            } else {
                //System.out.println("Source: HQ (unlimited supply)");
                outputstr += "Source: HQ (unlimited supply)";
            }
            
            /* System.out.print("Enter Quantity: ");
            int quantity = input.nextInt();
            input.nextLine(); */
            int quantity = Integer.parseInt(Methods.showInputDialog(outputstr + "\nEnter Quantity: "));
            
            if (quantity <= 0) {
                //System.out.println("Invalid quantity.");
                JOptionPane.showMessageDialog(null, "Invalid quantity.", null, JOptionPane.WARNING_MESSAGE);
                continue;
            }
            
            // Check if source has enough stock (skip for HQ)
            if (!fromOutletId.equals("HQ") && quantity > availableStock) {
                //System.out.println("Insufficient stock at source. Only " + availableStock + " available.");
                JOptionPane.showMessageDialog(null, "Insufficient stock at source. Only " + availableStock + " available.", null, JOptionPane.WARNING_MESSAGE);
                continue;
            }
            
            modelsReceived.put(foundModel.getModelId(), modelsReceived.getOrDefault(foundModel.getModelId(), 0) + quantity);
            totalQuantity += quantity;
        }
        
        if (modelsReceived.isEmpty()) {
            //System.out.println("No models added. Stock In cancelled.");
            JOptionPane.showMessageDialog(null, "No models added. Stock In cancelled.", null, JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Update model quantities - decrease from source (if not HQ), increase in current outlet
        if (!fromOutletId.equals("HQ")) {
            updateModelQuantities(modelsReceived, fromOutletId, false);
        }
        updateModelQuantities(modelsReceived, currentOutletId, true);
        
        // Reload models to reflect changes
        outputstr = ""; //reset outputstr
        loadModels();
        
        //System.out.println("\nModels Received:");
        for (Map.Entry<String, Integer> entry : modelsReceived.entrySet()) {
            //System.out.println("- " + entry.getKey() + " (Quantity: " + entry.getValue() + ")");
            outputstr += "\n"+"- " + entry.getKey() + " (Quantity: " + entry.getValue() + ")";
        }
        //System.out.println("Total Quantity: " + totalQuantity);
        //System.out.println("\u001B[32mModel quantities updated successfully.\u001B[0m");
        //System.out.println("\u001B[32mStock In recorded.\u001B[0m");

        // Generate receipt
        String receiptFile = Methods.generateReceipt("Stock In", dateStr, timeStr, fromOutletId, fromOutletName, 
                                              currentOutletId, toOutletName, modelsReceived, totalQuantity, employeeName);
        //System.out.println("Receipt generated: " + receiptFile);
        JOptionPane.showMessageDialog(null,
                "Models Received: "
                        + outputstr
                        + "\nTotal Quantity: " + totalQuantity
                        + "\nModel quantities updated successfully."
                        + "\nStock In recorded."
                        + "\nReceipt generated: " + receiptFile, null, JOptionPane.INFORMATION_MESSAGE);
    }

    public void stockOut(Scanner input, String currentOutletId, String employeeName) {
        //System.out.println("\n=== Stock Out ===");
        
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dateFT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFT = DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH);
        
        String dateStr = now.format(dateFT);
        String timeStr = now.format(timeFT).toLowerCase().replace("am", "a.m.").replace("pm", "p.m.");
        
        //System.out.println("Date: " + dateStr);
        //System.out.println("Time: " + timeStr);
        JOptionPane.showMessageDialog(null, "Date: " + dateStr + "\nTime: " + timeStr, null, JOptionPane.INFORMATION_MESSAGE);
        
        Outlet currentOutlet = outlets.get(currentOutletId);
        String fromOutletName = currentOutlet.getOutletName();
        
        // Select destination outlet (including HQ)
        //System.out.println("\nAvailable Destinations:");
        String outputstr = "\nAvailable Destinations:";
        List<String> outletIdList = new ArrayList<>();
        int idx = 1;
        for (String id : outlets.keySet()) {
            if (!id.equals(currentOutletId)) {
                Outlet o = outlets.get(id);
                //System.out.println(idx + ". " + id + " (" + o.getOutletName() + ")");
                outputstr += "\n" + idx + ". " + id + " (" + o.getOutletName() + ")";
                outletIdList.add(id);
                idx++;
            }
        }
        //System.out.println(idx + ". HQ (Service Center)");
        outputstr += "\n" + idx + ". HQ (Service Center)";
        outletIdList.add("HQ");
        
        //System.out.print("Select destination (To): ");
        //int toChoice = input.nextInt();
        //input.nextLine();
        outputstr += "\nSelect destination (To): ";
        int toChoice = Integer.parseInt(Methods.showInputDialog(outputstr));
        
        if (toChoice < 1 || toChoice > outletIdList.size()) {
            //System.out.println("Invalid selection.");
            JOptionPane.showMessageDialog(null, "Invalid selection.", null, JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String toOutletId = outletIdList.get(toChoice - 1);
        String toOutletName = toOutletId.equals("HQ") ? "Service Center" : outlets.get(toOutletId).getOutletName();
        
        //System.out.println("From: " + currentOutletId + " (" + fromOutletName + ")");
        //System.out.println("To: " + toOutletId + " (" + toOutletName + ")");
        
        // Collect models and quantities
        Map<String, Integer> modelsTransferred = new LinkedHashMap<>();
        int totalQuantity = 0;
        
        while (true) {
            //System.out.print("\nEnter Model Name (or 'done' to finish): ");
            //String modelName = input.next();
            String modelName = Methods.showInputDialog(
                    "From: " + currentOutletId + " (" + fromOutletName + ")"
                    + "\nTo: " + toOutletId + " (" + toOutletName + ")"
                    + "\nEnter Model Name (or 'done' to finish): ");
            
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
                //System.out.println("Model not found. Please try again.");
                JOptionPane.showMessageDialog(null, "Model not found. Please try again.", null, JOptionPane.WARNING_MESSAGE);
                continue;
            }
            
            // Check available stock
            int availableStock = foundModel.getStock(currentOutletId);
            //System.out.println("Available stock: " + availableStock);
            
            //System.out.print("Enter Quantity: ");
            //int quantity = input.nextInt();
            //input.nextLine();
            int quantity = Integer.parseInt(Methods.showInputDialog("Available stock: " + availableStock + "\nEnter Quantity: "));
            
            if (quantity <= 0) {
                //System.out.println("Invalid quantity.");
                JOptionPane.showMessageDialog(null, "Invalid quantity.", null, JOptionPane.WARNING_MESSAGE);
                continue;
            }
            
            if (quantity > availableStock) {
                //System.out.println("Insufficient stock. Only " + availableStock + " available.");
                JOptionPane.showMessageDialog(null, "Insufficient stock. Only " + availableStock + " available.", null, JOptionPane.WARNING_MESSAGE);
                continue;
            }
            
            modelsTransferred.put(foundModel.getModelId(), modelsTransferred.getOrDefault(foundModel.getModelId(), 0) + quantity);
            totalQuantity += quantity;
        }
        
        if (modelsTransferred.isEmpty()) {
            //System.out.println("No models added. Stock Out cancelled.");
            JOptionPane.showMessageDialog(null, "No models added. Stock Out cancelled.", null, JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Update model quantities - decrease from current outlet, only increase in destination if not HQ
        updateModelQuantities(modelsTransferred, currentOutletId, false);
        if (!toOutletId.equals("HQ")) {
            updateModelQuantities(modelsTransferred, toOutletId, true);
        }
        
        // Reload models to reflect changes
        loadModels();
        outputstr = "Models Transferred:"; //reset outputstr to use in next JOptionPane
        //System.out.println("\nModels Transferred:");
        for (Map.Entry<String, Integer> entry : modelsTransferred.entrySet()) {
            //System.out.println("- " + entry.getKey() + " (Quantity: " + entry.getValue() + ")");
            outputstr += "\n" + "- " + entry.getKey() + " (Quantity: " + entry.getValue() + ")";
        }
        //System.out.println("Total Quantity: " + totalQuantity);
        //System.out.println("\u001B[32mModel quantities updated successfully.\u001B[0m");
        //System.out.println("\u001B[32mStock Out recorded.\u001B[0m");
        
        // Generate receipt
        String receiptFile = Methods.generateReceipt("Stock Out", dateStr, timeStr, currentOutletId, fromOutletName, 
                                              toOutletId, toOutletName, modelsTransferred, totalQuantity, employeeName);
        //System.out.println("Receipt generated: " + receiptFile);
        JOptionPane.showMessageDialog(null,
                "Total Quantity: " + totalQuantity
                + "\nModel quantities updated successfully."
                + "\nStock Out recorded."
                + "\nReceipt generated: " + receiptFile);
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
}
