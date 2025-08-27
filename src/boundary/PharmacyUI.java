package boundary;

import control.PharmacyManagementCTRL;
import entity.Medicine;
import entity.Treatment;
import java.util.Scanner;

public class PharmacyUI {
    private PharmacyManagementCTRL pharmacyCTRL;
    private Scanner scanner;

    public PharmacyUI(PharmacyManagementCTRL pharmacyCTRL) {
        this.pharmacyCTRL = pharmacyCTRL;
        this.scanner = new Scanner(System.in);
    }

    public void displayMenu() {
        while (true) {
            System.out.println("\n=== PHARMACY MANAGEMENT ===");
            System.out.println("1. Dispense Medicine by Treatment");
            System.out.println("2. Manage Stock");
            System.out.println("3. Sales Report");
            System.out.println("4. Payment Report");
            System.out.println("5. Back to Main Menu");
            System.out.print("Enter your choice: ");

            int choice = getIntInput();
            switch (choice) {
                case 1:
                    dispenseMedicine();
                    break;
                case 2:
                    stockMenu();
                    break;
                case 3:
                    showSalesReport();
                    break;
                case 4:
                    pharmacyCTRL.countPayments();
                    break;
                case 5:
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private void dispenseMedicine() {
        System.out.print("Enter Treatment ID: ");
        String treatID = getStringInput();

        Treatment t = pharmacyCTRL.findTreatment(treatID);
        if (t == null) {
            System.out.println("Treatment not found!");
            return;
        }

        // ✅ Validation: Check if already paid
        if (t.getPaymentStatus()) {
            System.out.println("Treatment is complete. No further dispensing required.");
            return;
        }

        String medName = t.getTreatmentDetails();
        Medicine med = pharmacyCTRL.findMedicineByName(medName);

        if (med == null) {
            System.out.println("Medicine '" + medName + "' not found in stock.");
            return;
        }

        if (!pharmacyCTRL.isMedicineAvailable(medName, t.getQuantity())) {
            System.out.println("Not enough stock for " + med.getName() + ". Available: " + med.getStock());
            return;
        }

        // Deduct stock
        pharmacyCTRL.dispenseMedicine(medName, t.getQuantity());
        double totalPrice = med.getPrice() * t.getQuantity();

        System.out.println("\n=== DISPENSE SUMMARY ===");
        System.out.println("Treatment ID: " + t.getTreatmentId());
        System.out.println("Doctor: " + t.getDoctorName());
        System.out.println("Patient: " + t.getPatientName());
        System.out.println("Diagnosis: " + t.getDiagnosis());
        System.out.println("Medicine: " + med.getName());
        System.out.println("Quantity: " + t.getQuantity());
        System.out.println("Total Price: RM" + String.format("%.2f", totalPrice));

        System.out.print("Make payment now? (Y/N): ");
        String payChoice = scanner.nextLine().trim().toUpperCase();

        if (payChoice.equals("Y")) {
            t.setPaymentStatus(true);
            System.out.println("Payment successful. Treatment marked as PAID.");
        } else {
            System.out.println("Treatment remains UNPAID.");
        }

        pharmacyCTRL.saveTreatmentsToFile();
    }


    // =========================
    // NEW FUNCTION: Sales Report
    // =========================
    private void showSalesReport() {
        double totalSales = pharmacyCTRL.calculateTotalSales();
        System.out.println("\n=== SALES REPORT ===");
        System.out.println("Total Sales (Paid Treatments): RM" + String.format("%.2f", totalSales));
    }

    // =========================
    // Stock Management Methods
    // =========================
    private void stockMenu() {
        while (true) {
            System.out.println("\n=== MEDICINE STOCK MANAGEMENT ===");
            System.out.println("1. View All Medicines");
            System.out.println("2. Add New Medicine");
            System.out.println("3. Update Medicine");
            System.out.println("4. Delete Medicine");
            System.out.println("5. Restock Medicine");
            System.out.println("6. Back to Pharmacy Menu");
            System.out.print("Enter your choice: ");

            int choice = getIntInput();
            switch (choice) {
                case 1:
                    displayAllMedicines();
                    break;
                case 2:
                    addMedicine();
                    break;
                case 3:
                    updateMedicine();
                    break;
                case 4:
                    deleteMedicine();
                    break;
                case 5:
                    restockMedicine();
                    break;
                case 6:
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void displayAllMedicines() {
        System.out.println("\n=== ALL MEDICINES ===");
        System.out.printf("%-8s %-20s %-10s %-10s%n", "ID", "Name", "Price", "Stock");
        System.out.println("-------------------------------------------------------");

        for (int i = 1; i <= pharmacyCTRL.getAllMedicines().getNumberOfEntries(); i++) {
            Medicine med = pharmacyCTRL.getAllMedicines().getEntry(i);
            System.out.printf("%-8s %-20s RM%-9.2f %-10d%n",
                    med.getMedicineID(),
                    med.getName(),
                    med.getPrice(),
                    med.getStock());
        }
    }

    private void addMedicine() {
        System.out.println("\n=== ADD NEW MEDICINE ===");

        String name;
        while (true) {
            System.out.print("Enter Medicine Name (letters only): ");
            name = scanner.nextLine().trim();

            if (!name.matches("^[A-Za-z ]+$")) {
                System.out.println("Invalid name! Only letters and spaces are allowed.");
                continue;
            }
            break;
        }

        System.out.print("Enter Price: RM");
        double price = getDoubleInput();

        System.out.print("Enter Initial Stock: ");
        int stock = getIntInput();

        if (pharmacyCTRL.addMedicineAuto(name, price, stock)) {
            System.out.println("Medicine added successfully!");
        } else {
            System.out.println("Failed to add medicine.");
        }
    }

    private void updateMedicine() {
        System.out.println("\n=== UPDATE MEDICINE ===");
        System.out.print("Enter Medicine ID to update: ");
        String id = scanner.nextLine();

        Medicine existing = pharmacyCTRL.findMedicine(id);
        if (existing == null) {
            System.out.println("Medicine not found!");
            return;
        }

        System.out.print("Enter new Name (leave blank to keep current): ");
        String name = scanner.nextLine().trim();

        if (!name.isEmpty()) {
            if (!name.matches("^[A-Za-z ]+$")) {
                System.out.println("Invalid name! Only letters and spaces are allowed. Keeping old name.");
            } else {
                existing.setName(name);
            }
        }

        System.out.print("Enter new Price (enter 0 to keep current): RM");
        double price = getDoubleInput();

        if (price > 0) {
            existing.setPrice(price);
        }

        System.out.println("✅ Medicine updated successfully!");
        pharmacyCTRL.saveMedicinesToFile();
    }

    private void deleteMedicine() {
        System.out.println("\n=== DELETE MEDICINE ===");
        System.out.print("Enter Medicine ID to delete: ");
        String id = getStringInput();

        if (pharmacyCTRL.deleteMedicine(id)) {
            System.out.println("Medicine deleted successfully!");
        } else {
            System.out.println("Medicine not found or deletion failed!");
        }
    }

    private void restockMedicine() {
        System.out.println("\n=== RESTOCK MEDICINE ===");
        System.out.print("Enter Medicine ID: ");
        String id = getStringInput();

        Medicine medicine = pharmacyCTRL.findMedicine(id);
        if (medicine == null) {
            System.out.println("Medicine not found!");
            return;
        }

        System.out.print("Enter quantity to add: ");
        int quantity = getIntInput();

        medicine.addStock(quantity);
        System.out.println("Stock updated successfully! New stock: " + medicine.getStock());
        pharmacyCTRL.saveMedicinesToFile();
    }

    // =========================
    // Validation Helper Methods
    // =========================
    private int getIntInput() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. \nPlease enter a number: ");
            }
        }
    }

    private double getDoubleInput() {
        while (true) {
            try {
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. \nPlease enter a valid decimal number: ");
            }
        }
    }

    private String getStringInput() {
        String input;
        while (true) {
            input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.print("Input cannot be empty. Please enter again: ");
        }
    }
}
