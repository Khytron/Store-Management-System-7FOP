package model;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import util.FilePath;

public class Employer {
    // Attributes
    private String employerId;
    private String employerName;
    private String employerRole;
    private String employerPassword;
    public static List<String> employerIds = new ArrayList<>();
    
    static {
        loadEmployerIds();
    }

    private static void loadEmployerIds() {
        try (BufferedReader br = new BufferedReader(new FileReader(FilePath.employeeDataPath))) {
            br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                String[] arr = line.split(",");
                String role = arr[2];
                if (role.equalsIgnoreCase("Employer") || role.equalsIgnoreCase("Manager") || role.equalsIgnoreCase("Owner")) {
                    employerIds.add(arr[0]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Employer(String employerId, String employerName, String employerRole, String employerPassword){
        this.employerId = employerId;
        this.employerName = employerName;
        this.employerRole = employerRole;
        this.employerPassword = employerPassword;

    }

    public String getEmployerId(){
        return this.employerId;
    }
    public String getEmployerName(){
        return this.employerName;
    }
    public String getEmployerRole(){
        return this.employerRole;
    }
    public String getEmployerPassword(){
        return this.employerPassword;
    }
}
