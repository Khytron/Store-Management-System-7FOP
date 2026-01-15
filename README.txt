# Store Management System
A Java-based store operation management system developed by Group 7 OCC1.

## Features
- **Role-Based Access Control:** Secure login system distinguishing between Employers (Managers/Owners) and Employees.
- **Inventory Management:** 
  - Search stock information across all outlets.
  - Perform physical stock counts with automated mismatch detection.
  - Stock In/Out: Transfer inventory between outlets or receive from HQ.
- **Sales Tracking:**
  - Record new sales transactions.
  - Search and view detailed sales history.
  - Advanced filtering and sorting of sales data by date, amount, and customer.
- **Employee Management:**
  - Register new employees (Employer only).
  - Track employee attendance (Clock In/Clock Out).
  - View attendance history for specific employees or today's outlet staff.
  - Performance Metrics: Analyze sales performance and generate rankings.
- **Data Integrity:**
  - Automated receipt generation for both sales and stock movements.
  - Persistent storage using CSV databases.
  - GUI-enhanced interaction using Java Swing (JOptionPane).

## Project Structure
```
Store-Management-System-7FOP/
├── src/
│   ├── model/                  # Data models
│   │   ├── User.java           # Base user class
│   │   ├── Employee.java       # Employee data model
│   │   ├── Employer.java       # Employer/Manager data model
│   │   ├── Model.java          # Product/Stock tracking model
│   │   ├── Outlet.java         # Store outlet model
│   │   ├── Sales.java          # Sales transaction model
│   │   └── Attendance.java     # Attendance record model
│   ├── service/                # Business logic
│   │   ├── UserManager.java    # Authentication & employee registration
│   │   ├── StockManager.java   # Inventory & stock operations
│   │   ├── SalesManager.java   # Sales recording & history analytics
│   │   ├── AttendanceManager.java # Clock In/Out & history viewing
│   │   ├── EditManager.java    # Data modification services
│   │   └── PerformanceManager.java # Performance metrics & rankings
│   ├── util/                   # Utility classes
│   │   ├── FilePath.java       # CSV file path constants
│   │   └── Methods.java        # Shared helper utilities
│   └── StoreManagementApp.java # Application entry point
├── csv_database/               # CSV persistent storage
│   ├── employee.csv            # Credentials and roles
│   ├── attendance.csv          # Attendance logs
│   ├── outlet.csv              # Outlet identification data
│   ├── model.csv               # Inventory levels and prices
│   ├── sales.csv               # Sales transaction logs
│   └── employee-performance-metrics.csv # Exported performance data
├── receipts/                   # Auto-generated transaction receipts (.txt)
└── README.txt
```

## Menu Options

### Manager/Owner Menu
1. Register New Employee
2. Search Stock Information
3. Search Sales Information
4. Filter/Sort Sales History
5. Record New Sale
6. Perform Stock Count
7. Stock In
8. Stock Out
9. Edit Information
10. Employee Performance Metrics
11. Clock In/Clock Out
12. View Attendance
13. Logout

### Full-time/Part-time Employee Menu
1. Search Stock Information
2. Search Sales Information
3. Filter/Sort Sales History
4. Record New Sale
5. Perform Stock Count
6. Stock In
7. Stock Out
8. Edit Information
9. Clock In/Clock Out
10. Logout

## How to Run
1. Ensure Java JDK is installed on your system.
2. Navigate to the `src` folder.
3. Compile the project:
   ```
   javac -d ../out StoreManagementApp.java model/*.java service/*.java util/*.java
   ```
4. Run the application from the project root:
   ```
   java -cp out StoreManagementApp
   ```

## Default Login
- Check `csv_database/employee.csv` for available user IDs and passwords.
- Type "exit" as User ID or Password during login to terminate the program.

## Contributors
- Group 7 Members (OCC1)