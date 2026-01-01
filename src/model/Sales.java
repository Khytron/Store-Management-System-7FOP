package model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Sales {
    // Attributes
    private String saleId;
    private String employeeId;
    private String outletCode;
    private String modelName;
    private String modelQuantity;
    private String customerName;
    private String transactionMethod;
    private String totalPrice;
    private String date;
    private String time;

    // Constructor - auto-generates date and time
    public Sales(String saleId, String employeeId, String outletCode, String modelName, 
                 String modelQuantity, String customerName, String transactionMethod, 
                 String totalPrice) {
        this.saleId = saleId;
        this.employeeId = employeeId;
        this.outletCode = outletCode;
        this.modelName = modelName;
        this.modelQuantity = modelQuantity;
        this.customerName = customerName;
        this.transactionMethod = transactionMethod;
        this.totalPrice = totalPrice;
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
    public String getSaleId() {
        return this.saleId;
    }

    public String getEmployeeId() {
        return this.employeeId;
    }

    public String getOutletCode() {
        return this.outletCode;
    }

    public String getModelName() {
        return this.modelName;
    }

    public String getModelQuantity() {
        return this.modelQuantity;
    }

    public String getCustomerName() {
        return this.customerName;
    }

    public String getTransactionMethod() {
        return this.transactionMethod;
    }

    public String getTotalPrice() {
        return this.totalPrice;
    }

    public String getDate() {
        return this.date;
    }

    public String getTime() {
        return this.time;
    }
}
