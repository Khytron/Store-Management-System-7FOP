# Store Management System
A Java-based store operation management system developed by Group 7.

## Features
- Login system with role-based access control (Manager/Owner vs Full-time/Part-time)
- Employee registration (Manager/Owner only)
- Search stock information across all outlets
- Perform stock count with mismatch detection
- Stock In - receive inventory from other outlets or HQ
- Stock Out - transfer inventory to other outlets or HQ
- Auto-generated receipts for stock transactions

## Project Structure
```
Store-Management-System-7FOP/
├── src/
│   ├── model/          # Data models
│   │   ├── User.java           # Base user class
│   │   ├── Employee.java       # Employee model
│   │   ├── Employer.java       # Employer model
│   │   ├── Model.java          # Product model with stock tracking
│   │   ├── Outlet.java         # Store outlet model
│   │   ├── Sales.java          # Sales transaction model
│   │   └── Attendance.java     # Attendance tracking model
│   ├── service/        # Business logic
│   │   ├── UserManager.java    # Login, logout, employee registration
│   │   └── StockManager.java   # Stock search, count, in/out operations
│   ├── util/           # Utility classes
│   │   ├── FilePath.java       # CSV file path constants
│   │   └── Methods.java        # CSV reading utilities
│   └── StoreManagementApp.java # Main entry point
├── csv_database/       # CSV data storage
│   ├── employee.csv    # Employee credentials and info
│   ├── attendance.csv  # Attendance records
│   ├── outlet.csv      # Outlet information (C60-C69)
│   ├── model.csv       # Product models with stock per outlet
│   └── sales.csv       # Sales transactions
├── receipts/           # Auto-generated transaction receipts
└── README.txt
```

## Menu Options

### Manager/Owner Menu
1. Register New Employee
2. Search Stock Info
3. Perform Stock Count
4. Stock In
5. Stock Out
6. Logout

### Full-time/Part-time Employee Menu
1. Search Stock Info
2. Perform Stock Count
3. Stock In
4. Stock Out
5. Logout

## How to Run
1. Navigate to the `src` folder
2. Compile: `javac -d ../out StoreManagementApp.java model/*.java service/*.java util/*.java`
3. Navigate to project root and run: `java -cp out StoreManagementApp`

Or simply run from `src` folder:
```
javac StoreManagementApp.java model/*.java service/*.java util/*.java
java StoreManagementApp
```

## Default Login
Check `csv_database/employee.csv` for available user credentials.
Type "exit" as User ID or Password to terminate the program.

## Git Commands Reference
```
git status                          # Check repo status
git add .                           # Stage all changes
git commit -m "your message"        # Commit changes
git push                            # Push to remote
git pull                            # Pull latest changes
```

## Contributors
- Group 7 Members
