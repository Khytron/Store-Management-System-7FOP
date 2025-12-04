import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class methods {
    // Function to read csv file
    public static List<List<String>> readCsvFile(String path){
        String line;
        List<List<String>> FileData = new ArrayList<>();
    
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            while ((line = br.readLine()) != null ) {
                String[] data = line.split(",");

                FileData.add(Arrays.asList(data));
            }
        } catch (IOException e) {
            e.printStackTrace();    
        }

        return FileData;
    }
}