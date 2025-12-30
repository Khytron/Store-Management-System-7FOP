package model;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.LinkedHashMap;

public class outletName {
    private LinkedHashMap<String, String> outletId = new LinkedHashMap<>();

    public outletName(){
        try(BufferedReader br = new BufferedReader(new FileReader("csv_database//outlet.csv"))){
            String line = br.readLine();
            line = br.readLine();
            while(line!=null){
                String [] arr = line.split(",");
                this.outletId.put(arr[0], arr[1]);
                line = br.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

    public String getOutletLocation(String id){
        return this.outletId.get(id);
    }
    
}
