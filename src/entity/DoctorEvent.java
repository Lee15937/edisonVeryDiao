/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import adt.ListInterface;
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
    private ListInterface<TimeRange> shiftRanges = new adt.ArrayList<>();

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

    public static DoctorEvent Shift(String id, String name, ListInterface<TimeRange> ranges) {
        DoctorEvent e = new DoctorEvent(id, name);
        e.type = EventType.SHIFT;
        for (int i = 1; i <= ranges.getNumberOfEntries(); i++) {
            e.shiftRanges.add(ranges.getEntry(i));
        }
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

    public ListInterface<TimeRange> getShiftRanges() {
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

    public void setShiftRanges(ListInterface<TimeRange> shiftRanges) { // âœ… fixed type
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
                StringBuilder sb = new StringBuilder();
                for (int i = 1; i <= shiftRanges.getNumberOfEntries(); i++) {
                    sb.append(shiftRanges.getEntry(i).toString()).append(" ");
                }
                return sb.toString().trim();
            default:
                return "Unknown Event";
        }
    }
}
