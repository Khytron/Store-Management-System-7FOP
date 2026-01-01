package model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Attendance {
    // Attributes
    private String date;
    private String time;
    private String employeeId;
    private String outletCode;
    private String status;

    // Constructor - auto-generates date and time
    public Attendance(String employeeId, String outletCode, String status) {
        this.employeeId = employeeId;
        this.outletCode = outletCode;
        this.status = status;
        this.date = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        this.time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    // Setters for loading from CSV
    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    // Getters
    public String getDate() {
        return this.date;
    }

    public String getTime() {
        return this.time;
    }

    public String getEmployeeId() {
        return this.employeeId;
    }

    public String getOutletCode() {
        return this.outletCode;
    }

    public String getStatus() {
        return this.status;
    }
}
