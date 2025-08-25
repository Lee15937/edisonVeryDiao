package entity;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Objects;

public class Treatment implements Iterable<Treatment>, Serializable {

    private static final long serialVersionUID = 1L;

    private String treatmentId;
    private String patientName;
    private String doctorName; // wait sohai edison done the consultation if he does not done i died :)
    private String diagnosis;
    private String treatmentDetails;
    private boolean paymentStatus;
    private Date treatmentDate;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");

    public Treatment() {
        this.treatmentDate = new Date();
    }

    public Treatment(String treatmentId) {
        this.treatmentId = treatmentId;
    }

    public Treatment(String treatmentId, String patientName, String doctorName, String diagnosis, String treatmentDetails, boolean paymentStatus) {
        this.treatmentId = treatmentId;
        this.patientName = patientName;
        this.doctorName = doctorName;
        this.diagnosis = diagnosis;
        this.treatmentDetails = treatmentDetails;
        this.paymentStatus = paymentStatus;
        this.treatmentDate = new Date();   // current date
    }

    public String getTreatmentId() {
        return treatmentId;
    }

    public void setTreatmentId(String treatmentId) {
        this.treatmentId = treatmentId;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getTreatmentDetails() {
        return treatmentDetails;
    }

    public void setTreatmentPlan(String treatmentDetails) {
        this.treatmentDetails = treatmentDetails;
    }

    public boolean isPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(boolean paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
    
    public Date getTreatmentDate() {
        return treatmentDate;
    }

    public void setTreatmentDate(Date treatmentDate) {
        this.treatmentDate = treatmentDate;
    }

    public String getFormattedTreatmentDate() {
        return DATE_FORMAT.format(treatmentDate);
    }

    @Override
    public String toString() {
        return treatmentId + "#" + patientName + "#" + doctorName + "#" + diagnosis + "#" + treatmentDetails + "#" + treatmentDate + "#" + (paymentStatus ? "Pay" : "Unpay");
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.treatmentId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Treatment other = (Treatment) obj;
        return Objects.equals(this.treatmentId, other.treatmentId);
    }

    @Override
    public Iterator<Treatment> iterator() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
