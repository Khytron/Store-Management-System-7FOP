package service;

import model.Attendance;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class AttendanceManager {
        private static final String ATTENDANCE_FILE = "data/attendance.csv";
        private List<Attendance> attendanceRecords;

        public AttendanceManager() {
            this.attendanceRecords = new ArrayList<>();
            loadAttendanceFromFile();
        }

        // Clock In
        public void clockIn(String employeeId, String outletCode) {
            Attendance record = new Attendance(employeeId, outletCode, "Clock In");
            attendanceRecords.add(record);
            saveAttendanceToFile();
            System.out.println("\n✓ Clock In successful at " + record.getDate()+ " at " + record.getTime());
        }

        // Clock Out
        public void clockOut(String employeeId, String outletCode) {
            Attendance record = new Attendance(employeeId, outletCode, "Clock Out");
            attendanceRecords.add(record);
            saveAttendanceToFile();
            System.out.println("\n✓ Clock Out successful at " + record.getDate() + " at " + record.getTime());
        }

        // Save to CSV
        private void saveAttendanceToFile() {
            try {
                // Create data directory if it doesn't exist
                File file = new File(ATTENDANCE_FILE);
                file.getParentFile().mkdirs();

                BufferedWriter writer = new BufferedWriter(new FileWriter(ATTENDANCE_FILE));

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

                writer.close();
            } catch (IOException e) {
                System.out.println("Error saving attendance: " + e.getMessage());
            }
        }

        // Load from CSV
        private void loadAttendanceFromFile() {
            try {
                File file = new File(ATTENDANCE_FILE);
                if (!file.exists()) {
                    return; // File doesn't exist yet, that's okay
                }

                BufferedReader reader = new BufferedReader(new FileReader(ATTENDANCE_FILE));
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

                reader.close();
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
