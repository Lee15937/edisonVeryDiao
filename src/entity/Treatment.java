package entity;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Objects;

public class Treatment implements Iterable<Treatment>, Serializable {

    private static final long serialVersionUID = 1L;

    private String treatmentId;
    private String patientIC;
    private String patientName;
    private String doctorName;
    private String diagnosis;
    private String treatmentDetails;
    private int quantity;
    private boolean paymentStatus;
    private Date treatmentDate;

    public Treatment() {
        this.treatmentDate = new Date();
    }

    public Treatment(String treatmentId) {
        this.treatmentId = treatmentId;
    }

    public Treatment(String treatmentId, String diagnosis, String treatmentDetails, int quantity, boolean paymentStatus) {
        this.treatmentId = treatmentId;
        this.diagnosis = diagnosis;
        this.treatmentDetails = treatmentDetails;
        this.quantity = quantity;
        this.paymentStatus = paymentStatus;
        this.treatmentDate = new Date();   // current date
    }
    
    

    public Treatment(String treatmentId, String patientIC, String patientName, String doctorName, String diagnosis, String treatmentDetails, int quantity, boolean paymentStatus) {
        this.treatmentId = treatmentId;
        this.patientIC = patientIC;
        this.patientName = patientName;
        this.doctorName = doctorName;
        this.diagnosis = diagnosis;
        this.treatmentDetails = treatmentDetails;
        this.quantity = quantity;
        this.paymentStatus = paymentStatus;
        this.treatmentDate = new Date();   // current date
    }

    public String getTreatmentId() {
        return treatmentId;
    }

    public void setTreatmentId(String treatmentId) {
        this.treatmentId = treatmentId;
    }

    public String getPatientIC() {
        return patientIC;
    }

    public void setPatientIC(String patientIC) {
        this.patientIC = patientIC;
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

    public boolean getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(boolean paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    public Date getTreatmentDate() {
        return treatmentDate;
    }

    public void setTreatmentDate(Date treatmentDate) {
        this.treatmentDate = treatmentDate;
    }

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy");
        String formattedDate = (treatmentDate != null) ? sdf.format(treatmentDate) : "N/A";
        return treatmentId + "#" + patientIC + "#" + patientName + "#" + doctorName + "#" + diagnosis + "#" + treatmentDetails + "#" + 
                quantity + "#" + (paymentStatus ? "Pay" : "Unpay") + "#" + formattedDate;
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
        throw new UnsupportedOperationException("Not supported yet."); 
    }
}
