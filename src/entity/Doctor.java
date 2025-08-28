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