package control;

import entity.Medicine;
import adt.ArrayList;
import adt.ListInterface;
import java.io.*;

public class StockCTRL {
    private ListInterface<Medicine> medicines;
    private static final String FILE_NAME = "src/DAO/medicine.txt"; 
    private int idCounter = 0; // track last used medicine number

    public StockCTRL() {
        this.medicines = new ArrayList<>();  
        loadFromFile();
        updateIdCounter(); // ensure counter starts from last medicine
    }

    public ListInterface<Medicine> getAllMedicines() {
        return medicines;
    }

    // ✅ Auto-generate ID method
    private String generateMedicineID() {
        idCounter++;
        return String.format("M%03d", idCounter);
    }

    // ✅ Add medicine with auto ID
    public boolean addMedicineAuto(String name, double price, int stock) {
        String id = generateMedicineID();
        Medicine newMedicine = new Medicine(id, name, price, stock);
        medicines.add(newMedicine);
        saveToFile();
        return true;
    }

    // ✅ If you still want manual add (with custom ID)
    public boolean addMedicine(Medicine medicine) {
        if (findMedicine(medicine.getMedicineID()) != null) {
            return false; 
        }
        medicines.add(medicine);
        saveToFile();
        return true;
    }

    public boolean deleteMedicine(String medicineID) {
        for (int i = 1; i <= medicines.getNumberOfEntries(); i++) {
            Medicine med = medicines.getEntry(i);
            if (med.getMedicineID().equals(medicineID)) {
                medicines.remove(i);
                saveToFile();
                return true;
            }
        }
        return false;
    }

    public boolean isMedicineAvailable(String medicineID, int quantity) {
        Medicine med = findMedicine(medicineID);
        return med != null && med.getStock() >= quantity;
    }

    public void dispenseMedicine(String medicineID, int quantity) {
        Medicine med = findMedicine(medicineID);
        if (med == null) throw new RuntimeException("Medicine not found!");
        med.reduceStock(quantity);
        saveToFile();
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

    // ✅ Load medicines from file
    private void loadFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            System.out.println("⚠ medicine.txt not found, starting with empty list.");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                Medicine med = Medicine.fromString(line);
                if (med != null) {
                    medicines.add(med);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ✅ Save medicines to file
    private void saveToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (int i = 1; i <= medicines.getNumberOfEntries(); i++) {
                bw.write(medicines.getEntry(i).toString());
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ✅ Update idCounter based on last medicine ID in the file
    private void updateIdCounter() {
        if (medicines.getNumberOfEntries() > 0) {
            Medicine lastMed = medicines.getEntry(medicines.getNumberOfEntries());
            String lastID = lastMed.getMedicineID(); // e.g. "M007"
            try {
                idCounter = Integer.parseInt(lastID.substring(1)); // "007" → 7
            } catch (NumberFormatException e) {
                idCounter = 0;
            }
        }
    }
}
