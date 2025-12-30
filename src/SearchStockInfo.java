import java.util.Scanner;

import service.StockInfo;

public class SearchStockInfo {
    static Scanner input = new Scanner (System.in);

    public static void main(String [] args){
        System.out.println("=== Search Stock Information ===");
        System.out.print("Search Model Name: ");
        String search = input.next();
        new StockInfo(search);
        System.out.println("Searching...\n");

        if(StockInfo.isFound(search)){
            StockInfo.result();
        }else{
            System.out.println("Model " + search + " is not found in the database.");
        }
        input.close();
    }
}
