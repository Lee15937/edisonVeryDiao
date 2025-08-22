package control;

import adt.ListInterface;
import adt.ArrayList;
import entity.Prescription;
import java.io.*;

public class PrescriptionCTRL {
    private ListInterface<Prescription> prescriptions;
    private static final String FILE_NAME = "src/DAO/prescription.txt";

    public PrescriptionCTRL() {
        prescriptions = new ArrayList<>();
        loadFromFile();
    }

    public Prescription findPrescription(String presID) {
        for (int i = 1; i <= prescriptions.getNumberOfEntries(); i++) {
            Prescription p = prescriptions.getEntry(i);
            if (p.getPresID().equals(presID)) {
                return p;
            }
        }
        return null;
    }

    public void markAsPaid(String presID) {
        Prescription p = findPrescription(presID);
        if (p != null) {
            p.setPaymentStatus("Paid");
            saveToFile();
        }
    }

    public void reportPrescriptions() {
        System.out.println("\n=== Prescription Report ===");
        for (int i = 1; i <= prescriptions.getNumberOfEntries(); i++) {
            Prescription p = prescriptions.getEntry(i);
            System.out.printf("ID: %s | Patient: %s | Doctor: %s | Medicine: %s | Qty: %d | Status: %s%n",
                              p.getPresID(), p.getPatientName(), p.getDoctorName(),
                              p.getMedicineID(), p.getQuantity(), p.getPaymentStatus());
        }
    }

    private void loadFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                Prescription p = Prescription.fromString(line);
                if (p != null) prescriptions.add(p);
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    public void saveToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (int i = 1; i <= prescriptions.getNumberOfEntries(); i++) {
                bw.write(prescriptions.getEntry(i).toString());
                bw.newLine();
            }
        } catch (IOException e) { e.printStackTrace(); }
    }
}
