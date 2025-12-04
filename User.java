
public class User {
    //Attributes
    public String userId;
    public String userName;
    public String userRole;
    private String userPassword;
    public boolean isEmployee = false;
    public boolean isEmployer = false;
    public boolean isLogged = false;
    

    public User(String userId, String userName, String userRole, String userPassword){
        this.userId = userId;
        this.userName = userName; 
        this.userRole = userRole;
        this.userPassword = userPassword;
    }

    public void login(){
        if (!this.isLogged && this.userId != null) { 
            this.isLogged = true; 
            // Check if its an employee or employer
            if (Employer.employerIds.contains(this.userId))
                this.isEmployer = true;
            else
                this.isEmployee = true;
        }
    }

    public void logout(){
        if (this.isLogged && this.userId != null) { 
            this.isLogged = false; 
            this.userId = null;
            this.userName = null;
            this.userRole = null;
            this.userPassword = null;
            this.isEmployee = false;
            this.isEmployer = false;
            
        }
    }
}
