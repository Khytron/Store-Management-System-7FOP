package model;
public class Employee {
    // Attributes
    private String employeeId;
    private String employeeName;
    private String employeeRole;
    private String employeePassword;

    public Employee(String employeeId, String employeeName, String employeeRole, String employeePassword){
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.employeeRole = employeeRole;
        this.employeePassword = employeePassword;
    }

    public String getEmployeeId(){
        return this.employeeId;
    }
    public String getEmployeeName(){
        return this.employeeName;
    }
    public String getEmployeeRole(){
        return this.employeeRole;
    }
    public String getEmployeePassword(){
        return this.employeePassword;
    }
}
