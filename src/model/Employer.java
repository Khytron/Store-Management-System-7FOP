package model;
import java.util.ArrayList;
import java.util.List;

import util.FilePath;
import util.Methods;

// Represents an employer/manager.
// Maintains a static list of all employer IDs to distinguish them from regular employees during login.
public class Employer {
    // Attributes
    private String employerId;
    private String employerName;
    private String employerRole;
    private String employerPassword;
    public static List<String> employerIds = new ArrayList<>();
    
    // Static block to load employer IDs once when the class is loaded
    static {
        loadEmployerIds();
    }

    private static void loadEmployerIds() {
        List<List<String>> employeeData = Methods.readCsvFile(FilePath.employeeDataPath);
        
        // Remove header
        if (!employeeData.isEmpty())
            employeeData.remove(0);
        
        employerIds.clear();
        
        for (List<String> employee : employeeData) {
            String id = employee.get(0);
            String role = employee.get(2);
            if (Methods.isEmployerRole(role)) {
                employerIds.add(id);
            }
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
