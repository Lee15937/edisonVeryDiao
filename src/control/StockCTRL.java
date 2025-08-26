package control;

import DAO.Dao;
import entity.Medicine;
import adt.ArrayList;
import adt.ListInterface;
import java.util.function.Function;

public class StockCTRL {

    private ListInterface<Medicine> medicines;
    private Dao<Medicine> dao = new Dao<>();
    private static final String MEDICINE_FILE = "src/DAO/medicine.txt";
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

    // ✅ Add medicine manually (with custom ID)
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
        if (med == null) {
            throw new RuntimeException("Medicine not found!");
        }
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

    // ✅ Load medicines from file using Dao + fromString()
    public void loadFromFile() {
        Function<String[], Medicine> medicineMapper = parts -> {
            try {
                return Medicine.fromString(String.join("#", parts));
            } catch (Exception e) {
                System.out.println("Error parsing medicine record: " + String.join("#", parts));
                return null;
            }
        };
        ArrayList<Medicine> loadedMedicines = dao.readTextFileAsArrayList(MEDICINE_FILE, 4, medicineMapper);

        for (int i = 0; i < loadedMedicines.sizeOf(); i++) {
            Medicine med = loadedMedicines.get(i);
            if (med != null) {
                medicines.add(med);
            }
        }

        System.out.println("Loaded " + medicines.getNumberOfEntries() + " medicines.");
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

    // ✅ Save medicines to file
    public void saveToFile() {
        dao.saveToFile(medicines, MEDICINE_FILE);
        System.out.println("Medicine data saved.");
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
