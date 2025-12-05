# Store Management System
A Java-based store operation management system developed by Group 7.

## Features
- Employee/Employer login system
- Employee registration (Employer only)
- Role-based access control

## Project Structure
```
Store-Management-System-7FOP/
├── src/
│   ├── model/          # Data models (User, Employee, Employer)
│   ├── service/        # Business logic (UserManager)
│   ├── util/           # Utility classes (FilePath, Methods)
│   └── StoreManagementApp.java  # Main entry point
├── csv_database/       # CSV data storage
│   ├── employee.csv
│   ├── attendance.csv
│   ├── outlet.csv
│   ├── model.csv
│   └── sales.csv
└── README.txt
```

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
