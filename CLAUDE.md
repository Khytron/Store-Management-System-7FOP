# Store Management System - Project Overview

A Java-based console application for managing retail store operations across multiple outlets, developed by Group 7 for FOP (Fundamentals of Programming).

## Quick Start
```bash
# From src folder:
javac StoreManagementApp.java model/*.java service/*.java util/*.java
java StoreManagementApp

# Default login: Check csv_database/employee.csv for credentials
# Example: C6001 / a2b1c0 (Manager) or C6013 / abracadabra (Owner)
```

---

## Project Structure

```
Store-Management-System-7FOP/
├── src/                          # Java source code
│   ├── StoreManagementApp.java   # Main entry point - handles menu flow
│   ├── model/                    # Data models (POJOs)
│   │   ├── User.java             # Base user with login/logout, isEmployee/isEmployer flags
│   │   ├── Employee.java         # Employee data (id, name, role, password)
│   │   ├── Employer.java         # Employer data + static list of employer IDs
│   │   ├── Model.java            # Product model with price and stock per outlet (HashMap)
│   │   ├── Outlet.java           # Outlet info (id, name)
│   │   ├── Sales.java            # Sales transaction record
│   │   └── Attendance.java       # Clock in/out record with auto-generated date/time
│   ├── service/                  # Business logic layer
│   │   ├── UserManager.java      # Singleton - login, logout, employee registration
│   │   ├── StockManager.java     # Stock search, count, stock in/out operations
│   │   ├── SalesManager.java     # Record sales, search sales, update stock after sale
│   │   └── AttendanceManager.java # Clock in/out, view attendance records
│   └── util/                     # Utilities
│       ├── FilePath.java         # Constants for CSV file paths
│       └── Methods.java          # CSV reading, receipt generation, helper methods
├── csv_database/                 # CSV data storage (persistent data)
│   ├── employee.csv              # EmployeeID,EmployeeName,Role,Password
│   ├── outlet.csv                # OutletId,OutletName (C60-C69, 10 outlets)
│   ├── model.csv                 # Model,Price,C60,C61,...,C69 (stock per outlet)
│   ├── sales.csv                 # Sales transactions with customer info
│   └── attendance.csv            # Clock in/out records
├── receipts/                     # Auto-generated transaction receipts (.txt files)
└── README.txt                    # Original project documentation
```

---

## Core Concepts

### User Roles
- **Employer/Manager/Owner**: Can register new employees + all employee features
- **Employee (Full-time/Part-time)**: Standard operations only

Role detection: `Methods.isEmployerRole()` checks if role is "Employer", "Manager", or "Owner"

### User ID Format
- Format: `C60XX` where `C60` = outlet code, `XX` = employee number
- User's outlet is determined by first 3 characters of their ID

### Outlets
- 10 outlets: C60 (KL City Centre) to C69 (MyTown)
- Plus "HQ" (Service Center) for unlimited supply in Stock In operations

---

## Key Features

### 1. Login System (UserManager - Singleton)
- Validates credentials against employee.csv
- Creates User object with isEmployer/isEmployee flags
- Type "exit" as ID or password to terminate program

### 2. Employee Registration (Employer only)
- Appends new employee to employee.csv

### 3. Stock Search (StockManager)
- Search by model name (case-insensitive)
- Displays price and stock levels across all outlets

### 4. Stock Count
- Iterate through all models for current outlet
- Compare counted vs recorded stock
- Reports mismatches with unit difference

### 5. Stock In/Out (StockManager)
- Stock In: Receive from other outlet or HQ → increases current outlet stock
- Stock Out: Transfer to other outlet or HQ → decreases current outlet stock
- Updates model.csv directly
- Generates receipt in receipts/ folder

### 6. Sales (SalesManager)
- Record sales with customer info, items, quantities
- Validates stock availability before sale
- Updates model.csv (decreases stock)
- Generates sale receipt

### 7. Attendance (AttendanceManager)
- Clock In/Out with validation (can't clock in twice without clocking out)
- Calculates total hours worked
- View by employee ID or today's outlet attendance

### 8. Edit Information (EditManager)
- Edit Stock: Update model stock levels for current outlet
- Edit Sales: Search by date + customer name, edit any field (name, model, quantity, total, transaction method)
- Updates respective CSV files directly

---

## Data Files

### employee.csv
```
EmployeeID,EmployeeName,Role,Password
C6001,Tan Guan Han,Manager,a2b1c0
```

### model.csv
```
Model,Price,C60,C61,C62,C63,C64,C65,C66,C67,C68,C69
DW2300-1,399,12,4,3,1,3,3,2,2,2,4
```
- Columns after Price are stock quantities per outlet

### sales.csv
```
SaleID,EmployeeID,ModelName,ModelQuantity,CustomerName,TransactionMethod,TotalPrice,Date,Time
S01,C6002,SW2500-1,1,Che Ku,Cash,845,07-01-26,10:53 p.m.
```
- Multiple models in one sale separated by semicolons in ModelName

---

## Code Patterns

### Singleton Pattern
`UserManager.getInstance()` - ensures single instance manages all user operations

### CSV as Database
All data persisted in CSV files under `csv_database/`. Read with `Methods.readCsvFile()`, write with PrintWriter/FileWriter.

### ANSI Colors
- Green (`\u001B[32m`): Success messages
- Red (`\u001B[31m`): Errors, warnings, logout

---

## Menu Structure

### Employer Menu (11 options)
1. Register New Employee
2. Search Stock Information
3. Search Sales Information
4. Record New Sale
5. Perform Stock Count
6. Stock In
7. Stock Out
8. Edit Information
9. Clock In/Clock Out
10. View Attendance
11. Logout

### Employee Menu (9 options)
1. Search Stock Information
2. Search Sales Information
3. Record New Sale
4. Perform Stock Count
5. Stock In
6. Stock Out
7. Edit Information
8. Clock In/Clock Out
9. Logout

---

## Dependencies
- Pure Java (no external libraries)
- Java 8+ (uses LocalDateTime, DateTimeFormatter)

---

## Development Notes

**IMPORTANT**: Always remove `.class` files after compiling. They should not be committed to git.
```powershell
# Remove all .class files
Get-ChildItem -Recurse -Filter "*.class" | Remove-Item -Force
```
