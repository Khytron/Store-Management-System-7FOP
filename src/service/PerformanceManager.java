package service;

import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import util.FilePath;
import util.Methods;

import javax.swing.*;

// Calculates and displays employee performance metrics based on sales data.
// Generates rankings and exportable reports.
public class PerformanceManager {

    public PerformanceManager() {
    }

    public void viewPerformanceMetrics(Scanner input) {
        String startDate = Methods.showInputDialog("Enter Start Date (dd-MM-yy): ", "Employee Performance Metrics");
        String endDate = Methods.showInputDialog("Enter End Date (dd-MM-yy): ", "Employee Performance Metrics");

        // Calculate performance from sales data
        Map<String, int[]> performanceMap = calculatePerformance(startDate, endDate);

        if (performanceMap.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No sales records found for the specified period.", null, JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Get employee names
        Map<String, String> employeeNames = loadEmployeeNames();

        // Convert to list for sorting
        List<Map.Entry<String, int[]>> performanceList = new ArrayList<>(performanceMap.entrySet());

        // Sort by total sales (descending)
        performanceList.sort((a, b) -> Integer.compare(b.getValue()[0], a.getValue()[0]));

        // Display results
        String outputstr = "<html><pre>Performance Report (" + startDate + " to " + endDate + ")"
                + "<br>----------------------------------------------------";
        outputstr += String.format("<br>%-5s %-25s %-15s %-15s\n", "Rank", "Employee Name", "Total Sales", "Transactions");
        outputstr += "<br>----------------------------------------------------";

        int rank = 1;
        for (Map.Entry<String, int[]> entry : performanceList) {
            String employeeId = entry.getKey();
            int totalSales = entry.getValue()[0];
            int transactionCount = entry.getValue()[1];
            String employeeName = employeeNames.getOrDefault(employeeId, employeeId);

            outputstr += String.format("<br>%-5d %-25s RM%-13d %-15d\n", rank, employeeName, totalSales, transactionCount);
            rank++;
        }

        //System.out.println("----------------------------------------------------");
        outputstr += "<br>----------------------------------------------------";

        // Save to CSV
        savePerformanceToCSV(performanceList, employeeNames);
        outputstr += "<br>Performance data saved to employee-performance-metrics.csv</pre></html>";

        //Set font to a monospace font
        Font monospacefont = new Font("Monospaced", Font.PLAIN, 12);
        JLabel label = new JLabel(outputstr);
        label.setFont(monospacefont);

        JOptionPane.showMessageDialog(null, label, "receipt", JOptionPane.INFORMATION_MESSAGE);
    }

    private Map<String, int[]> calculatePerformance(String startDate, String endDate) {
        Map<String, int[]> performanceMap = new HashMap<>();

        List<List<String>> salesData = Methods.readCsvFile(FilePath.salesDataPath);

        // Skip header
        for (int i = 1; i < salesData.size(); i++) {
            List<String> row = salesData.get(i);
            if (row.size() >= 9) {
                String employeeId = row.get(1);
                String saleDate = row.get(7);
                int totalPrice;

                try {
                    totalPrice = Integer.parseInt(row.get(6));
                } catch (NumberFormatException e) {
                    continue;
                }

                // Check if date is within range
                if (isDateInRange(saleDate, startDate, endDate)) {
                    int[] current = performanceMap.getOrDefault(employeeId, new int[]{0, 0});
                    current[0] += totalPrice;  // Total sales
                    current[1] += 1;           // Transaction count
                    performanceMap.put(employeeId, current);
                }
            }
        }

        return performanceMap;
    }

    private boolean isDateInRange(String date, String startDate, String endDate) {
        // Parse dates in dd-MM-yy format
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

    private Map<String, String> loadEmployeeNames() {
        Map<String, String> names = new HashMap<>();
        List<List<String>> employeeData = Methods.readCsvFile(FilePath.employeeDataPath);

        for (int i = 1; i < employeeData.size(); i++) {
            List<String> row = employeeData.get(i);
            if (row.size() >= 2) {
                names.put(row.get(0), row.get(1));
            }
        }

        return names;
    }

    private void savePerformanceToCSV(List<Map.Entry<String, int[]>> performanceList, Map<String, String> employeeNames) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FilePath.performanceDataPath))) {
            writer.println("EmployeeID,EmployeeName,TotalSalesAmount,NumberOfTransactions");

            for (Map.Entry<String, int[]> entry : performanceList) {
                String employeeId = entry.getKey();
                String employeeName = employeeNames.getOrDefault(employeeId, employeeId);
                int totalSales = entry.getValue()[0];
                int transactionCount = entry.getValue()[1];

                writer.println(employeeId + "," + employeeName + "," + totalSales + "," + transactionCount);
            }
        } catch (IOException e) {
            System.out.println("Error saving performance data: " + e.getMessage());
        }
    }
}
