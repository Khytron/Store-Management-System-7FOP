package service;
import java.util.LinkedHashMap;

import model.outletName;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileReader;

public class StockInfo {
    private static String userSearch;
    private static int unitPrice;
    private static LinkedHashMap<String, Integer> outletStock = new LinkedHashMap<>();
    private static String [] outletIds;

    public StockInfo(String search){
        userSearch=search;
        outletStock.clear();
    }

    public static String getUserSearch(){
        return userSearch;
    }

    public static void getOutletId(){
        try(BufferedReader br = new BufferedReader(new FileReader("csv_database//model.csv"))){
            String line = br.readLine();
            String [] arr = line.split(",");
            outletIds = new String[arr.length-2];
            for(int i=2;i<arr.length;i++){
                outletIds[i-2]=arr[i];
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public static boolean isFound(String search){
        boolean isFound=false;
        try(BufferedReader br = new BufferedReader(new FileReader("csv_database//model.csv"))){
            String firstLine = br.readLine();
            String [] arr = firstLine.split(",");
            outletIds = new String[arr.length-2];
            for(int i=2;i<arr.length;i++){
                outletIds[i-2]=arr[i];
            }
            
            String line = br.readLine();
            while(line!=null){
                arr = line.split(",");
                if(arr[0].equalsIgnoreCase(search)){
                    unitPrice=Integer.parseInt(arr[1]);
                    for(int i=2;i<arr.length;i++){
                        if(Integer.parseInt(arr[i])!=0){
                            outletStock.put(outletIds[i-2], Integer.parseInt(arr[i]));
                        }
                    }
                    isFound=true;
                    break;
                }
                line = br.readLine();
            }
        }catch(IOException e){
            e.printStackTrace();
        }
        return isFound;
    }

    public static void result(){
        System.out.printf("Model: %s\n",userSearch);
        System.out.printf("Unit Price: RM%d\n",unitPrice);
        System.out.println("Stock by outlet: ");
        outletName outlet = new outletName();
        for(String key: outletStock.keySet()){
            System.out.printf("%s: %d \n", outlet.getOutletLocation(key), outletStock.get(key));
        }
    }    
}
