package entity;

public class Prescription {
    private String presID;
    private String doctorName;
    private String patientName;
    private String diagnostics;
    private String medicineID;
    private int quantity;
    private String paymentStatus; // NEW FIELD

    public Prescription(String presID, String doctorName, String patientName,
                        String diagnostics, String medicineID, int quantity,
                        String paymentStatus) {
        this.presID = presID;
        this.doctorName = doctorName;
        this.patientName = patientName;
        this.diagnostics = diagnostics;
        this.medicineID = medicineID;
        this.quantity = quantity;
        this.paymentStatus = paymentStatus;
    }

    // Getters & Setters
    public String getPresID() { return presID; }
    public String getDoctorName() { return doctorName; }
    public String getPatientName() { return patientName; }
    public String getDiagnostics() { return diagnostics; }
    public String getMedicineID() { return medicineID; }
    public int getQuantity() { return quantity; }
    public String getPaymentStatus() { return paymentStatus; }

    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    @Override
    public String toString() {
        return presID + "," + doctorName + "," + patientName + "," +
               diagnostics + "," + medicineID + "," + quantity + "," + paymentStatus;
    }

    public static Prescription fromString(String line) {
        String[] parts = line.split(",");
        if (parts.length == 7) {
            return new Prescription(parts[0], parts[1], parts[2], parts[3],
                                    parts[4], Integer.parseInt(parts[5]), parts[6]);
        }
        return null;
    }
}
