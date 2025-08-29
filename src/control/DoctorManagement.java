package control;

import DAO.Dao;
import adt.*;
import boundary.DoctorManagementUI;
import entity.Doctor;
import entity.DoctorEvent;
import entity.TimeRange;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class DoctorManagement {

    ListInterface<Doctor> doctorList = new ArrayList<>();
    ListInterface<DoctorEvent> doctorEventList = new adt.ArrayList<>();
    private Dao<Doctor> dao = new Dao<>();
    public static final String DOCTOR_FILE = "src/DAO/doctor.txt";
    DoctorManagementUI doctorMgmtUI = new DoctorManagementUI();

    public void runDoctorsManagement() {
        doctorList = readDoctorFromFileAsArrayList();
        int choice = 0;
        do {
            doctorMgmtUI.displayDoctorManagementMenu();
            choice = doctorMgmtUI.getMenuChoice();

            switch (choice) {
                case 1:
                    loadDoctorsFromFile();
                    Doctor doctor = doctorMgmtUI.inputDoctorDetails();
                    doctorList.add(doctor);
                    dao.saveToFile(doctorList, DOCTOR_FILE);
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
                    System.out.println("\nReturning to Main Menu...\n");
                    return;
                default:
                    System.out.println("\nInvalid choice.Please enter 1-8 only.");
            }
        } while (choice != 8);
    }

    public void searchDoctor() {
        String doctorId = doctorMgmtUI.getValidDoctorId();

        if (doctorId == null) {
            return;
        }

        ArrayList<Doctor> doctors = readDoctorFromFileAsArrayList();
        Doctor foundDoctor = null;

        for (Doctor d : doctors) {
            if (d.getDoctorId().equalsIgnoreCase(doctorId)) {
                foundDoctor = d;
                break;
            }
        }

        if (foundDoctor != null) {
            doctorMgmtUI.searchDoctorDetail(foundDoctor);

            for (int i = 1; i <= doctorEventList.getNumberOfEntries(); i++) {
                DoctorEvent event = doctorEventList.getEntry(i);
                if (event.getDoctorId().equalsIgnoreCase(foundDoctor.getDoctorId())) {
                    boolean firstLine = true;

                    ListInterface<TimeRange> shifts = event.getShiftRanges();
                    for (int j = 1; j <= event.getShiftRanges().getNumberOfEntries(); j++) {
                        TimeRange tr = event.getShiftRanges().getEntry(j);
                        if (firstLine) {
                            System.out.println("Shift        : " + tr.toString());
                            firstLine = false;
                        } else {
                            System.out.println("               " + tr.toString());
                        }
                    }
                }
            }
        } else {
            doctorMgmtUI.ChoiceYesOrNo();
        }
    }

    public ListInterface<Doctor> getAllDoctors() {
        return doctorList;
    }

    public ListInterface<DoctorEvent> getAllDoctorEvents() {
        return doctorEventList;
    }

    public ListInterface<DoctorEvent> getShifts() {
        ListInterface<DoctorEvent> result = doctorEventList;
        return result;
    }

    public ListInterface<DoctorEvent> getLeaves() {
        return doctorEventList;
    }

    public Doctor findDoctorById(String doctorId) {
        for (int i = 1; i <= doctorList.getNumberOfEntries(); i++) {
            Doctor d = doctorList.getEntry(i);
            if (d.getDoctorId().equalsIgnoreCase(doctorId)) {
                return d;
            }
        }
        return null;
    }

    public void doctorManagementList(ListInterface<Doctor> dortorList, ListInterface<DoctorEvent> dortorEventList) {
        while (true) {
            doctorMgmtUI.doctorListMenu();
            int action = doctorMgmtUI.doctorListMenuAction();
            switch (action) {
                case 1:
                    displayDoctorList();
                    break;
                case 2:
                    displayDutyScheduleList();
                    break;
                case 3:
                    displayShiftList();
                    break;
                case 4:
                    displayLeaveList();
                    break;
                case 5:
                    System.out.println("Returning to Doctor Management Menu...");
                    return;
                default:
                    System.out.println("Invalid choice. Please enter 1-5 only.");
            }
        }
    }

    public void displayDoctorList() {
        String header = String.format("| %-10s | %-20s |%-8s |%-20s |%-20s | %-25s |",
                "ID", "Name", "Gender", "Phone Number", "Email", "Availability");
        String line = "-".repeat(header.length());

        System.out.println(line);
        System.out.println(header);
        System.out.println(line);

        boolean hasRows = false;
        int availableCount = 0;
        int notAvailableCount = 0;

        ListInterface<Doctor> doctors = getAllDoctors();

        for (int i = 1; i <= doctors.getNumberOfEntries(); i++) {
            Doctor doc = doctors.getEntry(i);
            hasRows = true;

            boolean available = doc.isAvailability();
            if (available) {
                availableCount++;
            } else {
                notAvailableCount++;
            }

            System.out.printf("| %-10s | %-20s |%-8s |%-20s |%-20s | %-25s |\n",
                    doc.getDoctorId(), doc.getName(), doc.getGender(),
                    doc.getPhoneNo(), doc.getEmail(),
                    available ? "Available" : "Not Available");
        }

        if (!hasRows) {
            System.out.println("| No data available |");
        }
        System.out.println(line);
    }

    public void displayDutyScheduleList() {
        String header = String.format("| %-10s | %-20s | %-25s |", "ID", "Name", "Duty Schedule");
        String line = "-".repeat(header.length());

        System.out.println(line);
        System.out.println(header);
        System.out.println(line);

        boolean hasRows = false;
        ListInterface<Doctor> doctors = getAllDoctors();

        for (int i = 1; i <= doctors.getNumberOfEntries(); i++) {
            Doctor doc = doctors.getEntry(i);
            hasRows = true;
            System.out.printf("| %-10s | %-20s | %-25s |\n", doc.getDoctorId(), doc.getName(), doc.getDutySchedule());
        }

        if (!hasRows) {
            System.out.println("| No data available |");
        }
        System.out.println(line);
    }

    public void displayShiftList() {
        String header = String.format("| %-10s | %-20s | %-25s | %-25s |", "ID", "Name", "Duty Schedule", "Shift");
        String line = "-".repeat(header.length());

        System.out.println(line);
        System.out.println(header);
        System.out.println(line);
        boolean hasData = false;

        for (int i = 1; i <= doctorList.getNumberOfEntries(); i++) {
            Doctor d = doctorList.getEntry(i);
            if (d == null) {
                continue;
            }

            // Find doctorâ€™s events
            DoctorEvent targetEvent = null;
            for (int j = 1; j <= doctorEventList.getNumberOfEntries(); j++) {
                DoctorEvent e = doctorEventList.getEntry(j);
                if (e.getDoctorId().equalsIgnoreCase(d.getDoctorId())) {
                    targetEvent = e;
                    break;
                }
            }

            if (targetEvent != null && targetEvent.getShiftRanges().getNumberOfEntries() > 0) {
                hasData = true;
                boolean firstLine = true;
                for (int k = 1; k <= targetEvent.getShiftRanges().getNumberOfEntries(); k++) {
                    TimeRange tr = targetEvent.getShiftRanges().getEntry(k);
                    if (firstLine) {
                        System.out.printf("| %-10s | %-20s | %-25s | %-25s |\n",
                                d.getDoctorId(), d.getName(), d.getDutySchedule(), tr.toString());
                        firstLine = false;
                    } else {
                        System.out.printf("| %-10s | %-20s | %-25s | %-25s |\n",
                                "", "", "", tr.toString());
                    }
                }
            }
        }

        if (!hasData) {
            System.out.println("| No data available                                                                 |");
        }

        System.out.println(line);
    }

    public void displayLeaveList() {
        String header = String.format("| %-10s | %-20s | %-15s | %-15s | %-20s |","ID", "Name", "Start Date", "End Date", "Reason");
        String line = "-".repeat(header.length());

        System.out.println(line);
        System.out.println(header);
        System.out.println(line);

        boolean hasRows = false;
        ListInterface<DoctorEvent> events = getAllDoctorEvents();

        for (int i = 1; i <= events.getNumberOfEntries(); i++) {
            DoctorEvent event = events.getEntry(i);
            if (event.isLeave()) {
                Doctor doc = findDoctorById(event.getDoctorId());
                if (doc != null) {
                    hasRows = true;
                    System.out.printf("| %-10s | %-20s | %-15s | %-15s | %-20s |\n",
                            doc.getDoctorId(), doc.getName(),
                            event.getLeaveStartDate(), event.getLeaveEndDate(), event.getLeaveReason());
                }
            }
        }

        if (!hasRows) {
            System.out.println("| No data available |");
        }
        System.out.println(line);
    }

    public void summaryReports(ListInterface<Doctor> dortorList) {
        while (true) {
            doctorMgmtUI.summaryReportsMenu();
            int action = doctorMgmtUI.doctorListMenuAction();
            switch (action) {
                case 1:
                    displayDoctorDutyScheduleSummary();
                    break;
                case 2:
                    displayDoctorLeaveSummary(doctorList);
                    break;
                case 3:
                    System.out.println("Returning to Doctor Management Menu...");
                    return;
                default:
                    System.out.println("Invalid choice. Please enter 1-5 only.");
            }
        }
    }

    public void displayDoctorDutyScheduleSummary() {
        String[] days = {"MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN"};
        int[] counts = new int[7];

        for (int i = 1; i <= doctorList.getNumberOfEntries(); i++) {
            Doctor d = doctorList.getEntry(i);
            if (d == null) {
                continue;
            }

            String schedule = d.getDutySchedule();
            if (schedule == null || schedule.isEmpty()) {
                continue;
            }

            String dayPart = schedule.split(" ")[0];
            String[] parts = dayPart.split(",");

            for (String part : parts) {
                if (part.contains("-")) {
                    String[] range = part.split("-");
                    int start = dayIndex(range[0], days);
                    int end = dayIndex(range[1], days);

                    if (start != -1 && end != -1) {
                        for (int j = start; j <= end; j++) {
                            counts[j]++;
                        }
                    }
                } else {
                    int idx = dayIndex(part, days);
                    if (idx != -1) {
                        counts[idx]++;
                    }
                }
            }
        }

        String header = String.format("|  %-3s  | %-15s |", "Day", "Doctor Quantity");
        String line = "-".repeat(header.length());

        System.out.println(line);
        System.out.println("|    Duty Schedule Summary    |");
        System.out.println(line);
        System.out.println(header);
        System.out.println(line);

        boolean hasDuty = false;
        for (int i = 0; i < days.length; i++) {
            hasDuty = true;
            System.out.printf("|  %-3s  | %-15d |\n", days[i], counts[i]);
            System.out.println(line);
        }

        if (!hasDuty) {
            System.out.println("|    No Duty Schedule found in the system.      |");
            System.out.println(line);
        }
    }

    private int dayIndex(String day, String[] days) {
        String upper = day.substring(0, 3).toUpperCase();
        for (int i = 0; i < days.length; i++) {
            if (days[i].equals(upper)) {
                return i;
            }
        }
        return -1;
    }

    private void displayDoctorLeaveSummary(ListInterface<Doctor> doctorList) {
        String header = String.format("| %-10s | %-20s | %-8s |",
                "ID", "Name", "Quantity");
        String line = "-".repeat(header.length());

        System.out.println(line);
        System.out.println("|           Doctor Leave Summary Report        |");
        System.out.println(line);
        System.out.println(header);
        System.out.println(line);

        boolean hasLeave = false;

        for (int i = 1; i <= doctorList.getNumberOfEntries(); i++) {
            Doctor doc = doctorList.getEntry(i);
            int qtys = getDoctorLeaveCount(doc);
            hasLeave = true;

            String row = String.format("| %-10s | %-20s | %-8d |",
                    doc.getDoctorId(),
                    "Dr. " + doc.getName(),
                    qtys);
            System.out.println(row);
        }

        if (!hasLeave) {
            System.out.println("|     No leave found in the system.           |");
        }

        System.out.println(line);
    }

    public void trackAvailability() {
        String doctorId = doctorMgmtUI.getValidDoctorId();
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
            doctorMgmtUI.trackAvailabilityMenu();

            int action = doctorMgmtUI.doctorListMenuAction();

            switch (action) {
                case 1:
                    assignShift(selectedDoctor.getDoctorId());
                    break;
                case 2:
                    makeLeave(selectedDoctor.getDoctorId());
                    break;
                case 3:
                    System.out.println("Returning to Doctor Management Menu...");
                    return;
                default:
                    System.out.println("Invalid choice. Please enter 1-5 only.");
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
            doctorMgmtUI.showMessage("Doctor not found.");
            return;
        }

        String name = targetDoctor.getName();
        ListInterface<TimeRange> dutyRanges = parseDutySchedule(targetDoctor.getDutySchedule());
        if (dutyRanges.isEmpty()) {
            doctorMgmtUI.showMessage("\nInvalid duty schedule.");
            return;
        }

        doctorMgmtUI.showMessage("Allowed shift periods based on duty schedule:");
        for (int i = 1; i <= dutyRanges.getNumberOfEntries(); i++) {
            doctorMgmtUI.showMessage(" - " + dutyRanges.getEntry(i));
        }

        //  Find existing DoctorEvent (reuse if exists)
        DoctorEvent doctorEvent = null;
        for (int j = 1; j <= doctorEventList.getNumberOfEntries(); j++) {
            DoctorEvent e = doctorEventList.getEntry(j);
            if (e.getDoctorId().equalsIgnoreCase(doctorId)) {
                doctorEvent = e;
                break;
            }
        }
        if (doctorEvent == null) {
            doctorEvent = DoctorEvent.Shift(doctorId, name, new ArrayList<>());
            doctorEventList.add(doctorEvent);
        }

        ListInterface<TimeRange> assignedShifts = doctorEvent.getShiftRanges();

        while (true) {
            String shiftTimeStr = doctorMgmtUI.getShiftInput();
            if (shiftTimeStr.isEmpty()) {
                break;
            }

            TimeRange shiftRange = parseTimeRange(shiftTimeStr);
            if (shiftRange == null) {
                doctorMgmtUI.showMessage("\nInvalid time format or logic (end must be after start). Use HH:mm-HH:mm.");
                continue;
            }

            boolean withinDuty = false;
            for (int i = 1; i <= dutyRanges.getNumberOfEntries(); i++) {
                if (dutyRanges.getEntry(i).contains(shiftRange)) {
                    withinDuty = true;
                    break;
                }
            }
            if (!withinDuty) {
                doctorMgmtUI.showMessage("\nShift Time Not Within Allowed.");
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
                doctorMgmtUI.showMessage("\nShift overlaps with existing.");
                continue;
            }

            assignedShifts.add(shiftRange);
            doctorMgmtUI.showMessage("Shift added: " + shiftRange);
        }

        if (assignedShifts.isEmpty()) {
            doctorMgmtUI.showMessage("\nNo shifts assigned.");
            return;
        }

        doctorMgmtUI.showMessage("Shifts assigned to Doctor " + name + ":");
        for (int i = 1; i <= assignedShifts.getNumberOfEntries(); i++) {
            doctorMgmtUI.showMessage(" - " + assignedShifts.getEntry(i));
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
            doctorMgmtUI.showMessage("Doctor not found.");
            return;
        }

        String name = targetDoctor.getName();
        LocalDate startDate = doctorMgmtUI.getStartDate();
        int days = doctorMgmtUI.getLeaveDays();
        String reason = doctorMgmtUI.getReason();

        int qty = doctorEventList.getNumberOfEntries() + 1;
        DoctorEvent newLeave = DoctorEvent.Leave(doctorId, name, startDate, days, reason, qty);
        doctorEventList.add(newLeave);

        LocalDate endDate = startDate.plusDays(days);
        doctorMgmtUI.showMessage(
                String.format("Doctor %s is on leave from %s to %s. Reason: %s",
                        name, startDate, endDate, reason)
        );
    }

    public int getDoctorLeaveCount(Doctor doctor) {
        int count = 0;
        for (int i = 1; i <= doctorEventList.getNumberOfEntries(); i++) {
            DoctorEvent ev = doctorEventList.getEntry(i);
            if (ev.isLeave() && ev.getDoctorId().equalsIgnoreCase(doctor.getDoctorId())) {
                count++;
            }
        }
        return count;
    }

    public void editDoctor() {
        String id = doctorMgmtUI.getValidDoctorId();
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

        doc.setName(doctorMgmtUI.inputEditDoctorName(doc.getName()));
        doc.setPhoneNo(doctorMgmtUI.inputEditDoctorPhone(doc.getPhoneNo()));
        doc.setEmail(doctorMgmtUI.inputEditDoctorEmail(doc.getEmail()));
        doc.setDutySchedule(
                doctorMgmtUI.inputEditDoctorSchedule(doc.getDutySchedule(), doc, doctorEventList)
        );
        doc.setAvailability(doctorMgmtUI.inputEditDoctorAvailability(doc.isAvailability()));

        dao.saveToFile(doctorList, DOCTOR_FILE);
        System.out.println("\nDoctor Updated Successfully.");
    }

    public void removeDoctor() {
        String doctorId = doctorMgmtUI.getValidDoctorId();
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

        if (!doctorMgmtUI.confirmRemoval(doc.getName())) {
            System.out.println("Removal cancelled.");
            return;
        }

        Doctor removed = doctorList.remove(indexToRemove);
        if (removed != null) {
            dao.saveToFile(doctorList, DOCTOR_FILE);
            System.out.println("â€¦ Doctor " + doc.getName() + " removed successfully.");
        } else {
            System.out.println("Failed to remove doctor.");
        }
    }

    public ListInterface<TimeRange> parseDutySchedule(String dutySchedule) {
        ListInterface<TimeRange> ranges = new adt.ArrayList<>();
        String[] parts = dutySchedule.split(" ");
        if (parts.length < 2) {
            return ranges;
        }
        String timePart = parts[1];
        String[] timeRanges = timePart.split(",");
        for (String tr : timeRanges) {
            TimeRange range = parseTimeRange(tr.trim());
            if (range != null) {
                ranges.add(range);
            }
        }
        return ranges;
    }

    public TimeRange parseTimeRange(String s) {
        String[] split = s.split("-");
        if (split.length != 2) {
            return null;
        }
        LocalTime start = parseTime(split[0].trim());
        LocalTime end = parseTime(split[1].trim());
        if (start == null || end == null || !start.isBefore(end)) {
            return null;
        }
        return new TimeRange(start, end);
    }

    public LocalTime parseTime(String t) {
        try {
            t = t.trim();
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("H:mm");
            return LocalTime.parse(t, fmt);
        } catch (Exception e) {
            return null;
        }
    }

    public void loadDoctorsFromFile() {
        doctorList.clear();
        ArrayList<Doctor> loadedDoctors = dao.readTextFileAsArrayList(DOCTOR_FILE, 6, this::doctorMapper);

        if (loadedDoctors != null) {
            for (int i = 0; i < loadedDoctors.sizeOf(); i++) {
                Doctor doctor = loadedDoctors.get(i);
                if (doctor != null) {
                    doctorList.add(doctor);
                }
            }
        }

        if (doctorList.getNumberOfEntries() > 0) {
            Doctor lastDoctor = doctorList.getEntry(doctorList.getNumberOfEntries());
            String lastId = lastDoctor.getDoctorId();

            DoctorManagementUI doctorMgmtUI = new DoctorManagementUI();

            doctorMgmtUI.doctorIdCounter = Integer.parseInt(lastId.substring(1)) + 1;

        }
    }

    public Doctor doctorMapper(String[] parts) {
        try {
            String doctorId = parts[0].trim();
            String name = parts[1].trim();
            String gender = parts[2].trim();
            String phoneNo = parts[3].trim();
            String email = parts[4].trim();
            String dutySchedule = parts[5].trim();
            boolean availability = parts[6].trim().equalsIgnoreCase("Available");

            return new Doctor(doctorId, name, gender, phoneNo, email, dutySchedule, availability);
        } catch (Exception e) {
            System.out.println("Error parsing doctor record: " + String.join("#", parts));
            return null;
        }
    }

    public ArrayList<Doctor> readDoctorFromFileAsArrayList() {
        return dao.readTextFileAsArrayList(DOCTOR_FILE, 6, this::doctorMapper);
    }

    public static void DoctorManagementRun() {
        DoctorManagement doctormanagement = new DoctorManagement();
        doctormanagement.runDoctorsManagement();
    }

}
