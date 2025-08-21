package entity;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Objects;

/**
 *
 * @author kosoo
 */
public class Patient implements Iterable<Patient>, Serializable {

    private static final long serialVersionUID = 1L;
    private static int counter = 1;
    private String patientId;
    private String IC;
    private String name;
    private String gender;
    private int age;
    private String phoneNo;
    private Date date;

    public Patient() {

    }

    public Patient(String patientId) {
        this.patientId = patientId;
    }

    public Patient(String patientId, String IC, String name, String gender, int age, String phoneNo) {
        this.patientId = Patient.generateNewId();
        this.IC = IC;
        this.name = name;
        this.gender = gender;
        this.age = age;
        this.phoneNo = phoneNo;
        this.date = new Date();
    }
    
    public static String generateNewId() {
        return String.format("P%03d", counter++);
    }

    public static void setCounter(int newCounter) {
        counter = newCounter;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getPatientId() {
        return patientId;
    }

    public String getIC() {
        return IC;
    }

    public String getName() {
        return name;
    }

    public String getGender() {
        return gender;
    }

    public int getAge() {
        return age;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public void setIC(String IC) {
        this.IC = IC;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy");
        String formattedDate = (date != null) ? sdf.format(date) : "N/A";
        return patientId + "#" + IC + "#" + name + "#" + gender + "#" + age + "#" + phoneNo + "#" + formattedDate;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.patientId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Patient)) {
            return false;
        }
        Patient other = (Patient) obj;
        return Objects.equals(this.patientId, other.patientId);
    }

    @Override
    public Iterator<Patient> iterator() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
