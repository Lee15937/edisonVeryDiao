/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package boundary;

import adt.ArrayList;
import adt.ListInterface;
import control.DoctorManagement;
import entity.Doctor;
import entity.DoctorEvent;
import entity.TimeRange;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class DoctorManagementUI {

    Scanner scanner = new Scanner(System.in);
    public static int doctorIdCounter = 1;
    public adt.ArrayList<Doctor> doctors = new adt.ArrayList<>();

    public void displayDoctorManagementMenu() {
        System.out.println("\nDoctor Management Menu:");
        System.out.println("1. Add Doctor");
        System.out.println("2. Search Doctor");
        System.out.println("3. Doctor Management List");
        System.out.println("4. Track Availability");
        System.out.println("5. Summary Reports");
        System.out.println("6. Edit/Update");
        System.out.println("7. Remove");
        System.out.println("8. Exit");
    }

    public int getMenuChoice() {
        System.out.print("Enter your choice (1-8): ");
        while (!scanner.hasNextInt()) {
            System.out.print("Invalid input. Please enter a number.");
            scanner.nextLine();
            System.out.print("Enter your choice (1-8): ");
        }
        int choice = scanner.nextInt();
        scanner.nextLine();
        return choice;
    }

    public String inputDoctorName() {
        String name;
        while (true) {
            System.out.print("Enter Doctor Name: ");
            name = scanner.nextLine().trim();
            if (!name.isEmpty()) {
                return name;
            }
            System.out.println("Doctor name cannot be empty.");
        }
    }

    public String inputDoctorGender() {
        String gender;
        while (true) {
            System.out.print("Enter Doctor Gender (M/F): ");
            gender = scanner.nextLine().trim().toUpperCase();

            if (gender.equals("M")) {
                gender = "Male";
                return gender;
            } else if (gender.equals("F")) {
                gender = "Female";
                return gender;
            }

            System.out.println("Invalid input. Please enter M or F.");
        }
    }

    public String inputDoctorPhoneNumber() {
        String phoneNo;
        while (true) {
            System.out.print("Enter Doctor Phone Number: ");
            phoneNo = scanner.nextLine().trim();
            if (phoneNo.matches("\\d{10,15}")) {
                return phoneNo;
            }
            System.out.println("Invalid phone number. Please enter digits only (10-15 characters).");
        }
    }

    public String inputDoctorEmail() {
        String email;
        while (true) {
            System.out.print("Enter Doctor Email: ");
            email = scanner.nextLine().trim();
            if (email.matches("^[\\w.-]+@[\\w.-]+\\.[A-Za-z]{2,6}$")) {
                return email;
            }
            System.out.println("Invalid email. Please enter a valid format (example@domain.com).");
        }
    }

    public String inputDoctorSchedule() {
        String schedule;
        String timeRegex = "([01]?\\d|2[0-3]):[0-5]\\d";
        String pattern1 = "^(Mon|Tue|Wed|Thu|Fri|Sat|Sun)-(Mon|Tue|Wed|Thu|Fri|Sat|Sun) "
                + timeRegex + "-" + timeRegex + "$";
        String pattern2 = "^(Mon|Tue|Wed|Thu|Fri|Sat|Sun)(,(Mon|Tue|Wed|Thu|Fri|Sat|Sun))* "
                + timeRegex + "-" + timeRegex + "$";

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("H:mm");

        while (true) {
            System.out.print("Enter duty schedule (e.g., Mon-Fri 9:00-17:00 or Mon,Wed,Fri 9:00-17:00): ");
            schedule = scanner.nextLine().trim();

            if (schedule.matches(pattern1) || schedule.matches(pattern2)) {
                String timePart = schedule.substring(schedule.indexOf(" ") + 1);
                String[] times = timePart.split("-");

                LocalTime start = LocalTime.parse(times[0], fmt);
                LocalTime end = LocalTime.parse(times[1], fmt);

                if (start.isBefore(end)) {
                    return schedule;
                } else {
                    System.out.println("Invalid time! End time must be after start time.");
                }
            } else {
                System.out.println("Invalid format! Please use 'Mon-Fri 9:00-17:00' or 'Mon,Wed,Fri 9:00-17:00'.");
            }
        }
    }

    public boolean inputDoctorAilability() {
        boolean availability;
        while (true) {
            System.out.print("Is the doctor available? (Y/N): ");
            String availInput = scanner.nextLine().trim().toUpperCase();
            if (availInput.equals("Y")) {
                availability = true;
                return availability;
            } else if (availInput.equals("N")) {
                availability = false;
                return availability;
            }
            System.out.println("Invalid input. Please enter Y or N.");
        }
    }

    public Doctor inputDoctorDetails() {
        String doctorId = String.format("D%03d", doctorIdCounter++);
        System.out.println("Assigned Doctor ID: " + doctorId);

        String doctorName = inputDoctorName();
        String doctorGender = inputDoctorGender();
        String doctorPhoneNo = inputDoctorPhoneNumber();
        String doctorEmail = inputDoctorEmail();
        String doctorSchedule = inputDoctorSchedule();
        boolean doctorAilability = inputDoctorAilability();

        return new Doctor(doctorId, doctorName, doctorGender, doctorPhoneNo, doctorEmail, doctorSchedule, doctorAilability);
    }

    public void searchDoctorDetail(Doctor foundDoctor) {
        System.out.println("\nDoctor Found:");
        System.out.println("ID           : " + foundDoctor.getDoctorId());
        System.out.println("Name         : " + foundDoctor.getName());
        System.out.println("Gender       : " + foundDoctor.getGender());
        System.out.println("Phone Number : " + foundDoctor.getPhoneNo());
        System.out.println("Email        : " + foundDoctor.getEmail());
        System.out.println("Duty Schedule: " + foundDoctor.getDutySchedule());
        System.out.println("Availability : " + (foundDoctor.isAvailability() ? "Available" : "Not Available"));
    }

    public void doctorListMenu() {
        System.out.println("\nDoctor Management List");
        System.out.println("1. Doctor List");
        System.out.println("2. Duty Schedule List");
        System.out.println("3. Doctor Shift List");
        System.out.println("4. Doctor Leave List");
        System.out.println("5. Back To Doctor Management Menu");
    }

    public int doctorListMenuAction() {
        System.out.print("Choose an option: ");
        while (!scanner.hasNextInt()) {
            System.out.print("Invalid input. Please enter a number.");
            scanner.nextLine();
        }

        int action = scanner.nextInt();
        scanner.nextLine();
        return action;
    }

    public void printDoctorListHeader() {
        String header = String.format("| %-10s | %-20s |%-8s |%-20s |%-20s | %-25s |",
                "ID", "Name", "Gender", "Phone Number", "Email", "Availability");
        String line = "=".repeat(header.length());
        System.out.println(line);
        System.out.println(header);
        System.out.println(line);
    }

    public void printDoctorListRow(Doctor doc) {
        System.out.printf("| %-10s | %-20s |%-8s |%-20s |%-20s | %-25s |\n",
                doc.getDoctorId(), doc.getName(), doc.getGender(),
                doc.getPhoneNo(), doc.getEmail(),
                doc.isAvailability() ? "Available" : "Not Available");
    }

    public void printDoctorListNoData() {
        System.out.printf("| %-10s | %-20s |%-8s |%-20s |%-20s | %-25s |\n",
                "No Data", "", "", "", "", "");
    }

    public void printDutyScheduleHeader() {
        String header = String.format("| %-10s | %-20s | %-25s |", "ID", "Name", "Duty Schedule");
        String line = "=".repeat(header.length());
        System.out.println(line);
        System.out.println(header);
        System.out.println(line);
    }

    public void printDutyScheduleRow(Doctor doc) {
        System.out.printf("| %-10s | %-20s | %-25s |\n",
                doc.getDoctorId(), doc.getName(), doc.getDutySchedule());
    }

    public void printDutyScheduleNoData() {
        System.out.printf("| %-10s | %-20s | %-25s |\n", "No Data", "", "");
    }

    public void printShiftHeader() {
        String header = String.format("| %-10s | %-20s | %-25s | %-25s |",
                "ID", "Name", "Duty Schedule", "Shift");
        String line = "=".repeat(header.length());
        System.out.println(line);
        System.out.println(header);
        System.out.println(line);
    }

    public void printShiftRowFirst(Doctor d, TimeRange shift) {
        System.out.printf("| %-10s | %-20s | %-25s | %-25s |\n",
                d.getDoctorId(), d.getName(), d.getDutySchedule(), shift.toString());
    }

    public void printShiftRowEmpty(TimeRange shift) {
        System.out.printf("| %-10s | %-20s | %-25s | %-25s |\n", "", "", "", shift.toString());
    }

    public void printShiftNoData() {
        System.out.printf("| %-10s | %-20s | %-25s | %-25s |\n", "No Data", "", "", "");
    }

    public void printLeaveHeader() {
        String header = String.format("| %-10s | %-20s | %-15s | %-15s | %-20s |",
                "ID", "Name", "Start Date", "End Date", "Reason");
        String line = "=".repeat(header.length());
        System.out.println(line);
        System.out.println(header);
        System.out.println(line);
    }

    public void printLeaveRow(Doctor doc, DoctorEvent event) {
        System.out.printf("| %-10s | %-20s | %-15s | %-15s | %-20s |\n",
                doc.getDoctorId(), doc.getName(),
                event.getLeaveStartDate(), event.getLeaveEndDate(), event.getLeaveReason());
    }

    public void printLeaveNoData() {
        System.out.printf("| %-10s | %-20s | %-15s | %-15s | %-20s |\n", "No Data", "", "", "", "");
    }

    public void trackAvailabilityMenu() {
        System.out.println("1. Assign Shift");
        System.out.println("2. Make Leave");
        System.out.println("3. Back To Doctor Management Menu");
    }

    public String getShiftInput() {
        System.out.print("Enter Shift Time (e.g., 09:00-11:00). Leave blank to finish: ");
        return scanner.nextLine().trim();
    }

    public LocalDate getStartDate() {
        LocalDate startDate = null;
        while (startDate == null) {
            System.out.print("Enter start date for leave (YYYY-MM-DD): ");
            String input = scanner.nextLine().trim();
            try {
                startDate = LocalDate.parse(input);
            } catch (Exception e) {
                System.out.println("\nInvalid date.");
            }
        }
        return startDate;
    }

    public int getLeaveDays() {
        int days = 0;
        while (days <= 0) {
            System.out.print("Enter number of leave days: ");
            String input = scanner.nextLine().trim();
            try {
                days = Integer.parseInt(input);
                if (days <= 0) {
                    System.out.println("\nInvalid number.");
                }
            } catch (NumberFormatException e) {
                System.out.println("\nInvalid number.");
            }
        }
        return days;
    }

    public String getReason() {
        String reason = "";
        while (reason.isEmpty()) {
            System.out.print("Enter reason for leave: ");
            reason = scanner.nextLine().trim();
            if (reason.isEmpty()) {
                System.out.println("\nCannot be empty.");
            }
        }
        return reason;
    }

    public void showMessage(String message) {
        System.out.println(message);
    }

    public void summaryReportsMenu() {
        System.out.println("\nDoctor Management Summary Reports");
        System.out.println("1. Doctor Duty Schedule Summary");
        System.out.println("2. Doctor Leave Summary");
        System.out.println("3. Doctor Availability Summary");
        System.out.println("4. Back To Doctor Management Menu");
    }

    public void printDutyScheduleSummaryHeader() {
        String header = String.format("|  %-3s  | %-15s |", "Day", "Doctor Quantity");
        String line = "=".repeat(header.length());
        System.out.println(line);
        System.out.println("|    Duty Schedule Summary    |");
        System.out.println(line);
        System.out.println(header);
        System.out.println(line);
    }

    public void printDutyScheduleSummaryRow(String day, int qty) {
        String row = String.format("|  %-3s  | %-15d |", day, qty);
        System.out.println(row);
        System.out.println("=".repeat(row.length()));
    }

    public void printLeaveSummaryHeader() {
        String header = String.format("| %-10s | %-20s | %-8s |",
                "ID", "Name", "Quantity");
        String line = "=".repeat(header.length());
        System.out.println(line);
        System.out.println("|           Doctor Leave Summary Report        |");
        System.out.println(line);
        System.out.println(header);
        System.out.println(line);
    }

    public void printLeaveSummaryRow(String id, String name, int qty) {
        String row = String.format("| %-10s | %-20s | %-8d |", id, name, qty);
        System.out.println(row);
    }

    public void printAvailabilitySummaryHeader() {
        String header = String.format("| %-15s | %-5s |", "Availability", "Count");
        String line = "=".repeat(header.length());
        System.out.println(line);
        System.out.println(header);
        System.out.println(line);
    }

    public void printAvailabilityRow(String availability, int count) {
        String row = String.format("| %-15s | %-5d |\n", availability, count);
        System.out.println(row);
        System.out.println("=".repeat(row.length()));
    }

    public String getValidDoctorId() {
        DoctorManagement doctorMgmt = new DoctorManagement();
        while (true) {
            System.out.print("Enter Doctor ID: ");
            String id = scanner.nextLine().trim();

            ArrayList<Doctor> doctors = doctorMgmt.readDoctorFromFileAsArrayList();
            Doctor foundDoctor = null;

            for (Doctor d : doctors) {
                if (d.getDoctorId().equalsIgnoreCase(id)) {
                    foundDoctor = d;
                    break;
                }
            }

            if (foundDoctor != null) {
                return id;
            } else {
                System.out.println("\nDoctor with ID " + id + " not found.");
                if (!ChoiceYesOrNo()) {
                    System.out.println("Returning to Doctor Management Menu...");
                    return null;
                }
            }
        }
    }

    public boolean ChoiceYesOrNo() {
        while (true) {
            System.out.print("\nDo you want to enter the Doctor ID again? (Y/N): ");
            String retry = scanner.nextLine().trim();
            if (retry.equalsIgnoreCase("Y")) {
                return true;
            } else if (retry.equalsIgnoreCase("N")) {
                return false;
            } else {
                System.out.println("\nInvalid input. Please type 'Y' for yes or 'N' for no.");
            }
        }
    }

    public String inputEditDoctorName(String currentName) {
        System.out.print("Enter new name (leave blank to keep '" + currentName + "'): ");
        String newName = scanner.nextLine().trim();
        return newName.isEmpty() ? currentName : newName;
    }

    public String inputEditDoctorPhone(String currentPhone) {
        while (true) {
            System.out.print("Enter new phone number (10â€“15 digits) (leave blank to keep '" + currentPhone + "'): ");
            String newPhone = scanner.nextLine().trim();
            if (newPhone.isEmpty()) {
                return currentPhone;
            }
            if (newPhone.matches("\\d{10,15}")) {
                return newPhone;
            }
            System.out.println("Invalid format.");
        }
    }

    public String inputEditDoctorEmail(String currentEmail) {
        while (true) {
            System.out.print("Enter new email (leave blank to keep '" + currentEmail + "'): ");
            String newEmail = scanner.nextLine().trim();
            if (newEmail.isEmpty()) {
                return currentEmail;
            }
            if (newEmail.matches("^[\\w.-]+@[\\w.-]+\\.[A-Za-z]{2,6}$")) {
                return newEmail;
            }
            System.out.println("Invalid format.");
        }
    }

    public String inputEditDoctorSchedule(String currentSchedule, Doctor doctor, ListInterface<DoctorEvent> doctorEventList) {
        String timeRegex = "([01]?\\d|2[0-3]):[0-5]\\d";
        String pattern1 = "^(Mon|Tue|Wed|Thu|Fri|Sat|Sun)-(Mon|Tue|Wed|Thu|Fri|Sat|Sun) "
                + timeRegex + "-" + timeRegex + "$";
        String pattern2 = "^(Mon|Tue|Wed|Thu|Fri|Sat|Sun)(,(Mon|Tue|Wed|Thu|Fri|Sat|Sun))* "
                + timeRegex + "-" + timeRegex + "$";

        while (true) {
            System.out.print("Enter new duty schedule (leave blank to keep '" + currentSchedule + "'): ");
            String newSchedule = scanner.nextLine().trim();

            if (newSchedule.isEmpty()) {
                return currentSchedule;
            }

            if (newSchedule.matches(pattern1) || newSchedule.matches(pattern2)) {
                try {
                    String timePart = newSchedule.substring(newSchedule.indexOf(" ") + 1);
                    String[] times = timePart.split("-");
                    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("H:mm");

                    LocalTime start = LocalTime.parse(times[0], timeFormatter);
                    LocalTime end = LocalTime.parse(times[1], timeFormatter);

                    if (!start.isBefore(end)) {
                        System.out.println("X Invalid time! End time must be after start time.");
                        continue;
                    }

                    boolean conflictFound = false;
                    for (int i = 1; i <= doctorEventList.getNumberOfEntries(); i++) {
                        DoctorEvent shiftEvent = doctorEventList.getEntry(i);
                        if (shiftEvent.getDoctorId().equals(doctor.getDoctorId()) && shiftEvent.isShift()) {
                            for (int j = 1; j <= shiftEvent.getShiftRanges().getNumberOfEntries(); j++) {
                                TimeRange tr = shiftEvent.getShiftRanges().getEntry(j);
                                if (tr.getStart().isBefore(start) || tr.getEnd().isAfter(end)) {
                                    conflictFound = true;
                                    System.out.println("X Conflict: Shift " + tr + " does not fit inside "
                                            + start + "-" + end);
                                }
                            }
                        }
                    }

                    if (conflictFound) {
                        System.out.println("â�Œ Cannot set this duty schedule because some shifts are outside the time.");
                        continue;
                    }

                    return newSchedule;

                } catch (DateTimeParseException e) {
                    System.out.println("X Error parsing time. Please use format like 'Mon-Fri 09:00-17:00'");
                }
            } else {
                System.out.println("X Invalid format. Example: 'Mon-Fri 09:00-17:00' or 'Mon,Wed,Fri 09:00-17:00'");
            }
        }
    }

    public boolean inputEditDoctorAvailability(boolean currentAvailability) {
        while (true) {
            System.out.print("Is the doctor available? (Y/N, leave blank to keep current '"
                    + (currentAvailability ? "Yes" : "No") + "'): ");
            String availInput = scanner.nextLine().trim().toUpperCase();
            if (availInput.isEmpty()) {
                return currentAvailability;
            }
            if (availInput.equals("Y")) {
                return true;
            } else if (availInput.equals("N")) {
                return false;
            }
            System.out.println("Invalid input. Please enter Y or N.");
        }
    }

    public boolean confirmRemoval(String doctorName) {
        System.out.print("Are you sure you want to remove Dr. " + doctorName + " (Y/N)? ");
        String confirm = scanner.nextLine().trim().toUpperCase();
        return confirm.equals("Y");
    }
}
