package control;

import entity.Doctor;
import DAO.Dao;
import java.util.function.Function;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;
import adt.*;
import boundary.DoctorManagementUI;
import entity.Doctor;
import entity.TimeRange;
import java.time.LocalTime;
import java.util.List;

public class DoctorManagement {

    ListInterface<Doctor> doctorList = new ArrayList<>();

    private Dao<Doctor> dao = new Dao<>();

    public static final String DOCTOR_FILE = "src/DAO/doctor.txt";

    public List<TimeRange> parseDutySchedule(String dutySchedule) {
        java.util.List<TimeRange> ranges = new java.util.ArrayList<>();
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
            t = t.toLowerCase().replace(" ", "");
            int h, m = 0;
            String digits, ampm;
            if (t.endsWith("am") || t.endsWith("pm")) {
                ampm = t.substring(t.length() - 2);
                digits = t.substring(0, t.length() - 2);
                String[] p = digits.split(":");
                h = Integer.parseInt(p[0]);
                if (p.length == 2) {
                    m = Integer.parseInt(p[1]);
                }
                if (ampm.equals("am")) {
                    if (h == 12) {
                        h = 0;
                    }
                } else {
                    if (h != 12) {
                        h += 12;
                    }
                }
                return LocalTime.of(h, m);
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    public ListInterface<Doctor> getDoctorList() {
        return doctorList;
    }

    public void loadDoctorsFromFile() {
        ArrayList<Doctor> loadedDoctors = dao.readTextFileAsArrayList(DOCTOR_FILE, 6, this::doctorMapper);

        if (loadedDoctors != null) {
            for (int i = 0; i < loadedDoctors.sizeOf(); i++) {
                Doctor doctor = loadedDoctors.get(i);
                if (doctor != null) {
                    doctorList.add(doctor);
                }
            }
        }

        System.out.println("Loaded " + doctorList.getNumberOfEntries() + " doctors.");

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

    public void saveDoctorsToFile() {
        dao.saveToFile(doctorList, DOCTOR_FILE);
        System.out.println("Doctor data saved.");
    }

    public static void DoctorManagementRun() {
        boundary.DoctorManagementUI doctormanagement = new boundary.DoctorManagementUI();
        doctormanagement.runDoctorsManagement();
    }
}
