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
}
