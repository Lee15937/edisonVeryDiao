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
        Doctor foundDoctor = findDoctorById(doctorId);

        // display doctor detail
        if (foundDoctor != null) {
            doctorMgmtUI.searchDoctorDetail(foundDoctor);

            DoctorEvent event = findDoctorEvent(foundDoctor.getDoctorId());
            if (event != null) {
                boolean firstLine = true;
                ListInterface<TimeRange> shifts = event.getShiftRanges();
                for (int j = 1; j <= shifts.getNumberOfEntries(); j++) {
                    TimeRange tr = shifts.getEntry(j);
                    if (firstLine) {
                        System.out.println("Shift        : " + tr.toString());
                        firstLine = false;
                    } else {
                        System.out.println("               " + tr.toString());
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

    private Doctor reqdoctorMgmtUIreDoctor(String doctorId) {
        Doctor doctor = findDoctorById(doctorId);
        if (doctor == null) {
            doctorMgmtUI.showMessage("Doctor with ID " + doctorId + " not found.");
        }
        return doctor;
    }

    private DoctorEvent findDoctorEvent(String doctorId) {
        for (int i = 1; i <= doctorEventList.getNumberOfEntries(); i++) {
            DoctorEvent e = doctorEventList.getEntry(i);
            if (e.getDoctorId().equalsIgnoreCase(doctorId)) {
                return e;
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
        doctorMgmtUI.printDoctorListHeader();
        boolean hasRows = false;

        for (int i = 1; i <= doctorList.getNumberOfEntries(); i++) {
            Doctor doc = doctorList.getEntry(i);
            if (doc != null) {
                hasRows = true;
                doctorMgmtUI.printDoctorListRow(doc);
            }
        }

        if (!hasRows) {
            doctorMgmtUI.printDoctorListNoData();
        }
    }

    public void displayDutyScheduleList() {
        doctorMgmtUI.printDutyScheduleHeader();
        boolean hasRows = false;

        for (int i = 1; i <= doctorList.getNumberOfEntries(); i++) {
            Doctor doc = doctorList.getEntry(i);
            if (doc != null) {
                hasRows = true;
                doctorMgmtUI.printDutyScheduleRow(doc);
            }
        }

        if (!hasRows) {
            doctorMgmtUI.printDutyScheduleNoData();
        }
    }

    public void displayShiftList() {
        doctorMgmtUI.printShiftHeader();
        boolean hasData = false;

        for (int i = 1; i <= doctorList.getNumberOfEntries(); i++) {
            Doctor d = doctorList.getEntry(i);
            DoctorEvent event = findDoctorEvent(d.getDoctorId());

            if (event != null && event.getShiftRanges().getNumberOfEntries() > 0) {
                hasData = true;

                doctorMgmtUI.printShiftRowFirst(d, event.getShiftRanges().getEntry(1));

                // Print remaining shifts
                for (int j = 2; j <= event.getShiftRanges().getNumberOfEntries(); j++) {
                    doctorMgmtUI.printShiftRowEmpty(event.getShiftRanges().getEntry(j));
                }
            }
        }

        if (!hasData) {
            doctorMgmtUI.printShiftNoData();
        }
    }

    public void displayLeaveList() {
        doctorMgmtUI.printLeaveHeader();
        boolean hasRows = false;

        for (int i = 1; i <= doctorEventList.getNumberOfEntries(); i++) {
            DoctorEvent event = doctorEventList.getEntry(i);
            if (event.isLeave()) {
                Doctor doc = findDoctorById(event.getDoctorId());
                if (doc != null) {
                    hasRows = true;
                    doctorMgmtUI.printLeaveRow(doc, event);
                }
            }
        }

        if (!hasRows) {
            doctorMgmtUI.printLeaveNoData();
        }
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
                    displayDoctorLeaveSummary();
                    break;
                case 3:
                    displayAvailabilitySummary();
                    break;
                case 4:
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

        doctorMgmtUI.printDutyScheduleSummaryHeader();
        for (int i = 0; i < days.length; i++) {
            doctorMgmtUI.printDutyScheduleSummaryRow(days[i], counts[i]);
        }
    }

    public void displayDoctorLeaveSummary() {
        doctorMgmtUI.printLeaveSummaryHeader();

        for (int i = 1; i <= doctorList.getNumberOfEntries(); i++) {
            Doctor doc = doctorList.getEntry(i);
            int qtys = getDoctorLeaveCount(doc);
            doctorMgmtUI.printLeaveSummaryRow(doc.getDoctorId(), "Dr. " + doc.getName(), qtys);
        }
    }

    public void displayAvailabilitySummary() {
        int availableCount = 0, notAvailableCount = 0;

        for (int i = 1; i <= doctorList.getNumberOfEntries(); i++) {
            Doctor d = doctorList.getEntry(i);
            if (d.isAvailability()) {
                availableCount++;
            } else {
                notAvailableCount++;
            }
        }

        doctorMgmtUI.printAvailabilitySummaryHeader();
        doctorMgmtUI.printAvailabilityRow("Available", availableCount);
        doctorMgmtUI.printAvailabilityRow("Not Available", notAvailableCount);
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

    public void trackAvailability() {
        String doctorId = doctorMgmtUI.getValidDoctorId();
        if (doctorId == null) {
            return;
        }
        Doctor selectedDoctor = reqdoctorMgmtUIreDoctor(doctorId);
        if (selectedDoctor == null) {
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
        Doctor targetDoctor = reqdoctorMgmtUIreDoctor(doctorId);
        if (targetDoctor == null) {
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

        // To reuse DoctorEvent if exists
        DoctorEvent doctorEvent = findDoctorEvent(doctorId);
        if (doctorEvent == null) {
            doctorEvent = DoctorEvent.Shift(doctorId, name, new adt.ArrayList<>());
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
        Doctor targetDoctor = reqdoctorMgmtUIreDoctor(doctorId);
        if (targetDoctor == null) {
            return;
        }

        String name = targetDoctor.getName();
        LocalDate startDate = doctorMgmtUI.getStartDate();
        int days = doctorMgmtUI.getLeaveDays();
        LocalDate endDate = startDate.plusDays(days);
        String reason = doctorMgmtUI.getReason();

        // To loop less
        DoctorEvent existingLeave = findDoctorEvent(doctorId);
        if (existingLeave != null && existingLeave.isLeave()) {
            LocalDate existingStart = existingLeave.getLeaveStartDate();
            LocalDate existingEnd = existingLeave.getLeaveEndDate();
            boolean overlap = !endDate.isBefore(existingStart) && !startDate.isAfter(existingEnd);
            if (overlap) {
                doctorMgmtUI.showMessage(String.format(
                        "Doctor %s already has leave from %s to %s (Reason: %s).",
                        name, existingStart, existingEnd, existingLeave.getLeaveReason()));
                return;
            }
        }

        // If no overlap, add new leave
        int qty = doctorEventList.getNumberOfEntries() + 1;
        DoctorEvent newLeave = DoctorEvent.Leave(doctorId, name, startDate, days, reason, qty);
        doctorEventList.add(newLeave);

        doctorMgmtUI.showMessage(
                String.format("Doctor %s is on leave from %s to %s. Reason: %s",
                        name, startDate, endDate, reason)
        );
    }

    public void editDoctor() {
        String doctorId = doctorMgmtUI.getValidDoctorId();
        if (doctorId == null) {
            return;
        }

        Doctor doc = reqdoctorMgmtUIreDoctor(doctorId);
        if (doc == null) {
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

        Doctor doc = reqdoctorMgmtUIreDoctor(doctorId);
        if (doc == null) {
            return;
        }

        if (!doctorMgmtUI.confirmRemoval(doc.getName())) {
            System.out.println("Removal cancelled.");
            return;
        }

        // Find index to remove
        int indexToRemove = -1;
        for (int i = 1; i <= doctorList.getNumberOfEntries(); i++) {
            if (doctorList.getEntry(i).getDoctorId().equalsIgnoreCase(doctorId)) {
                indexToRemove = i;
                break;
            }
        }

        if (indexToRemove != -1) {
            Doctor removed = doctorList.remove(indexToRemove);
            if (removed != null) {
                dao.saveToFile(doctorList, DOCTOR_FILE);
                System.out.println("â€¦ Doctor " + doc.getName() + " removed successfully.");
            } else {
                System.out.println("Failed to remove doctor.");
            }
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
