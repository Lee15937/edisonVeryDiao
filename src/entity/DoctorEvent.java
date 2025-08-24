/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class DoctorEvent extends Doctor implements Serializable {

    public enum EventType {
        LEAVE, SHIFT
    }

    private EventType type;

    private int qty;

    private LocalDate leaveStartDate;
    private LocalDate leaveEndDate;
    private String leaveReason;

    private List<TimeRange> shiftRanges;

    public static DoctorEvent Leave(String id, String name,
            LocalDate start, int day,
            String reason, int qty) {
        DoctorEvent e = new DoctorEvent(id, name);
        e.type = EventType.LEAVE;
        e.leaveStartDate = start;
        e.leaveEndDate = start.plusDays(day);
        e.leaveReason = reason;
        e.qty = qty;
        return e;
    }

    public static DoctorEvent Shift(String id, String name,
            List<TimeRange> ranges) {
        DoctorEvent e = new DoctorEvent(id, name);
        e.type = EventType.SHIFT;
        e.shiftRanges = ranges;
        return e;
    }

    private DoctorEvent(String id, String name) {
        super(id, name);

    }

    public EventType getType() {
        return type;
    }

    public LocalDate getLeaveStartDate() {
        return leaveStartDate;
    }

    public LocalDate getLeaveEndDate() {
        return leaveEndDate;
    }

    public String getLeaveReason() {
        return leaveReason;
    }

    public List<TimeRange> getShiftRanges() {
        if (shiftRanges == null) {
            shiftRanges = new ArrayList<>();
        }
        return shiftRanges;
    }

    public int getQty() {
        return qty;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public void setLeaveStartDate(LocalDate leaveStartDate) {
        this.leaveStartDate = leaveStartDate;
    }

    public void setLeaveEndDate(LocalDate leaveEndDate) {
        this.leaveEndDate = leaveEndDate;
    }

    public void setLeaveReason(String leaveReason) {
        this.leaveReason = leaveReason;
    }

    public void setShiftRanges(List<TimeRange> shiftRanges) {
        this.shiftRanges = shiftRanges;
    }

    public boolean isLeave() {
        return type == EventType.LEAVE;
    }

    public boolean isShift() {
        return type == EventType.SHIFT;
    }

    @Override
    public String toString() {
        switch (type) {
            case LEAVE:
                return String.format("| %-20s | %-20s | %-20s|", leaveStartDate, leaveEndDate, leaveReason);
            case SHIFT:
                return shiftRanges.toString();
            default:
                return "Unknown Event";
        }
    }
}
