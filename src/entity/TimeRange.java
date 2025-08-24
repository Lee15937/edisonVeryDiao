/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import java.time.LocalTime;

public class TimeRange {

    private LocalTime start;
    private LocalTime end;

    public TimeRange(LocalTime start, LocalTime end) {
        this.start = start;
        this.end = end;
    }

    public LocalTime getStart() {
        return start;
    }

    public void setStart(LocalTime start) {
        this.start = start;
    }

    public LocalTime getEnd() {
        return end;
    }

    public void setEnd(LocalTime end) {
        this.end = end;
    }

    public boolean contains(TimeRange other) {
        return !other.start.isBefore(this.start) && !other.end.isAfter(this.end);
    }

    public boolean overlapsWith(TimeRange other) {
        return !this.end.isBefore(other.start) && !other.end.isBefore(this.start);
    }

    public String toString() {
        return start + "-" + end;
    }
}
