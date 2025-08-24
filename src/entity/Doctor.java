/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 *
 * @author kosoo
 */
public class Doctor implements Iterable<Doctor>, Serializable {

    private String doctorId;
    private String name;
    private String gender;
    private String phoneNo;
    private String email;
    private String dutySchedule;
    private boolean availability;
    private Date date;
    private List<DoctorEvent> events = new ArrayList<>();

    private static final long serialVersionUID = 1L;

    public Doctor() {

    }

    public Doctor(String doctorId) {
        this.doctorId = doctorId;
    }

    public Doctor(String doctorId, String name, String gender, String phoneNo, String email, String dutySchedule, boolean availability) {
        this.doctorId = doctorId;
        this.name = name;
        this.gender = gender;
        this.phoneNo = phoneNo;
        this.email = email;
        this.dutySchedule = dutySchedule;
        this.availability = availability;
        this.date = new Date();
    }

    public Doctor(String doctorId, String name, String gender, String phoneNo, String email) {
        this.doctorId = doctorId;
        this.name = name;
        this.gender = gender;
        this.phoneNo = phoneNo;
        this.email = email;
    }

    public Doctor(String name, String gender, String phoneNo, String email, String dutySchedule, boolean availability) {
        this.name = name;
        this.gender = gender;
        this.phoneNo = phoneNo;
        this.email = email;
        this.dutySchedule = dutySchedule;
        this.availability = availability;
        this.date = new Date();
    }

    public Doctor(String doctorId, String name) {
        this.doctorId = doctorId;
        this.name = name;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setDate(Date date) {
        this.date = date;
    }
    

    public String getDutySchedule() {
        return dutySchedule;
    }

    public void setDutySchedule(String dutySchedule) {
        this.dutySchedule = dutySchedule;
    }

    public boolean isAvailability() {
        return availability;
    }

    public void setAvailability(boolean availability) {
        this.availability = availability;
    }

    public void addLeave(DoctorEvent leave) {
        if (leave.getType() == DoctorEvent.EventType.LEAVE) {
            events.add(leave);
        }
    }

    public void addShift(DoctorEvent shift) {
        if (shift.getType() == DoctorEvent.EventType.SHIFT) {
            events.add(shift);
        }
    }

    public boolean isDateWithinDutyDay(String dutySchedule, LocalDate date) {
        String[] parts = dutySchedule.split(" ");
        if (parts.length < 1) {
            return false;
        }
        String daysPart = parts[0];

        Set<Integer> dutyDays = new HashSet<>();
        if (daysPart.contains("-")) {
            String[] range = daysPart.split("-");
            int start = dayOfWeekToInt(range[0]);
            int end = dayOfWeekToInt(range[1]);
            for (int i = start; i <= end; i++) {
                dutyDays.add(i);
            }
        } else {
            String[] days = daysPart.split(",");
            for (String d : days) {
                dutyDays.add(dayOfWeekToInt(d));
            }
        }
        int apptDay = date.getDayOfWeek().getValue();
        return dutyDays.contains(apptDay);
    }

    public boolean isTimeWithinDutyTime(String dutySchedule, LocalDate date, LocalTime time) {
        String[] parts = dutySchedule.split(" ");
        if (parts.length < 2) {
            return false;
        }
        String timePart = parts[1];
        String[] timeRange = timePart.split("-");
        if (timeRange.length != 2) {
            return false;
        }
        LocalTime startTime = parseTimeWithMin(timeRange[0]);
        LocalTime endTime = parseTimeWithMin(timeRange[1]);
        if (startTime == null || endTime == null) {
            return false;
        }

        if (!time.isBefore(endTime.minusMinutes(20))) {
            return false;
        }
        return !time.isBefore(startTime) && time.isBefore(endTime);
    }

    public LocalTime parseTimeWithMin(String timeStr) {
        try {
            timeStr = timeStr.replace(" ", "");
            int hour = 0, minute = 0;
            if (timeStr.endsWith("am") || timeStr.endsWith("pm")) {
                String base = timeStr.substring(0, timeStr.length() - 2);
                String[] parts = base.split(":");
                hour = Integer.parseInt(parts[0]);
                if (parts.length == 2) {
                    minute = Integer.parseInt(parts[1]);
                }
                if (timeStr.endsWith("am")) {
                    if (hour == 12) {
                        hour = 0;
                    }
                } else {
                    if (hour != 12) {
                        hour += 12;
                    }
                }
                if (hour < 0 || hour > 23 || minute < 0 || minute > 59) {
                    return null;
                }
                return LocalTime.of(hour, minute);
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    public int dayOfWeekToInt(String day) {
        switch (day.trim()) {
            case "Mon":
                return 1;
            case "Tue":
                return 2;
            case "Wed":
                return 3;
            case "Thu":
                return 4;
            case "Fri":
                return 5;
            case "Sat":
                return 6;
            case "Sun":
                return 7;
            default:
                return -1;
        }
    }

    public List<DoctorEvent> getLeaves() {
        return events.stream()
                .filter(e -> e.getType() == DoctorEvent.EventType.LEAVE)
                .toList();
    }

    public List<DoctorEvent> getShifts() {
        return events.stream()
                .filter(e -> e.getType() == DoctorEvent.EventType.SHIFT)
                .toList();
    }

    public boolean isOnLeave(LocalDate date) {
        for (DoctorEvent ev : getLeaves()) {
            if ((date.isEqual(ev.getLeaveStartDate()) || date.isAfter(ev.getLeaveStartDate()))
                    && (date.isEqual(ev.getLeaveEndDate()) || date.isBefore(ev.getLeaveEndDate()))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return doctorId + "#" + name + "#" + gender + "#" + phoneNo + "#" + email + "#" + dutySchedule + "#" + (availability ? "Available" : "Not Available");
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.doctorId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Doctor)) {
            return false;
        }
        Doctor other = (Doctor) obj;
        return Objects.equals(this.doctorId, other.doctorId);
    }

    @Override
    public Iterator<Doctor> iterator() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
