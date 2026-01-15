package util;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.BorderLayout;

// Utility class containing shared helper methods for GUI input, file I/O, receipt generation, and time calculations.
public class Methods {
    // Input dialog without Cancel button - returns empty string if closed
    public static String showInputDialog(String message) {
        return showInputDialog(message, "Input");
    }
    
    public static String showInputDialog(String message, String title) {
        JTextField textField = new JTextField(20);
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.add(new JLabel("<html>" + message.replace("\n", "<br>") + "</html>"), BorderLayout.NORTH);
        panel.add(textField, BorderLayout.CENTER);
        
        String[] options = {"OK"};
        int result = JOptionPane.showOptionDialog(null, panel, title,
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                null, options, options[0]);
        
        if (result == 0) {
            return textField.getText();
        }
        return ""; // Return empty string instead of null when closed
    }

    // Method to read csv file
    public static List<List<String>> readCsvFile(String path){
        String line;
        List<List<String>> fileData = new ArrayList<>();
    
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            while ((line = br.readLine()) != null ) {
                String[] data = line.split(",");

                fileData.add(Arrays.asList(data));
            }
        } catch (IOException e) {
            e.printStackTrace();    
        }

        return fileData;
    }

    public static boolean isEmployerRole(String role) {
        return role.equalsIgnoreCase("Employer") || 
               role.equalsIgnoreCase("Manager") || 
               role.equalsIgnoreCase("Owner");
    }

    public static String generateReceipt(String transactionType, String date, String time, 
                                         String fromId, String fromName, String toId, String toName,
                                         Map<String, Integer> modelItems, int totalQty, String employeeName) {
        String fileName = "stockmovements_" + date + ".txt";
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

    public static String generateSalesReceipt(String date, String time, String customerName,
                                              Map<String, int[]> items, String transactionMethod,
                                              int subtotal, String employeeName) {
        String fileName = "sales_" + date + ".txt";
        String filePath = FilePath.receiptsFolder + fileName;
        
        // Create receipts folder if it doesn't exist
        File folder = new File(FilePath.receiptsFolder);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        
        StringBuilder receipt = new StringBuilder();
        receipt.append("\n=== Sales Receipt ===\n");
        receipt.append("Date: ").append(date).append("\n");
        receipt.append("Time: ").append(time).append("\n");
        receipt.append("Customer Name: ").append(customerName).append("\n");
        receipt.append("Item(s) Purchased:\n");
        
        for (Map.Entry<String, int[]> entry : items.entrySet()) {
            String model = entry.getKey();
            int qty = entry.getValue()[0];
            int unitPrice = entry.getValue()[1];
            receipt.append("- ").append(model).append(" x").append(qty)
                   .append(" @ RM").append(unitPrice).append(" = RM").append(qty * unitPrice).append("\n");
        }
        
        receipt.append("Transaction Method: ").append(transactionMethod).append("\n");
        receipt.append("Subtotal: RM").append(subtotal).append("\n");
        receipt.append("Employee in Charge: ").append(employeeName).append("\n");
        
        // Append to file (same day sales go to same file)
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath, true))) {
            writer.print(receipt.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return fileName;
    }

    public static String timeDifference(String time1, String time2){
        // 1. Parse the strings into LocalTime objects
        // LocalTime.parse automatically understands "HH:mm" format
        LocalTime t1 = LocalTime.parse(time1);
        LocalTime t2 = LocalTime.parse(time2);

        // 2. Calculate the difference
        // We assume time2 is the start and time1 is the end (15:25 - 12:11)
        Duration diff = Duration.between(t2, t1);

        // 3. Convert difference to total minutes
        long minutes = diff.toMinutes();

        // 4. Convert minutes to decimal hours
        // We divide by 60.0 (double) to ensure we get decimals, not integers
        double hours = minutes / 60.0;

        // 5. Format to 1 decimal place
        String result = String.format("%.1f Hours", hours);

        return result;
    }
}