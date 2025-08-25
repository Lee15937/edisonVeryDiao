/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package boundary;

import DAO.Dao;
import adt.ArrayList;
import adt.ListInterface;
import control.DoctorManagement;
import entity.Doctor;
import entity.DoctorEvent;
import entity.TimeRange;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class DoctorManagementUI {

    DoctorManagement doctorMgmt = new DoctorManagement();
    ListInterface<Doctor> doctorList = new adt.ArrayList<>();
    ListInterface<DoctorEvent> doctorEventList = new adt.ArrayList<>();
    ListInterface<TimeRange> timeRangeList = new adt.ArrayList<>();
    Scanner scanner = new Scanner(System.in);
    public static int doctorIdCounter = 1;
    public adt.ArrayList<Doctor> doctors = new adt.ArrayList<>();
    private Dao<Doctor> dao = new Dao<>();

    public static final String DOCTOR_FILE = "src/DAO/doctor.txt";

    public void runDoctorsManagement() {
        doctorList = doctorMgmt.readDoctorFromFileAsArrayList();
        int choice = -1;
        do {
            System.out.println("\nDoctor Management Menu:");
            System.out.println("1. Add Doctor");
            System.out.println("2. Search Doctor");
            System.out.println("3. Doctor Management List");
            System.out.println("4. Track Availability");
            System.out.println("5. Summary Reports");
            System.out.println("6. Edit/Update");
            System.out.println("7. Remove");
            System.out.println("8. Exit");
            System.out.print("Enter your choice (1-8): ");
            while (!scanner.hasNextInt()) {
                scanner.next();
                System.out.print("Enter your choice (1-8): ");
            }
            choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    doctorMgmt.loadDoctorsFromFile();
                    addDoctor();
                    break;
                case 2:
                    searchDoctor();
                    break;
                case 3:
                    doctorManagementList(doctorList, doctorEventList);
                    break;
                case 4:
                    trackAvailability();
                    break;
                case 5:
                    summaryReports(doctorList);
                    break;
                case 6:
                    editDoctor();
                    break;
                case 7:
                    removeDoctor();
                    break;
                case 8:
                    return;
                default:
                    System.out.println("\nInvalid choice.Please enter 1-8 only.");
            }
        } while (choice != 8);
    }

    public void addDoctor() {
        String id = String.format("D%03d", doctorIdCounter++);
        System.out.println("Assigned Doctor ID: " + id);

        String name, gender, phoneNo, email, schedule;
        boolean availability;

        while (true) {
            System.out.print("Enter Doctor Name: ");
            name = scanner.nextLine().trim();
            if (!name.isEmpty()) {
                break;
            }
            System.out.println("Doctor name cannot be empty.");
        }

        while (true) {
            System.out.print("Enter Doctor Gender (M/F): ");
            gender = scanner.nextLine().trim().toUpperCase();

            if (gender.equals("M")) {
                gender = "Male";
                break;
            } else if (gender.equals("F")) {
                gender = "Female";
                break;
            }

            System.out.println("Invalid input. Please enter M or F.");
        }

        while (true) {
            System.out.print("Enter Doctor Phone Number: ");
            phoneNo = scanner.nextLine().trim();
            if (phoneNo.matches("\\d{10,15}")) {
                break;
            }
            System.out.println("Invalid phone number. Please enter digits only (10â€“15 characters).");
        }

        while (true) {
            System.out.print("Enter Doctor Email: ");
            email = scanner.nextLine().trim();
            if (email.matches("^[\\w.-]+@[\\w.-]+\\.[A-Za-z]{2,6}$")) {
                break;
            }
            System.out.println("Invalid email. Please enter a valid format (example@domain.com).");
        }

        String pattern1 = "^(Mon|Tue|Wed|Thu|Fri|Sat|Sun)-(Mon|Tue|Wed|Thu|Fri|Sat|Sun) \\d{1,2}(am|pm)-\\d{1,2}(am|pm)$";
        String pattern2 = "^(Mon|Tue|Wed|Thu|Fri|Sat|Sun)(,(Mon|Tue|Wed|Thu|Fri|Sat|Sun))* \\d{1,2}(am|pm)-\\d{1,2}(am|pm)$";
        while (true) {
            System.out.print("Enter duty schedule (e.g., Mon-Fri 9am-5pm or Mon,Wed,Fri 9am-5pm): ");
            schedule = scanner.nextLine().trim();
            if (schedule.matches(pattern1) || schedule.matches(pattern2)) {
                break;
            }
            System.out.println("Invalid format! Please use 'Mon-Fri 9am-5pm' or 'Mon,Wed,Fri 9am-5pm'.");
        }

        while (true) {
            System.out.print("Is the doctor available? (Y/N): ");
            String availInput = scanner.nextLine().trim().toUpperCase();
            if (availInput.equals("Y")) {
                availability = true;
                break;
            } else if (availInput.equals("N")) {
                availability = false;
                break;
            }
            System.out.println("Invalid input. Please enter Y or N.");
        }

        Doctor newDoctor = new Doctor(id, name, gender, phoneNo, email, schedule, availability);
        doctorList.add(newDoctor);

        if (doctorMgmt != null) {
            dao.saveToFile(doctorList, DOCTOR_FILE);
        }

        System.out.println("Doctor added successfully: " + newDoctor.getName());
    }

    public void searchDoctor() {
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
                System.out.println("\nDoctor Found:");
                System.out.println("ID           : " + foundDoctor.getDoctorId());
                System.out.println("Name         : " + foundDoctor.getName());
                System.out.println("Gender       : " + foundDoctor.getGender());
                System.out.println("Phone Number : " + foundDoctor.getPhoneNo());
                System.out.println("Email        : " + foundDoctor.getEmail());
                System.out.println("Duty Schedule: " + foundDoctor.getDutySchedule());
                System.out.println("Availability : " + (foundDoctor.isAvailability() ? "Available" : "Not Available"));

                for (int i = 1; i <= doctorEventList.getNumberOfEntries(); i++) {
                    DoctorEvent event = doctorEventList.getEntry(i);
                    if (event.getDoctorId().equalsIgnoreCase(foundDoctor.getDoctorId())) {
                        boolean firstLine = true;
                        for (TimeRange tr : event.getShiftRanges()) {
                            if (firstLine) {
                                System.out.println("Shift        : " + tr.toString());
                                firstLine = false;
                            } else {
                                System.out.println("               " + tr.toString());
                            }
                        }
                    }
                }
                break;
            } else {
                ChoiceYesOrNo();
            }
        }
    }

    public void doctorManagementList(ListInterface<Doctor> dortorList, ListInterface<DoctorEvent> dortorEventList) {
        doctorMgmt.getDoctorList();
        String header1 = String.format("| %-10s | %-20s |%-8s |%-20s |%-20s | %-25s |\n",
                "ID", "Name", "Gender", "Phone Number", "Email", "Availability");
        String header2 = String.format("| %-10s | %-20s | %-25s |\n",
                "ID", "Name", "Duty Schedule");
        String header3 = String.format("| %-10s | %-20s | %-25s | %-25s |\n",
                "ID", "Name", "Duty Schedule", "Shift");
        String header4 = String.format("| %-10s | %-20s | %-15s | %-15s | %-20s |\n",
                "ID", "Name", "Start Date", "End Date", "Reason");

        String line1 = "-----------------------------------------------------------------------------------------------------------------------";
        String line2 = "-----------------------------------------------------------------";
        String line3 = "---------------------------------------------------------------------------------------------";
        String line4 = "------------------------------------------------------------------------------------------------";

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nDoctor Management List");
            System.out.println("1. Doctor List");
            System.out.println("2. Duty Schedule List");
            System.out.println("3. Doctor Shift List");
            System.out.println("4. Doctor Leave List");
            System.out.println("5. Back To Doctor Management Menu");

            System.out.print("Enter choice (1-5): ");
            String action = scanner.nextLine().trim();

            if (action.equals("1")) {
                System.out.println(line1);
                System.out.println(header1);
                System.out.println(line1);
                boolean hasRows = false;
                for (int i = 1; i <= dortorList.getNumberOfEntries(); i++) {
                    Doctor doc = dortorList.getEntry(i);
                    hasRows = true;
                    String row = String.format("| %-10s | %-20s |%-8s |%-20s |%-20s | %-25s |\n",
                            doc.getDoctorId(), doc.getName(), doc.getGender(),
                            doc.getPhoneNo(), doc.getEmail(),
                            doc.isAvailability() ? "Available" : "Not Available");
                    System.out.print(row);
                }
                if (!hasRows) {
                    System.out.println("| No data available.".concat(" ".repeat(line1.length() - 20)) + "|");
                }
                System.out.println(line1);

            } else if (action.equals("2")) {
                System.out.println(line2);
                System.out.println(header2);
                System.out.println(line2);
                boolean hasRows = false;
                for (int i = 1; i <= dortorList.getNumberOfEntries(); i++) {
                    Doctor doc = dortorList.getEntry(i);
                    hasRows = true;
                    String row = String.format("| %-10s | %-20s | %-25s |\n",
                            doc.getDoctorId(), doc.getName(), doc.getDutySchedule());
                    System.out.print(row);
                }
                if (!hasRows) {
                    System.out.println("| No data available.".concat(" ".repeat(line1.length() - 20)) + "|");
                }
                System.out.println(line2);

            } else if (action.equals("3")) {
                System.out.println(line3);
                System.out.println(header3);
                System.out.println(line3);
                boolean hasRows = false;

                for (int i = 1; i <= dortorEventList.getNumberOfEntries(); i++) {
                    DoctorEvent event = dortorEventList.getEntry(i);

                    Doctor doc = null;
                    for (int j = 1; j <= doctorList.getNumberOfEntries(); j++) {
                        Doctor tempDoc = doctorList.getEntry(j);
                        if (tempDoc.getDoctorId().equals(event.getDoctorId())) {
                            doc = tempDoc;
                            break;
                        }
                    }

                    if (doc != null) {
                        boolean firstLine = true;
                        for (TimeRange tr : event.getShiftRanges()) {
                            hasRows = true;
                            String row = String.format("| %-10s | %-20s | %-25s | %-25s |\n",
                                    firstLine ? doc.getDoctorId() : "",
                                    firstLine ? doc.getName() : "",
                                    firstLine ? doc.getDutySchedule() : "",
                                    tr.toString());
                            System.out.print(row);
                            firstLine = false;
                        }
                    }
                }

                if (!hasRows) {
                    System.out.println("| No data available.".concat(" ".repeat(line3.length() - 20)) + "|");
                }
                System.out.println(line3);

            } else if (action.equals("4")) {
                System.out.println(line4);
                System.out.println(header4);
                System.out.println(line4);
                boolean hasRows = false;

                for (int i = 1; i <= doctorEventList.getNumberOfEntries(); i++) {
                    DoctorEvent event = doctorEventList.getEntry(i);

                    if (event.isLeave()) {
                        Doctor doc = null;
                        for (int j = 1; j <= doctorList.getNumberOfEntries(); j++) {
                            Doctor tempDoc = doctorList.getEntry(j);
                            if (tempDoc.getDoctorId().equals(event.getDoctorId())) {
                                doc = tempDoc;
                                break;
                            }
                        }

                        if (doc != null) {
                            hasRows = true;
                            String row = String.format("| %-10s | %-20s | %-15s | %-15s | %-20s |\n",
                                    doc.getDoctorId(), doc.getName(),
                                    event.getLeaveStartDate(), event.getLeaveEndDate(), event.getLeaveReason());
                            System.out.print(row);
                        }
                    }
                }

                if (!hasRows) {
                    System.out.println("| No data available.".concat(" ".repeat(line4.length() - 20)) + "|");
                }
                System.out.println(line4);

            } else if (action.equals("5")) {
                System.out.println("Returning to Doctor Management Menu...");
                return;

            } else {
                System.out.println("\nInvalid choice. Please enter 1-5 only.");
            }
        }
    }

    public void ChoiceYesOrNo() {
        while (true) {
            System.out.print("\nDo you want to enter the Doctor ID again? (Y/N): ");
            String retry = scanner.nextLine().trim();
            if (retry.equalsIgnoreCase("Y")) {
                break;
            } else if (retry.equalsIgnoreCase("N")) {
                System.out.println("Returning to Doctor Management Menu...");
                runDoctorsManagement();
            } else {
                System.out.println("\nInvalid input.Please type 'Y' for yes or 'N' for no.");
            }
        }
    }

    public void trackAvailability() {
        String doctorId = getValidDoctorId();
        if (doctorId == null) {
            return;
        }

        Doctor selectedDoctor = null;
        for (int i = 1; i <= doctorList.getNumberOfEntries(); i++) {
            Doctor d = doctorList.getEntry(i);
            if (d.getDoctorId().equalsIgnoreCase(doctorId)) {
                selectedDoctor = d;
                break;
            }
        }

        if (selectedDoctor == null) {
            System.out.println("Doctor with ID " + doctorId + " not found.");
            return;
        }

        System.out.println("Selected Doctor: " + selectedDoctor.getName());
        System.out.println("Duty Schedule: " + selectedDoctor.getDutySchedule());

        while (true) {
            System.out.println("1. Assign Shift");
            System.out.println("2. Make Leave");
            System.out.println("3. Back To Doctor Management Menu");

            System.out.print("Enter choice (1-3): ");
            String action = scanner.nextLine().trim();

            if (action.equals("1")) {
                assignShift(selectedDoctor.getDoctorId());
            } else if (action.equals("2")) {
                makeLeave(selectedDoctor.getDoctorId());
            } else if (action.equals("3")) {
                return;
            } else {
                System.out.println("\nInvalid choice. Please enter 1-3 only.");
            }
        }
    }

    public void assignShift(String doctorId) {
        Doctor targetDoctor = null;
        for (int i = 1; i <= doctorList.getNumberOfEntries(); i++) {
            Doctor d = doctorList.getEntry(i);
            if (d.getDoctorId().equalsIgnoreCase(doctorId)) {
                targetDoctor = d;
                break;
            }
        }
        if (targetDoctor == null) {
            System.out.println("Doctor not found.");
            return;
        }

        String name = targetDoctor.getName();
        List<TimeRange> dutyRanges = doctorMgmt.parseDutySchedule(targetDoctor.getDutySchedule());
        if (dutyRanges.isEmpty()) {
            System.out.println("\nInvalid duty schedule.");
            return;
        }

        System.out.println("Allowed shift periods based on duty schedule:");
        for (TimeRange tr : dutyRanges) {
            System.out.println(" - " + tr);
        }

        ListInterface<TimeRange> assignedShifts = new adt.ArrayList<>();

        while (true) {
            System.out.print("Enter Shift Time (e.g., 9am-11am). Leave blank to finish: ");
            String shiftTimeStr = scanner.nextLine().trim();
            if (shiftTimeStr.isEmpty()) {
                break;
            }

            TimeRange shiftRange = doctorMgmt.parseTimeRange(shiftTimeStr);
            if (shiftRange == null) {
                System.out.println("\nInvalid time.");
                continue;
            }

            boolean withinDuty = false;
            for (TimeRange duty : dutyRanges) {
                if (duty.contains(shiftRange)) {
                    withinDuty = true;
                    break;
                }
            }
            if (!withinDuty) {
                System.out.println("\nShift Time Not Within Allowed.");
                continue;
            }

            boolean overlaps = false;
            for (int i = 1; i <= assignedShifts.getNumberOfEntries(); i++) {
                if (assignedShifts.getEntry(i).overlapsWith(shiftRange)) {
                    overlaps = true;
                    break;
                }
            }
            if (overlaps) {
                System.out.println("\nShift overlaps with existing.");
                continue;
            }

            assignedShifts.add(shiftRange);
            System.out.println("Shift added: " + shiftRange);
        }

        if (assignedShifts.isEmpty()) {
            System.out.println("\nNo shifts assigned.");
            return;
        }

        java.util.List<TimeRange> shiftList = new java.util.ArrayList<>();
        for (int i = 1; i <= assignedShifts.getNumberOfEntries(); i++) {
            shiftList.add(assignedShifts.getEntry(i));
        }

        DoctorEvent newShift = DoctorEvent.Shift(doctorId, name, shiftList);
        doctorEventList.add(newShift);

        System.out.println("Shifts assigned to Doctor " + name + ":");
        for (TimeRange tr : shiftList) {
            System.out.println(" - " + tr);
        }
    }

    public void makeLeave(String doctorId) {
        Doctor targetDoctor = null;
        for (int i = 1; i <= doctorList.getNumberOfEntries(); i++) {
            Doctor d = doctorList.getEntry(i);
            if (d.getDoctorId().equalsIgnoreCase(doctorId)) {
                targetDoctor = d;
                break;
            }
        }
        if (targetDoctor == null) {
            System.out.println("Doctor not found.");
            return;
        }

        String name = targetDoctor.getName();
        LocalDate startDate = null;

        while (startDate == null) {
            System.out.print("Enter start date for leave (YYYY-MM-DD): ");
            String startDateStr = scanner.nextLine().trim();
            try {
                startDate = LocalDate.parse(startDateStr);
            } catch (Exception e) {
                System.out.println("\nInvalid date.");
            }
        }

        int days = 0;
        while (days <= 0) {
            System.out.print("Enter number of leave days: ");
            String daysStr = scanner.nextLine().trim();
            try {
                days = Integer.parseInt(daysStr);
                if (days <= 0) {
                    System.out.println("\nInvalid number.");
                }
            } catch (NumberFormatException e) {
                System.out.println("\nInvalid number.");
            }
        }

        String reason = "";
        while (reason.isEmpty()) {
            System.out.print("Enter reason for leave: ");
            reason = scanner.nextLine().trim();
            if (reason.isEmpty()) {
                System.out.println("\nCannot be empty.");
            }
        }

        int qty = doctorEventList.getNumberOfEntries() + 1;

        DoctorEvent newLeave = DoctorEvent.Leave(doctorId, name, startDate, days, reason, qty);
        doctorEventList.add(newLeave);

        LocalDate endDate = startDate.plusDays(days);
        System.out.printf("Doctor %s is on leave from %s to %s. Reason: %s\n",
                name, startDate, endDate, reason);
    }

    public void summaryReports(ListInterface<Doctor> dortorList) {
        while (true) {
            System.out.println("\nDoctor Management Summary Reports");
            System.out.println("1. Doctor Leave");
            System.out.println("2. Doctor ");
            System.out.println("3. Back To Doctor Management Menu");

            System.out.print("Enter choice (1-4): ");
            String action = scanner.nextLine().trim();

            if (action.equals("1")) {
                doctorLeaveSummaryReports(dortorList);
            } else if (action.equals("2")) {

            } else if (action.equals("3")) {
                System.out.println("Returning to Doctor Management Menu...");
                return;
            } else {
                System.out.println("\nInvalid choice.Please enter 1-3 only.");
            }
            continue;
        }
    }

    public void doctorLeaveSummaryReports(ListInterface<Doctor> dortorList) {
        System.out.println("------------------------------------------------");
        System.out.println("|      Doctor Leave Summary Report             |");
        System.out.println("------------------------------------------------");
        System.out.printf("| %-10s | %-20s | %-8s |\n", "ID", "Name", "Quantity");
        System.out.println("------------------------------------------------");
        boolean hasLeave = false;
        for (int i = 1; i <= dortorList.getNumberOfEntries(); i++) {
            Doctor doc = dortorList.getEntry(i);
            hasLeave = true;
            List<DoctorEvent> leaves = doc.getLeaves();
            int qtys = leaves.size();
            String row = String.format("| %-10s | %-20s | %-8d |\n", doc.getDoctorId(), "Dr." + doc.getName(), qtys);
            System.out.print(row);
        }
        if (!hasLeave) {
            System.out.println("|    No leave found in the system.            |");
        }
        System.out.println("------------------------------------------------");
    }

    public String getValidDoctorId() {
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
                System.out.println("\n Doctor with ID " + id + " not found.");
                ChoiceYesOrNo();
            }
        }
    }

    public void editDoctor() {
        String id = getValidDoctorId();
        if (id == null) {
            return;
        }

        Doctor doc = null;
        for (int i = 1; i <= doctorList.getNumberOfEntries(); i++) {
            Doctor d = doctorList.getEntry(i);
            if (d.getDoctorId().equalsIgnoreCase(id)) {
                doc = d;
                break;
            }
        }

        if (doc == null) {
            System.out.println("Doctor not found.");
            return;
        }

        System.out.println("Editing Doctor: " + doc.getName() + " (" + doc.getDoctorId() + ")");

        System.out.print("Enter new name (leave blank to keep '" + doc.getName() + "'): ");
        String newName = scanner.nextLine().trim();
        if (!newName.isEmpty()) {
            doc.setName(newName);
        }

        while (true) {
            System.out.print("Enter new phone number (10â€“15 digits) (leave blank to keep '" + doc.getPhoneNo() + "'): ");
            String newPhone = scanner.nextLine().trim();
            if (newPhone.isEmpty()) {
                break;
            }
            if (newPhone.matches("\\d{10,15}")) {
                doc.setPhoneNo(newPhone);
                break;
            } else {
                System.out.println("\nInvalid format.");
            }
        }

        while (true) {
            System.out.print("Enter new email (leave blank to keep '" + doc.getEmail() + "'): ");
            String newEmail = scanner.nextLine().trim();
            if (newEmail.isEmpty()) {
                break;
            }
            if (newEmail.matches("^[\\w.-]+@[\\w.-]+\\.[A-Za-z]{2,6}$")) {
                doc.setEmail(newEmail);
                break;
            } else {
                System.out.println("\nInvalid format.");
            }
        }

        String pattern1 = "^(Mon|Tue|Wed|Thu|Fri|Sat|Sun)-(Mon|Tue|Wed|Thu|Fri|Sat|Sun) \\d{1,2}(am|pm)-\\d{1,2}(am|pm)$";
        String pattern2 = "^(Mon|Tue|Wed|Thu|Fri|Sat|Sun)(,(Mon|Tue|Wed|Thu|Fri|Sat|Sun))* \\d{1,2}(am|pm)-\\d{1,2}(am|pm)$";
        while (true) {
            System.out.print("Enter new duty schedule (leave blank to keep '" + doc.getDutySchedule() + "'): ");
            String newSchedule = scanner.nextLine().trim();
            if (newSchedule.isEmpty()) {
                break;
            }
            if (newSchedule.matches(pattern1) || newSchedule.matches(pattern2)) {
                doc.setDutySchedule(newSchedule);
                break;
            } else {
                System.out.println("\nInvalid format.");
            }
        }

        while (true) {
            System.out.print("Is the doctor available? (Y/N, leave blank to keep current '"
                    + (doc.isAvailability() ? "Yes" : "No") + "'): ");
            String availInput = scanner.nextLine().trim().toUpperCase();
            if (availInput.isEmpty()) {
                break;
            }
            if (availInput.equals("Y")) {
                doc.setAvailability(true);
                break;
            } else if (availInput.equals("N")) {
                doc.setAvailability(false);
                break;
            }
            System.out.println("\nInvalid format.");
        }

        dao.saveToFile(doctorList, DOCTOR_FILE);
        System.out.println("\nDoctor Updated Successfully.");
    }

    public void removeDoctor() {
        String doctorId = getValidDoctorId();
        if (doctorId == null) {
            return;
        }

        Doctor doc = null;
        int indexToRemove = -1;

        for (int i = 1; i <= doctorList.getNumberOfEntries(); i++) {
            Doctor d = doctorList.getEntry(i);
            if (d.getDoctorId().equalsIgnoreCase(doctorId)) {
                doc = d;
                indexToRemove = i;
                break;
            }
        }

        if (doc == null) {
            System.out.println("Doctor not found.");
            return;
        }

        System.out.print("Are you sure you want to remove Dr. " + doc.getName() + " (Y/N)? ");
        String confirm = scanner.nextLine().trim();
        if (!confirm.equalsIgnoreCase("Y")) {
            System.out.println("Removal cancelled.");
            return;
        }

        Doctor removed = doctorList.remove(indexToRemove);
        if (removed != null) {
            dao.saveToFile(doctorList, DOCTOR_FILE);
            System.out.println("… Doctor " + doc.getName() + " removed successfully.");
        } else {
            System.out.println("Failed to remove doctor.");
        }
    }
}
