import java.util.Scanner;

import service.SalesInfo;

public class SearchSalesInfo {
    static Scanner input = new Scanner (System.in);

    public static void main(String [] args){
        System.out.println("=== Search Sales Information ===");
        System.out.print("Search keyword: ");
        String keyword = input.next();
        SalesInfo salesInfo = new SalesInfo(keyword);
        System.out.println("Searching...");

        if(salesInfo.findSales()) {
            salesInfo.dislpaySalesInfo();
        } else {
            System.out.println("No matching sales record found.");
        }
    }
}

