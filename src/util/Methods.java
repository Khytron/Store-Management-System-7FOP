package util;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Methods {
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