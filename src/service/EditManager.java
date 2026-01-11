package service;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import util.FilePath;
import util.Methods;

import javax.swing.*;

public class EditManager {

    public EditManager() {
    }

    public void editStockInfo(Scanner input, String currentOutletId) {
        /* System.out.println("\n=== Edit Stock Information ===");
        System.out.print("Enter Model Name: ");
        String modelName = input.next();
        input.nextLine(); */
        String modelName = Methods.showInputDialog("\nEnter Model Name: ", "Edit Stock Information");

        List<List<String>> modelData = Methods.readCsvFile(FilePath.modelDataPath);
        if (modelData.isEmpty()) {
            //System.out.println("No model data found.");
            JOptionPane.showMessageDialog(null, "No model data found", null, JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<String> header = modelData.get(0);
        
        // Find outlet column index
        int outletIndex = -1;
        for (int i = 0; i < header.size(); i++) {
            if (header.get(i).equals(currentOutletId)) {
                outletIndex = i;
                break;
            }
        }

        if (outletIndex == -1) {
            //System.out.println("Outlet not found in database.");
            JOptionPane.showMessageDialog(null, "Outlet not found in database.", null, JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Find model row
        int modelRowIndex = -1;
        for (int i = 1; i < modelData.size(); i++) {
            if (modelData.get(i).get(0).equalsIgnoreCase(modelName)) {
                modelRowIndex = i;
                break;
            }
        }

        if (modelRowIndex == -1) {
            //System.out.println("Model " + modelName + " not found in database.");
            JOptionPane.showMessageDialog(null, "Model " + modelName + " not found in database.", null, JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<String> modelRow = modelData.get(modelRowIndex);
        int currentStock = Integer.parseInt(modelRow.get(outletIndex));
        //System.out.println("Current Stock: " + currentStock);

        //System.out.print("Enter New Stock Value: ");
        int newStock;
        try {
            //newStock = Integer.parseInt(input.nextLine());
            newStock = Integer.parseInt(Methods.showInputDialog("Current Stock: " + currentStock + "\nEnter New Stock Value: "));
        } catch (NumberFormatException e) {
            //System.out.println("Invalid stock value.");
            JOptionPane.showMessageDialog(null, "Invalid stock value.", null, JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (newStock < 0) {
            //System.out.println("Stock value cannot be negative.");
            JOptionPane.showMessageDialog(null, "Stock value cannot be negative.", null, JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Update the stock value
        List<String> updatedRow = new ArrayList<>(modelRow);
        updatedRow.set(outletIndex, String.valueOf(newStock));
        modelData.set(modelRowIndex, updatedRow);

        // Write back to CSV
        try (PrintWriter writer = new PrintWriter(new FileWriter(FilePath.modelDataPath))) {
            for (List<String> row : modelData) {
                writer.println(String.join(",", row));
            }
            //System.out.println("\u001B[32mStock information updated successfully.\u001B[0m");
            JOptionPane.showMessageDialog(null, "Stock information updated successfully.", null, JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            //System.out.println("Error updating stock: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Error updating stock: " + e.getMessage(), null, JOptionPane.WARNING_MESSAGE);
        }
    }

    public void editSalesInfo(Scanner input) {
        //System.out.println("\n=== Edit Sales Information ===");
        //System.out.print("Enter Transaction Date (dd-MM-yy): ");
        //String searchDate = input.nextLine();
        String searchDate = Methods.showInputDialog("Enter Transaction Date (dd-MM-yy): ", "Edit Sales Information");
        //System.out.print("Enter Customer Name: ");
        //String searchCustomer = input.nextLine();
        String searchCustomer = Methods.showInputDialog("Enter Customer Name: ");

        List<List<String>> salesData = Methods.readCsvFile(FilePath.salesDataPath);
        if (salesData.isEmpty()) {
            //System.out.println("No sales data found.");
            JOptionPane.showMessageDialog(null, "No sales data found.", null, JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Find matching sale record
        int saleRowIndex = -1;
        for (int i = 1; i < salesData.size(); i++) {
            List<String> row = salesData.get(i);
            if (row.size() >= 9) {
                String date = row.get(7);
                String customer = row.get(4);
                if (date.equals(searchDate) && customer.equalsIgnoreCase(searchCustomer)) {
                    saleRowIndex = i;
                    break;
                }
            }
        }

        if (saleRowIndex == -1) {
            //System.out.println("No matching sales record found.");
            JOptionPane.showMessageDialog(null, "No matching sales record found.", null, JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<String> saleRow = salesData.get(saleRowIndex);
        
        // Display found record
        // Format: SaleID,EmployeeID,ModelName,ModelQuantity,CustomerName,TransactionMethod,TotalPrice,Date,Time
        /*
        System.out.println("\nSales Record Found:");
        System.out.println("Model: " + saleRow.get(2) + " Quantity: " + saleRow.get(3));
        System.out.println("Total: RM" + saleRow.get(6));
        System.out.println("Transaction Method: " + saleRow.get(5));

        System.out.println("\nSelect number to edit:");
        System.out.println("1. Name  2. Model  3. Quantity  4. Total");
        System.out.println("5. Transaction Method");
        System.out.print("> ");
        
        String choice = input.nextLine();
         */
        String choice = Methods.showInputDialog(
                "Sales Record Found:"
                + "\nModel: " + saleRow.get(2)
                + "\nQuantity: " + saleRow.get(3)
                + "\nTotal: RM" + saleRow.get(6)
                + "\nTransaction Method: " + saleRow.get(5)
                + "\n\nSelect number to edit:"
                + "\n1. Name  \n2. Model  \n3. Quantity  \n4. Total"
                + "\n5. Transaction Method"
        );
        String newValue = "";
        int fieldIndex = -1;

        switch (choice) {
            case "1":
                //System.out.print("Enter New Customer Name: ");
                //newValue = input.nextLine();
                newValue = Methods.showInputDialog("Enter New Customer Name: ");
                fieldIndex = 4;
                break;
            case "2":
                //System.out.print("Enter New Model: ");
                //newValue = input.nextLine();
                newValue = Methods.showInputDialog("Enter New Model: ");
                fieldIndex = 2;
                break;
            case "3":
                //System.out.print("Enter New Quantity: ");
                //newValue = input.nextLine();
                newValue = Methods.showInputDialog("Enter New Quantity: ");
                try {
                    Integer.parseInt(newValue);
                } catch (NumberFormatException e) {
                    //System.out.println("Invalid quantity.");
                    JOptionPane.showMessageDialog(null, "Invalid quantity.", null, JOptionPane.WARNING_MESSAGE);
                    return;
                }
                fieldIndex = 3;
                break;
            case "4":
                //System.out.print("Enter New Total: ");
                //newValue = input.nextLine();
                newValue = Methods.showInputDialog("Enter New Total");
                try {
                    Integer.parseInt(newValue);
                } catch (NumberFormatException e) {
                    //System.out.println("Invalid total.");
                    JOptionPane.showMessageDialog(null, "Invalid total.", null, JOptionPane.WARNING_MESSAGE);
                    return;
                }
                fieldIndex = 6;
                break;
            case "5":
                //System.out.print("Enter New Transaction Method: ");
                //newValue = input.nextLine();
                newValue = Methods.showInputDialog("Enter New Transaction Method: ");
                fieldIndex = 5;
                break;
            default:
                //System.out.println("Invalid selection.");
                JOptionPane.showMessageDialog(null, "Invalid selection.", null, JOptionPane.WARNING_MESSAGE);
                return;
        }

        //System.out.print("Confirm Update? (Y/N): ");
        //String confirm = input.nextLine();
        String confirm = Methods.showInputDialog("Confirm Update? (Y/N): ");

        if (!confirm.equalsIgnoreCase("Y")) {
            //System.out.println("Update cancelled.");
            JOptionPane.showMessageDialog(null, "Update cancelled.", null, JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Update the field
        List<String> updatedRow = new ArrayList<>(saleRow);
        updatedRow.set(fieldIndex, newValue);
        salesData.set(saleRowIndex, updatedRow);

        // Write back to CSV
        try (PrintWriter writer = new PrintWriter(new FileWriter(FilePath.salesDataPath))) {
            for (List<String> row : salesData) {
                writer.println(String.join(",", row));
            }
            //System.out.println("\u001B[32mSales information updated successfully.\u001B[0m");
            JOptionPane.showMessageDialog(null, "Sales information updated successfully.", null, JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            //System.out.println("Error updating sales: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Error updating sales: " + e.getMessage(), null, JOptionPane.WARNING_MESSAGE);
        }
    }
}
