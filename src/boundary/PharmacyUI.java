package boundary;

import control.StockCTRL;
import control.PrescriptionCTRL;
import entity.Prescription;
import entity.Medicine;
import java.util.Scanner;

public class PharmacyUI {
    private StockCTRL stockCTRL;
    private PrescriptionCTRL presCTRL;
    private Scanner scanner;

    public PharmacyUI(StockCTRL stockCTRL, PrescriptionCTRL presCTRL) {
        this.stockCTRL = stockCTRL;
        this.presCTRL = presCTRL;
        this.scanner = new Scanner(System.in);
    }

    public void displayMenu() {
        while (true) {
            System.out.println("\n=== PHARMACY MANAGEMENT ===");
            System.out.println("1. Dispense Medicine by Prescription");
            System.out.println("2. Prescription Report");
            System.out.println("3. Manage Stock");
            System.out.println("4. Back to Main Menu");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    dispenseMedicine();
                    break;
                case 2:
                    presCTRL.reportPrescriptions();
                    break;
                case 3:
                    stockMenu();
                    break;
                case 4:
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private void dispenseMedicine() {
        System.out.print("Enter Prescription ID: ");
        String presID = scanner.nextLine();

        Prescription p = presCTRL.findPrescription(presID);
        if (p == null) {
            System.out.println("Prescription not found!");
            return;
        }

        Medicine med = stockCTRL.findMedicine(p.getMedicineID());
        if (med == null) {
            System.out.println("Medicine " + p.getMedicineID() + " not found in stock.");
            return;
        }

        if (!stockCTRL.isMedicineAvailable(p.getMedicineID(), p.getQuantity())) {
            System.out.println("Not enough stock for " + med.getName() + ". Available: " + med.getStock());
            return;
        }

        // Deduct stock
        stockCTRL.dispenseMedicine(p.getMedicineID(), p.getQuantity());
        double totalPrice = med.getPrice() * p.getQuantity();

        // Print details
        System.out.println("Dispensed " + p.getQuantity() + " of " + med.getName());
        System.out.println("Doctor: " + p.getDoctorName());
        System.out.println("Patient: " + p.getPatientName());
        System.out.println("Diagnostics: " + p.getDiagnostics());
        System.out.println("Total Price: RM" + totalPrice);

        // Ask for payment
        System.out.print("Make payment now? (Y/N): ");
        String payChoice = scanner.nextLine().trim().toUpperCase();

        if (payChoice.equals("Y")) {
            p.setPaymentStatus("Paid");
            System.out.println("Payment successful. Prescription marked as PAID.");
        } else {
            System.out.println("Prescription remains UNPAID.");
        }

        // Save updated prescriptions
        presCTRL.saveToFile();
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
            
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline
            
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

        for (int i = 1; i <= stockCTRL.getAllMedicines().getNumberOfEntries(); i++) {
            Medicine med = stockCTRL.getAllMedicines().getEntry(i);
            System.out.printf("%-8s %-20s RM%-9.2f %-10d%n", 
                med.getMedicineID(), 
                med.getName(), 
                med.getPrice(), 
                med.getStock());
        }
    }

    private void addMedicine() {
        System.out.println("\n=== ADD NEW MEDICINE ===");
        System.out.print("Enter Medicine Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter Price: RM");
        double price = scanner.nextDouble();
        System.out.print("Enter Initial Stock: ");
        int stock = scanner.nextInt();
        scanner.nextLine(); 

        if (stockCTRL.addMedicineAuto(name, price, stock)) {
            System.out.println("Medicine added successfully!");
        } else {
            System.out.println("Failed to add medicine.");
        }
    }

    private void updateMedicine() {
        System.out.println("\n=== UPDATE MEDICINE ===");
        System.out.print("Enter Medicine ID to update: ");
        String id = scanner.nextLine();
        
        Medicine existing = stockCTRL.findMedicine(id);
        if (existing == null) {
            System.out.println("Medicine not found!");
            return;
        }
        
        System.out.print("Enter new Name (leave blank to keep current): ");
        String name = scanner.nextLine();
        System.out.print("Enter new Price (enter 0 to keep current): RM");
        double price = scanner.nextDouble();
        scanner.nextLine();
        
        if (!name.isEmpty()) {
            existing.setName(name);
        }
        if (price > 0) {
            existing.setPrice(price);
        }
        
        System.out.println("Medicine updated successfully!");
    }

    private void deleteMedicine() {
        System.out.println("\n=== DELETE MEDICINE ===");
        System.out.print("Enter Medicine ID to delete: ");
        String id = scanner.nextLine();
        
        if (stockCTRL.deleteMedicine(id)) {
            System.out.println("Medicine deleted successfully!");
        } else {
            System.out.println("Medicine not found or deletion failed!");
        }
    }

    private void restockMedicine() {
        System.out.println("\n=== RESTOCK MEDICINE ===");
        System.out.print("Enter Medicine ID: ");
        String id = scanner.nextLine();
        
        Medicine medicine = stockCTRL.findMedicine(id);
        if (medicine == null) {
            System.out.println("Medicine not found!");
            return;
        }
        
        System.out.print("Enter quantity to add: ");
        int quantity = scanner.nextInt();
        scanner.nextLine();
        
        medicine.addStock(quantity);
        System.out.println("Stock updated successfully! New stock: " + medicine.getStock());
    }
}
