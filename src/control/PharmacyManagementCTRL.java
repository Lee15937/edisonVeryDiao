package control;

import DAO.Dao;
import adt.ArrayList;
import adt.ListInterface;
import entity.Medicine;
import entity.Treatment;

import java.io.*;
import java.util.function.Function;

public class PharmacyManagementCTRL {

    private ListInterface<Medicine> medicines;
    private ListInterface<Treatment> treatments;
    private Dao<Medicine> dao = new Dao<>();

    private static final String MEDICINE_FILE = "src/DAO/medicine.txt";
    private static final String TREATMENT_FILE = "src/DAO/treatment.txt";

    private int idCounter = 0; // for medicine IDs

    public PharmacyManagementCTRL() {
        medicines = new ArrayList<>();
        treatments = new ArrayList<>();
        loadMedicinesFromFile();
        loadTreatmentsFromFile();
        updateIdCounter();
    }

    // ===============================
    // Medicine Management
    // ===============================
    public ListInterface<Medicine> getAllMedicines() {
        return medicines;
    }

    private String generateMedicineID() {
        idCounter++;
        return String.format("M%03d", idCounter);
    }

    public boolean addMedicineAuto(String name, double price, int stock) {
        String id = generateMedicineID();
        Medicine newMedicine = new Medicine(id, name, price, stock);
        medicines.add(newMedicine);
        saveMedicinesToFile();
        return true;
    }

    public boolean addMedicine(Medicine medicine) {
        if (findMedicine(medicine.getMedicineID()) != null) return false;
        medicines.add(medicine);
        saveMedicinesToFile();
        return true;
    }

    public boolean deleteMedicine(String medicineID) {
        for (int i = 1; i <= medicines.getNumberOfEntries(); i++) {
            Medicine med = medicines.getEntry(i);
            if (med.getMedicineID().equals(medicineID)) {
                medicines.remove(i);
                saveMedicinesToFile();
                return true;
            }
        }
        return false;
    }

    public Medicine findMedicineByName(String medicineName) {
        for (int i = 1; i <= medicines.getNumberOfEntries(); i++) {
            Medicine med = medicines.getEntry(i);
            if (med.getName().equalsIgnoreCase(medicineName)) {
                return med;
            }
        }
        return null;
    }

    public Medicine findMedicine(String medicineID) {
        for (int i = 1; i <= medicines.getNumberOfEntries(); i++) {
            Medicine med = medicines.getEntry(i);
            if (med.getMedicineID().equals(medicineID)) {
                return med;
            }
        }
        return null;
    }

    public boolean isMedicineAvailable(String medicineName, int quantity) {
        Medicine med = findMedicineByName(medicineName);
        return med != null && med.getStock() >= quantity;
    }

    public void dispenseMedicine(String medicineName, int quantity) {
        Medicine med = findMedicineByName(medicineName);
        if (med == null) throw new RuntimeException("Medicine not found!");
        med.reduceStock(quantity);
        saveMedicinesToFile();
    }

    private void loadMedicinesFromFile() {
        Function<String[], Medicine> mapper = parts -> {
            try {
                return Medicine.fromString(String.join("#", parts));
            } catch (Exception e) {
                System.out.println("Error parsing medicine record: " + String.join("#", parts));
                return null;
            }
        };
        ArrayList<Medicine> loaded = dao.readTextFileAsArrayList(MEDICINE_FILE, 4, mapper);

        for (int i = 0; i < loaded.sizeOf(); i++) {
            if (loaded.get(i) != null) medicines.add(loaded.get(i));
        }
    }
    
    public ArrayList<Medicine> readMedicineFromFileAsArrayList() {
        Function<String[], Medicine> medicineMapper = parts -> {
            try {
                return Medicine.fromString(String.join("#", parts));
            } catch (Exception e) {
                System.out.println("Error parsing medicine record: " + String.join("#", parts));
                return null;
            }
        };
        return dao.readTextFileAsArrayList(MEDICINE_FILE, 4, medicineMapper);
    }

    public void saveMedicinesToFile() {
        dao.saveToFile(medicines, MEDICINE_FILE);
    }

    private void updateIdCounter() {
        if (medicines.getNumberOfEntries() > 0) {
            Medicine lastMed = medicines.getEntry(medicines.getNumberOfEntries());
            try {
                idCounter = Integer.parseInt(lastMed.getMedicineID().substring(1));
            } catch (NumberFormatException e) {
                idCounter = 0;
            }
        }
    }

    // ===============================
    // Treatment Management
    // ===============================
    public Treatment findTreatment(String treatmentId) {
        for (int i = 1; i <= treatments.getNumberOfEntries(); i++) {
            Treatment t = treatments.getEntry(i);
            if (t.getTreatmentId().equals(treatmentId)) {
                return t;
            }
        }
        return null;
    }

    public void markAsPaid(String treatmentId) {
        Treatment t = findTreatment(treatmentId);
        if (t != null) {
            t.setPaymentStatus(true);
            saveTreatmentsToFile();
        }
    }

    public double calculateTotalSales() {
        double total = 0;
        for (int i = 1; i <= treatments.getNumberOfEntries(); i++) {
            Treatment t = treatments.getEntry(i);
            Medicine med = findMedicineByName(t.getTreatmentDetails());
            if (med != null) total += med.getPrice() * t.getQuantity();
        }
        return total;
    }

    public void countPayments() {
        int paid = 0, unpaid = 0;
        for (int i = 1; i <= treatments.getNumberOfEntries(); i++) {
            Treatment t = treatments.getEntry(i);
            if (t.getPaymentStatus()) paid++;
            else unpaid++;
        }
        System.out.println("\n=== PAYMENT REPORT ===");
        System.out.println("Paid Patients: " + paid);
        System.out.println("Unpaid Patients: " + unpaid);
    }

    private void loadTreatmentsFromFile() {
        File file = new File(TREATMENT_FILE);
        if (!file.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                Treatment t = fromString(line);
                if (t != null) treatments.add(t);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveTreatmentsToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(TREATMENT_FILE))) {
            for (int i = 1; i <= treatments.getNumberOfEntries(); i++) {
                bw.write(treatments.getEntry(i).toString());
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Treatment fromString(String line) {
        try {
            String[] parts = line.split("#");
            if (parts.length < 7) return null;

            Treatment t = new Treatment();
            t.setTreatmentId(parts[0]);
            t.setPatientName(parts[1]);
            t.setDoctorName(parts[2]);
            t.setDiagnosis(parts[3]);
            t.setTreatmentPlan(parts[4]);
            t.setQuantity(Integer.parseInt(parts[5]));
            t.setPaymentStatus(parts[6].equalsIgnoreCase("Pay"));
            return t;
        } catch (Exception e) {
            return null;
        }
    }
}
