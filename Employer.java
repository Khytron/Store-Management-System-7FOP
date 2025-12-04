import java.util.ArrayList;
import java.util.List;

public class Employer {
    // Attributes
    private String employerId;
    private String employerName;
    private String employerRole;
    private String employerPassword;
    public static List<String> employerIds = new ArrayList<>();
    
    // Lists of employer ids
    static {
        employerIds.add("C6001");
        // add more here ....
    }

    public Employer(String employerId, String employerName, String employerRole, String employerPassword){
        this.employerId = employerId;
        this.employerName = employerName;
        this.employerRole = employerRole;
        this.employerPassword = employerPassword;


    }
}
