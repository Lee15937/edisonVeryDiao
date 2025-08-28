package control;

import adt.SortedLinkedList;
import boundary.ConsultationUI;
import java.util.Scanner;
import entity.Consultation;
import utility.Utils;
import DAO.Dao;
import adt.ArrayList;
import entity.Doctor;
import entity.Patient;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import boundary.PatientRegistrationForm;
import java.time.YearMonth;

public class ConsultationManagement {

    private static final String CONSUL_FILE = "src\\DAO\\consultation.txt";

    ConsultationUI ui = new ConsultationUI();
    private Scanner scanner = new Scanner(System.in);

    private Dao dao = new Dao();
    // Consultation-specific list (uses sorted list for Consultation)
    private SortedLinkedList<Consultation> consultationList = consultationList = readConnsultationFromFile();

    PatientRegistrationForm prf = new PatientRegistrationForm();

    public void addConsultation() {
        // Delegates to validation helpers
        String date = Utils.getValidatedDate();
        String time = Utils.getValidatedTime();

        String ic = prf.inputPatientIC();
        Patient patient = findPatientByIC(ic);
        if (patient == null) {
            System.out.println("Patient not found for IC: " + ic + ".\nPlease Register first.");
            return;
        }

        String patientIC = patient.getIC();
        String patientName = patient.getName();
//        System.out.print("Enter patient name: ");
//        String patientName = scanner.nextLine();

        ArrayList<Doctor> availableDoctors = getAvailableDoctors(date, time);
        if (availableDoctors.isEmpty()) {
            System.out.println("No available doctors found at the specified date/time.");
            return;
        }

        System.out.println("\nAvailable Doctors:");
        System.out.printf("%-10s %-20s\n", "ID", "Name");
        for (Doctor doc : availableDoctors) {
            System.out.printf("%-10s %-20s\n", doc.getDoctorId(), doc.getName());
        }

        System.out.print("Enter the ID of the available doctor: ");
        String doctorId = scanner.nextLine().trim();
//        System.out.print("Enter doctor name: ");
//        String doctorName = scanner.nextLine();

        Doctor selectedDoctor = null;
        for (Doctor doc : availableDoctors) {
            if (doc.getDoctorId().equalsIgnoreCase(doctorId)) {
                selectedDoctor = doc;
                break;
            }
        }

        if (selectedDoctor == null) {
            System.out.println("Invalid Doctor ID. Consultation not added.");
            return;
        }

        SortedLinkedList<Consultation> cl = consultationList = readConnsultationFromFile();
        
        Consultation newConsultation = new Consultation(patientIC, patientName, doctorId, selectedDoctor.getName(), date, time);

        //Conflict checking before adding
//        for (Consultation c : consultationList) {
//            if (newConsultation.conflictsWith(c)) {
//                System.out.println("Cannot schedule. Conflict with: \n");
//                ui.displayConsultationHeader();
//                System.out.println(c);
//                return; // exit without adding
//            }
//        }
        boolean added = consultationList.add(newConsultation);

        if (added) {
            dao.saveToFile(consultationList, CONSUL_FILE);
            System.out.println("Consultation appointment added successfully!");

        } else {
            System.out.println("Failed to add consultation (unexpected error).");
        }
    }

    private Patient findPatientByIC(String ic) {
        if (ic == null || ic.isEmpty()) {
            return null;
        }

        PatientRegistration pr = new PatientRegistration();

        // Make sure patient list is loaded from file if not already
        ArrayList<Patient> patientList = pr.readPatientFromFileAsArrayList();

        //Debug use only
        //System.out.println("[DEBUG] Loaded patient list from file, size = " + (patientList != null ? patientList.getNumberOfEntries() : 0));
        //System.out.println("[DEBUG] Searching for IC: " + ic);
        for (int i = 1; i <= patientList.getNumberOfEntries(); i++) {
            Patient p = patientList.getEntry(i);

            //Debug use only
            //System.out.println("[DEBUG] Checking patient #" + i  + " IC=" + p.getIC() + " Name=" + p.getName());
            if (p.getIC().equalsIgnoreCase(ic)) {

                //Debug use only
                //System.out.println("[DEBUG] Match found! Patient = " + p.getName());
                return p;
            }
        }
        return null; // not found
    }

    private ArrayList<Doctor> getAvailableDoctors(String date, String time) {
        ArrayList<Doctor> availableDoctors = new ArrayList<>();
        LocalDate localDate = LocalDate.parse(date);
        LocalTime consultationTime = LocalTime.parse(time);

        DoctorManagement dm = new DoctorManagement();
        ArrayList<Doctor> doctorList = dm.readDoctorFromFileAsArrayList();

        Consultation requestedConsultation = new Consultation("TEMP_IC", "TEMP_PATIENT", "TEMP_DOCTORID", "TEMP_DOCTOR", date, time);
        //Debug use only
        //System.out.println("DEBUG: Total doctors read = " + doctorList.getNumberOfEntries());

        for (int i = 1; i <= doctorList.getNumberOfEntries(); i++) { // check if 1 or 0 based
            Doctor doc = doctorList.getEntry(i);

            //debug use only
//            System.out.println("DEBUG: Read doctor -> ID: " + doc.getDoctorId()
//                    + " | Name: " + doc.getName()
//                    + " | Gender: " + doc.getGender()
//                    + " | phoneNo: " + doc.getPhoneNo()
//                    + " | Email: " + doc.getEmail()
//                    + " | Duty: " + doc.getDutySchedule()
//                    + " | Availability: " + doc.isAvailability());
//            System.out.println("DEBUG: Checking doctor " + doc.getName()
//                    + " | Duty: " + doc.getDutySchedule()
//                    + " | Availability flag: " + doc.isAvailability());
//
//            //debug use only
//            System.out.println("DEBUG: Comparing consultation date: " + localDate.getDayOfWeek());
            String dutySchedule = doc.getDutySchedule();
            if (isDateWithinDutyDay(dutySchedule, localDate)
                    && isTimeWithinDutyTime(dutySchedule, consultationTime)) {

                requestedConsultation.setDoctorName(doc.getName());
                boolean hasConflict = false;
                for (Consultation existing : consultationList) {
                    if (requestedConsultation.conflictsWith(existing)) {
                        hasConflict = true;
                        System.out.println("[DEBUG] Conflict: " + doc.getName()
                                + " already has consultation " + existing.getConsultationID()
                                + " at " + existing.getTime());
                        break;
                    }
                }

                if (!hasConflict) {
                    availableDoctors.add(doc);
                }
            }
        }
        return availableDoctors;
    }

    private boolean isDateWithinDutyDay(String dutySchedule, LocalDate date) {
        // Example dutySchedule: "Sat-Sun 10:00-18:00"
        String[] parts = dutySchedule.trim().split(" "); // ["Sat-Sun", "10:00-18:00"]
        String[] days = parts[0].split("-");             // ["Sat", "Sun"]

        DayOfWeek startDay = parseShortDayName(days[0].trim());
        DayOfWeek endDay = parseShortDayName(days[1].trim());
        DayOfWeek consultationDay = date.getDayOfWeek();

        // Handle normal or wrapping cases like Fri-Mon
        if (startDay.getValue() <= endDay.getValue()) {
            return consultationDay.getValue() >= startDay.getValue()
                    && consultationDay.getValue() <= endDay.getValue();
        } else {
            return consultationDay.getValue() >= startDay.getValue()
                    || consultationDay.getValue() <= endDay.getValue();
        }
    }

    private boolean isTimeWithinDutyTime(String dutySchedule, LocalTime time) {
        String[] parts = dutySchedule.trim().split(" "); // ["Sat-Sun", "10:00-18:00"]
        String[] times = parts[1].split("-");            // ["10:00", "18:00"]

        LocalTime startTime = LocalTime.parse(times[0].trim());
        LocalTime endTime = LocalTime.parse(times[1].trim());

        return !time.isBefore(startTime) && !time.isAfter(endTime);
    }

// Helper: Map short day names (case-insensitive)
    private DayOfWeek parseShortDayName(String shortDay) {
        switch (shortDay.toLowerCase()) {
            case "mon" -> {
                return DayOfWeek.MONDAY;
            }
            case "tue" -> {
                return DayOfWeek.TUESDAY;
            }
            case "wed" -> {
                return DayOfWeek.WEDNESDAY;
            }
            case "thu" -> {
                return DayOfWeek.THURSDAY;
            }
            case "fri" -> {
                return DayOfWeek.FRIDAY;
            }
            case "sat" -> {
                return DayOfWeek.SATURDAY;
            }
            case "sun" -> {
                return DayOfWeek.SUNDAY;
            }
            default ->
                throw new IllegalArgumentException("Invalid day: " + shortDay);
        }
    }

    public void listConsultations() {
        if (consultationList.isEmpty()) {
            System.out.println("No consultations found.");
        } else {
            Utils.printCenteredTitle("ALL CONSULTATIONS", 115);
            ui.displayConsultationHeader();
            for (Consultation c : consultationList) {
                System.out.println(c);
            }
            Utils.printDivider(115);
        }
    }

    public void listAwaitingAppointment() {
        if (consultationList.isEmpty()) {
            System.out.println("No consultations found.");
        } else {
            Utils.printCenteredTitle("SCHEDULED CONSULTATIONS", 115);
            ui.displayConsultationHeader();
            for (Consultation c : consultationList) {
                if (c.getStatus() == Consultation.Status.SCHEDULED) {
                    System.out.println(c);
                }
            }
            Utils.printDivider(115);
        }
    }

    public void listCheckInAppointment() {
        if (consultationList.isEmpty()) {
            System.out.println("No consultations found.");
        } else {
            Utils.printCenteredTitle("CHECKED-IN CONSULTATIONS", 115);
            ui.displayConsultationHeader();
            for (Consultation c : consultationList) {
                if (c.getStatus() == Consultation.Status.CHECKED_IN) {
                    System.out.println(c);
                }
            }
            Utils.printDivider(115);
        }
    }

    public void cancelAppointment() {
        // Step 1: Check if there are scheduled consultations
        boolean hasScheduled = false;
        for (Consultation c : consultationList) {
            if (c.getStatus() == Consultation.Status.SCHEDULED) {
                hasScheduled = true;
                break;
            }
        }

        if (!hasScheduled) {
            System.out.println("No scheduled consultations available to cancel.");
            return; // go back to main menu
        }

        // Step 2: Show all scheduled consultations
        listAwaitingAppointment();

        // Step 3: Ask for ID
        String id = ui.getConsultationIDInput();
        if (id.equalsIgnoreCase("0")) { // if user typed 0 or cancelled
            System.out.println("Cancellation aborted by user.");
            return;
        }

        // Step 4: Try to cancel
        boolean success = cancelSearchedAppointment(id);

        // Step 5: Display result
        if (success) {
            System.out.println("Consultation " + id + " has been cancelled and removed.");
        } else {
            System.out.println("Consultation " + id + " not found or not eligible for cancellation.");
        }
    }

    // Cancel consultation by ID (update the status to CANCELLED)
    public boolean cancelSearchedAppointment(String consultationId) {
        for (Consultation c : consultationList) {
            if (c.getConsultationID().equalsIgnoreCase(consultationId)
                    && c.getStatus() == Consultation.Status.SCHEDULED) {

                c.setStatus(Consultation.Status.CANCELLED);  // âœ… mark cancelled
                dao.updateRecordInFile(CONSUL_FILE, consultationId, 7, "CANCELLED"); // update file
                return true;
            }
        }
        return false;
    }

    public void updateConsultation() {
        boolean exit = false;
        while (!exit) {
            ui.UpdateConsultationAppointmentMenu();
            int choice = ui.getChoice();
            switch (choice) {
                case 1:
                    rescheduleConsultation();
                    break;
                case 2:
                    appointmentCheckin();
                    break;
                case 3:
                    appointmentComplete();
                    break;
                case 4:
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid choice, please try again.");
            }
        }
    }

    public void rescheduleConsultation() {
        listAwaitingAppointment();

        String id = ui.getConsultationIDInput();
        if (id.equalsIgnoreCase("0")) {
            System.out.println("Update cancelled by user.");
            return;
        }

        Consultation consultation = findById(id);
        if (consultation == null) {
            System.out.println("Consultation not found.");
            return;
        }

        System.out.println("Current Date: " + consultation.getDate() + ", Time: " + consultation.getTime());
        String newDate = Utils.getValidatedDate();
        String newTime = Utils.getValidatedTime();

        // Update the consultation details
        consultation.setStatus(Consultation.Status.SCHEDULED);
        consultation.setDate(newDate);
        consultation.setTime(newTime);

        dao.updateRecordInFile(CONSUL_FILE, id, 5, newDate);
        dao.updateRecordInFile(CONSUL_FILE, id, 6, newTime);

        System.out.println("Consultation rescheduled successfully.");
    }

    public Consultation findById(String id) {
        for (Consultation c : consultationList) {  // iterate over your consultations list
            if (c.getConsultationID().equals(id)) { // compare IDs as string
                return c;
            }
        }
        return null; // not found
    }

    public boolean appointmentCheckin() {
        listAwaitingAppointment();

        String id = ui.getConsultationIDInput(); // <-- Change UI input to String
        if (id.equalsIgnoreCase("0")) {
            System.out.println("Update cancelled by user.");
            return false;
        }

        boolean success = updateStatusToCheckIn(id);
        if (success) {
            System.out.println("Consultation " + id + " status updated to CHECKED_IN.");
        } else {
            System.out.println("Consultation " + id + " not found or not eligible.");
        }
        return success;
    }

    public boolean appointmentComplete() {
        listCheckInAppointment();

        String id = ui.getConsultationIDInput();
        if (id.equalsIgnoreCase("0")) {
            System.out.println("Update cancelled by user.");
            return false;
        }

        boolean success = updateStatusToCompleted(id);

        if (success) {
            System.out.println("Consultation " + id + " status updated to COMPLETED.");
        } else {
            System.out.println("Consultation " + id + " not found or not eligible.");
        }
        return success;
    }

// Update status to CHECKED_IN
    public boolean updateStatusToCheckIn(String consultationID) {
        for (Consultation c : consultationList) {   // âœ… use the list's iterator
            if (c.getConsultationID().equalsIgnoreCase(consultationID)) {
                if (c.getStatus() == Consultation.Status.SCHEDULED) {
                    c.setStatus(Consultation.Status.CHECKED_IN);
                    dao.updateRecordInFile(CONSUL_FILE, consultationID, 7, "CHECKED_IN");
                    return true;
                } else {
                    System.out.println("Consultation is not in SCHEDULED state.");
                    return false;
                }
            }
        }
        return false; // Not found
    }

// Update status to COMPLETED
    public boolean updateStatusToCompleted(String consultationID) {
        for (Consultation c : consultationList) {
            if (c.getConsultationID().equalsIgnoreCase(consultationID)) {
                if (c.getStatus() == Consultation.Status.CHECKED_IN) {
                    c.setStatus(Consultation.Status.COMPLETED);

                    //Update the file immediately, no need do the updateRecordInfile at the appoinmentComplete()
                    dao.updateRecordInFile(CONSUL_FILE, consultationID, 7, "COMPLETED");

                    return true;
                } else {
                    System.out.println("Consultation is not in CHECKED_IN state.");
                    return false;
                }
            }
        }
        return false; // Not found
    }

    public void searchAppointment() {
        boolean exit = false;
        ui.displaySearchConsultationMenu();
        int choice = ui.getChoice();
        while (!exit) {
            switch (choice) {
                case 1:
                    searchByDateTime();
                    break;
                case 2:
                    searchByPatient();
                    break;
                case 3:
                    searchByDoctor();
                    break;
                case 4:
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid choice, please try again.");
            }
        }
    }

    private void searchByDateTime() {
        String date = Utils.getValidatedDateFormat();
        String time = Utils.getValidatedTime();

        Utils.printCenteredTitle("SEARCH RESULTS BY DATE & TIME", 115);
        ui.displayConsultationHeader();
        boolean found = false;

        for (Consultation c : consultationList) {
            if (c.getDate().equals(date) && c.getTime().equals(time)) {
                System.out.println(c);
                found = true;
            }
        }

        if (!found) {
            System.out.println("No consultations found for the given date and time.");
        }
        Utils.printDivider(115);
    }

    private void searchByPatient() {
        System.out.print("Enter Patient IC or Name: ");
        String keyword = scanner.nextLine().trim();

        Utils.printCenteredTitle("SEARCH RESULTS BY PATIENT", 115);
        ui.displayConsultationHeader();
        boolean found = false;

        for (Consultation c : consultationList) {
            if (c.getPatientIC().equalsIgnoreCase(keyword)
                    || c.getPatientName().toLowerCase().contains(keyword.toLowerCase())) {
                System.out.println(c);
                found = true;
            }
        }

        if (!found) {
            System.out.println("No consultations found for the given patient.");
        }
        Utils.printDivider(115);
    }

    private void searchByDoctor() {
        System.out.print("Enter Doctor ID or Name: ");
        String keyword = scanner.nextLine().trim();

        Utils.printCenteredTitle("SEARCH RESULTS BY DOCTOR", 115);
        ui.displayConsultationHeader();
        boolean found = false;

        for (Consultation c : consultationList) {
            if (c.getDoctorID().equalsIgnoreCase(keyword)
                    || c.getDoctorName().toLowerCase().contains(keyword.toLowerCase())) {
                System.out.println(c);
                found = true;
            }
        }

        if (!found) {
            System.out.println("No consultations found for the given doctor.");
        }
        Utils.printDivider(115);
    }

    public SortedLinkedList<Consultation> readConnsultationFromFile() {
        SortedLinkedList<Consultation> list = dao.readTextFileAsSortedLinkedList(CONSUL_FILE, 8, this::parseConsultationFromParts);

        // âœ… Sync Consultation ID counter with the latest from file
        int maxId = 1000;
        for (Consultation c : list) {
            String idStr = c.getConsultationID().substring(1); // remove "C"
            try {
                int idNum = Integer.parseInt(idStr);
                if (idNum > maxId) {
                    maxId = idNum;
                }
            } catch (NumberFormatException e) {
                System.err.println("Invalid consultation ID in file: " + c.getConsultationID());
            }
        }
        Consultation.syncCounter(maxId);

        return list;
    }

    public ArrayList<Consultation> readConsultationFromFileAsArrayList() {
        ArrayList<Consultation> list = dao.readTextFileAsArrayList(CONSUL_FILE, 8, this::parseConsultationFromParts);

        // âœ… Sync ID counter here too
        int maxId = 1000;
        for (int i = 1; i <= list.getNumberOfEntries(); i++) {
            Consultation c = list.getEntry(i);
            String idStr = c.getConsultationID().substring(1);
            try {
                int idNum = Integer.parseInt(idStr);
                if (idNum > maxId) {
                    maxId = idNum;
                }
            } catch (NumberFormatException e) {
                System.err.println("Invalid consultation ID in file: " + c.getConsultationID());
            }
        }
        Consultation.syncCounter(maxId);

        return list;
    }

    private Consultation parseConsultationFromParts(String[] parts) {
        try {
            // ID#PatientIC#PatientName#DoctorID#DoctorName#Date#Time#Status
            String id = parts[0].trim();
            String patientIC = parts[1].trim();
            String patientName = parts[2].trim();
            String doctorID = parts[3].trim();
            String doctorName = parts[4].trim();
            String date = parts[5].trim();
            String time = parts[6].trim();
            Consultation.Status status = Consultation.Status.valueOf(parts[7].trim().toUpperCase());

            return new Consultation(id, patientIC, patientName, doctorID, doctorName, date, time, status);
        } catch (Exception e) {
            System.err.println("Error parsing consultation record: " + e.getMessage());
            return null; // skip invalid line
        }
    }

    public void generateConsultationReport() {
        boolean exit = false;
        while (!exit) {
            ui.generateConsultationAppointmentMenu();
            int choice = ui.getChoice();
            switch (choice) {
                case 1:
                    int year = ui.enterYear();
                    if (year != 0) {
                        generateYearlyConsultationSummaryReport(year);
                    }
                    break;
                case 2:
                    int y = ui.enterYear();
                    if (y != 0) {
                        int m = ui.enterMonth();
                        if (m != 0) {
                            generateMonthlyConsultationSummaryReport(y, m);
                        }
                    }
                    break;
                case 3:
                    System.out.println("Exiting consultation report menu...");
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        }

    }

    public void generateYearlyConsultationSummaryReport(int year) {
        int[] totalByMonth = new int[12];
        int[] scheduledByMonth = new int[12];
        int[] completedByMonth = new int[12];
        int[] cancelledByMonth = new int[12];
        int totalYear = 0, completedYear = 0, cancelledYear = 0, scheduledYear = 0;

        // Count consultations
        for (Consultation c : consultationList) {
            LocalDate d = LocalDate.parse(c.getDate()); // YYYY-MM-DD
            if (d.getYear() == year) {
                int monthIndex = d.getMonthValue() - 1;
                totalByMonth[monthIndex]++;
                totalYear++;
                if (c.getStatus() == Consultation.Status.COMPLETED) {
                    completedByMonth[monthIndex]++;
                    completedYear++;
                } else if (c.getStatus() == Consultation.Status.CANCELLED) {
                    cancelledByMonth[monthIndex]++;
                    cancelledYear++;
                } else if (c.getStatus() == Consultation.Status.SCHEDULED) {
                    scheduledByMonth[monthIndex]++;
                    scheduledYear++;
                }
            }
        }

        // Print header
        Utils.printCenteredTitle("CONSULTATIONS YEARLY SUMMARY REPORT (" + year + ")", 115);
        System.out.printf("%-10s %-15s %-20s %-20s %-20s %-20s%n",
                "Month", "Total", "% of Year", "Scheduled", "Completed", "Cancelled");

        // Print all 12 months
        for (int i = 0; i < 12; i++) {
            double percentOfYear = (totalYear == 0) ? 0.0 : (totalByMonth[i] * 100.0) / totalYear;
            double percentScheduled = (totalByMonth[i] == 0) ? 0.0 : (scheduledByMonth[i] * 100.0) / totalByMonth[i];
            double percentCompleted = (totalByMonth[i] == 0) ? 0.0 : (completedByMonth[i] * 100.0) / totalByMonth[i];
            double percentCancelled = (totalByMonth[i] == 0) ? 0.0 : (cancelledByMonth[i] * 100.0) / totalByMonth[i];

            System.out.printf("%-10s %-15d %-20s %-20s %-20s %-20s%n",
                    LocalDate.of(2000, i + 1, 1).getMonth(),
                    totalByMonth[i],
                    String.format("%.2f%%", percentOfYear),
                    String.format("%d (%.2f%%)", scheduledByMonth[i], percentScheduled),
                    String.format("%d (%.2f%%)", completedByMonth[i], percentCompleted),
                    String.format("%d (%.2f%%)", cancelledByMonth[i], percentCancelled));
        }

        // Totals
        double completedPercent = (totalYear == 0) ? 0.0 : (completedYear * 100.0) / totalYear;
        double cancelledPercent = (totalYear == 0) ? 0.0 : (cancelledYear * 100.0) / totalYear;
        double scheduledPercent = (totalYear == 0) ? 0.0 : (scheduledYear * 100.0) / totalYear;

        Utils.printDivider(115);
        System.out.printf("%-10s %-15d %-20s %-20s %-20s %-20s%n",
                "TOTAL",
                totalYear,
                (totalYear == 0 ? "0.00%" : "100.00%"),
                String.format("%d (%.2f%%)", scheduledYear, scheduledPercent),
                String.format("%d (%.2f%%)", completedYear, completedPercent),
                String.format("%d (%.2f%%)", cancelledYear, cancelledPercent));
        Utils.printDivider(115);

        Utils.printCenteredTitle("CLICK ENTER TO CONTINUE", 115);
        scanner.nextLine();
    }

    public void generateMonthlyConsultationSummaryReport(int year, int month) {
        // Find how many days in this month
        YearMonth ym = YearMonth.of(year, month);
        int daysInMonth = ym.lengthOfMonth();

        int[] totalByDay = new int[daysInMonth];
        int[] scheduledByDay = new int[daysInMonth];
        int[] completedByDay = new int[daysInMonth];
        int[] cancelledByDay = new int[daysInMonth];

        int totalMonth = 0, completedMonth = 0, cancelledMonth = 0, scheduledMonth = 0;

        // Count consultations for the selected month
        for (Consultation c : consultationList) {
            LocalDate d = LocalDate.parse(c.getDate()); // YYYY-MM-DD
            if (d.getYear() == year && d.getMonthValue() == month) {
                int dayIndex = d.getDayOfMonth() - 1;
                totalByDay[dayIndex]++;
                totalMonth++;
                if (c.getStatus() == Consultation.Status.COMPLETED) {
                    completedByDay[dayIndex]++;
                    completedMonth++;
                } else if (c.getStatus() == Consultation.Status.CANCELLED) {
                    cancelledByDay[dayIndex]++;
                    cancelledMonth++;
                } else if (c.getStatus() == Consultation.Status.SCHEDULED) {
                    scheduledByDay[dayIndex]++;
                    scheduledMonth++;
                }
            }
        }

        // Print header
        Utils.printCenteredTitle("CONSULTATIONS MONTHLY SUMMARY REPORT (" + ym.getMonth() + " " + year + ")", 115);
        System.out.printf("%-10s %-15s %-20s %-20s %-20s %-20s%n",
                "Day", "Total", "% of Month", "Scheduled", "Completed", "Cancelled");

        // Print day breakdown
        for (int i = 0; i < daysInMonth; i++) {
            double percentOfMonth = (totalByDay[i] * 100.0) / (totalMonth == 0 ? 1 : totalMonth);
            double percentScheduled = (scheduledByDay[i] * 100.0) / (totalByDay[i] == 0 ? 1 : totalByDay[i]);
            double percentCompleted = (completedByDay[i] * 100.0) / (totalByDay[i] == 0 ? 1 : totalByDay[i]);
            double percentCancelled = (cancelledByDay[i] * 100.0) / (totalByDay[i] == 0 ? 1 : totalByDay[i]);

            System.out.printf("%-10d %-15d %-20s %-20s %-20s %-20s%n",
                    i + 1,
                    totalByDay[i],
                    (totalMonth == 0 ? "0.00%" : String.format("%.2f%%", percentOfMonth)),
                    String.format("%d (%.2f%%)", scheduledByDay[i], percentScheduled),
                    String.format("%d (%.2f%%)", completedByDay[i], percentCompleted),
                    String.format("%d (%.2f%%)", cancelledByDay[i], percentCancelled));
        }

        // Totals
        double completedPercent = 0.0;
        double cancelledPercent = 0.0;
        double scheduledPercent = 0.0;
        if (totalMonth != 0) {
            completedPercent = (completedMonth * 100.0) / totalMonth;
            cancelledPercent = (cancelledMonth * 100.0) / totalMonth;
            scheduledPercent = (scheduledMonth * 100.0) / totalMonth;
        }
        Utils.printDivider(115);
        System.out.printf("%-10s %-15d %-20s %-20s %-20s %-20s%n",
                "TOTAL",
                totalMonth,
                (totalMonth == 0 ? "0.00%" : "100.00%"),
                String.format("%d (%.2f%%)", scheduledMonth, scheduledPercent),
                String.format("%d (%.2f%%)", completedMonth, completedPercent),
                String.format("%d (%.2f%%)", cancelledMonth, cancelledPercent));
        Utils.printDivider(115);
        Utils.printCenteredTitle("CLICK ENTER TO CONTINUE", 115);
        scanner.nextLine();
    }
}
