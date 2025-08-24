package entity;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class Consultation implements Comparable<Consultation> {

    private static final AtomicInteger COUNTER = new AtomicInteger(1000);
    // Auto-increment ID starting from 1000

    private final int consultationID;
    private String patientName;
    private String doctorName;
    private String date; // Format: YYYY-MM-DD
    private String time; // Format: HH:MM (24-hour)
    private Status status;

    // Enum for consultation status
    public enum Status {
        SCHEDULED, CHECKED_IN, COMPLETED
    }

    public Consultation(String patientName, String doctorName, String date, String time) {
        this.consultationID = COUNTER.getAndIncrement(); // assign unique ID
        this.patientName = patientName;
        this.doctorName = doctorName;
        this.date = date;
        this.time = time;
        this.status = Status.SCHEDULED;
    }

    // Getters
    public int getConsultationID() {
        return consultationID;
    }

    public String getPatientName() {
        return patientName;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public Status getStatus() {
        return status;
    }

    // Update status
    public void setStatus(Status status) {
        this.status = status;
    }

    // Compare two consultations by date, then time
    @Override
    public int compareTo(Consultation other) {
        int dateCompare = this.date.compareTo(other.date);
        if (dateCompare != 0) {
            return dateCompare; // earlier date first
        }
        return this.time.compareTo(other.time); // same date → compare time
    }

    // Check if two consultations conflict (same doctor, same date, within 15 mins)
    public boolean conflictsWith(Consultation other) {
        // Only matters if same doctor & same date
        if (!this.doctorName.equalsIgnoreCase(other.doctorName)
                || !this.date.equals(other.date)) {
            return false;
        }

        // Parse times into minutes since midnight
        int thisMinutes = toMinutes(this.time);
        int otherMinutes = toMinutes(other.time);

        // If within 15 minutes either way -> conflict
        return Math.abs(thisMinutes - otherMinutes) < 15;
    }

    // Helper method to convert HH:mm to minutes
    private int toMinutes(String time) {
        String[] parts = time.split(":");
        int hour = Integer.parseInt(parts[0]);
        int minute = Integer.parseInt(parts[1]);
        return hour * 60 + minute;
    }

    // Override equals to compare by ID
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Consultation)) {
            return false;
        }
        Consultation other = (Consultation) obj;
        return consultationID == other.consultationID;
    }

    // Override hashCode
    @Override
    public int hashCode() {
        return Objects.hash(consultationID);
    }

    @Override
    public String toString() {
        return consultationID + "#" + date + "#" + time + "#" + patientName + "#" + doctorName + "#" + status + "#";
    }
}
