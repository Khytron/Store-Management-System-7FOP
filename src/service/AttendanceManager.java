package service;

import model.Attendance;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import util.FilePath;
import util.Methods;

public class AttendanceManager {
        private List<Attendance> attendanceRecords;

        public AttendanceManager() {
            this.attendanceRecords = new ArrayList<>();
            loadAttendanceFromFile();
        }

        // Clock In
        public void clockIn(String employeeId, String outletCode) {
            // Check if employee already clocked in without clocking out
            if (hasOpenClockIn(employeeId)) {
                System.out.println("\nYou have already clocked in. Please clock out first.");
                return;
            }
            Attendance record = new Attendance(employeeId, outletCode, "Clock In");
            attendanceRecords.add(record);
            saveAttendanceToFile();
            System.out.println("\n=== Attendance Clock In ===");
            System.out.println("Employee ID: " + record.getEmployeeId());
            System.out.println("Outlet: " + record.getOutletCode());
            System.out.println("\nClock In \u001B[32mSuccessful\u001B[0m!\nDate: " + record.getDate()+ "\nTime: " + record.getTime());
        }

        // Clock Out
        public void clockOut(String employeeId, String outletCode) {
            // Check if employee has clocked in first
            if (!hasOpenClockIn(employeeId)) {
                System.out.println("\nYou haven't clocked in yet. Please clock in first.");
                return;
            }
            Attendance clockInRecord = getLatestClockIn(employeeId);
            Attendance record = new Attendance(employeeId, outletCode, "Clock Out");
            attendanceRecords.add(record);
            saveAttendanceToFile();
            System.out.println("\n=== Attendance Clock Out ===");
            System.out.println("Employee ID: " + record.getEmployeeId());
            System.out.println("Outlet: " + record.getOutletCode());
            System.out.println("\nClock Out \u001B[32mSuccessful\u001B[0m!\nDate: " + record.getDate()+ "\nTime: " + record.getTime());
            System.out.println("Total Hours Worked: " + Methods.timeDifference(record.getTime(),clockInRecord.getTime()));
        }

        // Check if employee has an open clock-in (clocked in but not out yet today)
        private boolean hasOpenClockIn(String employeeId) {
            String today = java.time.LocalDate.now()
                    .format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            
            for (int i = attendanceRecords.size()-1; i >= 0; i--){
                Attendance att = attendanceRecords.get(i);
                if (att.getEmployeeId().equals(employeeId) && att.getStatus().equals("Clock Out")){
                    return false;
                }
                if (att.getEmployeeId().equals(employeeId) && att.getStatus().equals("Clock In") && att.getDate().equals(today)){
                    return true;
                }
            }
            
            return false;
        }

        private Attendance getLatestClockIn(String employeeId){
            String today = java.time.LocalDate.now()
                    .format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy"));

            // Loop backwards through all attendance record
            for (int i = attendanceRecords.size()-1; i >= 0; i--){
                Attendance att = attendanceRecords.get(i);
                if (att.getEmployeeId().equals(employeeId) && att.getStatus().equals("Clock Out")){
                    throw new IllegalArgumentException("Error: Trying to access unavailable open clock in for employee " + employeeId);
                }
                if (att.getEmployeeId().equals(employeeId) && att.getStatus().equals("Clock In") && att.getDate().equals(today)){
                    return att;
                }
            }
            throw new IllegalAccessError("Trying to get latest open clock in record that doesn't exist");
        }

        // Save to CSV
        private void saveAttendanceToFile() {
            // Create data directory if it doesn't exist
            File file = new File(FilePath.attendanceDataPath);
            file.getParentFile().mkdirs();

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(FilePath.attendanceDataPath))) {
                // Write header
                writer.write("date,time,employeeId,outletCode,status\n");

                // Write all records
                for (Attendance att : attendanceRecords) {
                    writer.write(String.format("%s,%s,%s,%s,%s\n",
                            att.getDate(),
                            att.getTime(),
                            att.getEmployeeId(),
                            att.getOutletCode(),
                            att.getStatus()
                    ));
                }
            } catch (IOException e) {
                System.out.println("Error saving attendance: " + e.getMessage());
            }
        }

        // Load from CSV
        private void loadAttendanceFromFile() {
            File file = new File(FilePath.attendanceDataPath);
            if (!file.exists()) {
                return; // File doesn't exist yet, that's okay
            }

            try (BufferedReader reader = new BufferedReader(new FileReader(FilePath.attendanceDataPath))) {
                String line = reader.readLine(); // Skip header

                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length == 5) {
                        Attendance att = new Attendance(parts[2], parts[3], parts[4]);
                        att.setDate(parts[0]);
                        att.setTime(parts[1]);
                        attendanceRecords.add(att);
                    }
                }
            } catch (IOException e) {
                System.out.println("Error loading attendance: " + e.getMessage());
            }
        }

        // View attendance for a specific employee
        public void viewAttendance(Scanner input) {
            System.out.print("\nEnter Employee ID: ");
            String employeeId = input.nextLine();

            System.out.println("\n=== Attendance Records for " + employeeId + " ===");
            boolean found = false;

            for (Attendance att : attendanceRecords) {
                if (att.getEmployeeId().equals(employeeId)) {
                    System.out.printf("%s %s - %s - %s\n",
                            att.getDate(),
                            att.getTime(),
                            att.getStatus(),
                            att.getOutletCode()
                    );
                    found = true;
                }
            }

            if (!found) {
                System.out.println("No attendance records found.");
            }
        }

        // View today's attendance for current outlet
        public void viewTodayAttendance(String outletCode) {
            System.out.println("\n=== Today's Attendance for Outlet " + outletCode + " ===");

            String today = java.time.LocalDate.now()
                    .format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy"));

            boolean found = false;

            for (Attendance att : attendanceRecords) {
                if (att.getOutletCode().equals(outletCode) && att.getDate().equals(today)) {
                    System.out.printf("%s - %s - %s\n",
                            att.getTime(),
                            att.getEmployeeId(),
                            att.getStatus()
                    );
                    found = true;
                }
            }

            if (!found) {
                System.out.println("No attendance records for today.");
            }
        }

}
