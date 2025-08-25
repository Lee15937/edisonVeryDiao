/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package boundary;

import adt.ArrayList;
import adt.DoubleLinkedList;
import entity.Treatment;
import utility.MessageUI;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

/**
 *
 * @author kosoo
 */
public class MedicalTreatmentUI {

    private Scanner scanner = new Scanner(System.in);
    private MessageUI messageUI = new MessageUI();

    public Scanner getScanner() {
        return scanner;
    }

    public void displayTreatmentMenu() {
        System.out.println("============================================");
        System.out.println("  Medical Treatment Management System");
        System.out.println("============================================");
        System.out.println("1. Add New Diagnosis Record");
        System.out.println("2. Update Treatment Details");
        System.out.println("3. Delete Traetment Record");
        System.out.println("4. Search Treatment History");
        System.out.println("5. List All Patient Treatment History");
        System.out.println("6. Filter Treatment");
        System.out.println("7. Generate Reports");
        System.out.println("8. Exit");
    }

    public int getMenuChoice() {
        System.out.print("Choose an option: ");
        while (!scanner.hasNextInt()) {
            messageUI.displayInvalidMessage("Invalid input. Please enter a number.");
            scanner.nextLine();
            System.out.print("Choose an option: ");
        }
        int choice = scanner.nextInt();
        scanner.nextLine();
        return choice;
    }

    public Treatment gatherTreatmentDetails() {
        String diagnosis = getUserInput("Enter Diagnosis Description (or 'X' to exit): ", "Error: Diagnosis cannot be blank.");
        if (diagnosis == null) {
            return null;
        }

        String treatment = getUserInput("Enter Treatment Details (or 'X' to exit): ", "Error: Treatment details cannot be blank.");
        if (treatment == null) {
            return null;
        }

        String qtyStr = getUserInputWithRegex("Enter Quantity (number, or 'X' to exit): ",
                "Error: Invalid Quantity.", "\\d{1,3}");
        if (qtyStr == null) {
            return null;
        }
        int qty = Integer.parseInt(qtyStr);

        return new Treatment(null, diagnosis, treatment, qty, false);
    }

    public String getUserInput(String prompt, String errorMessage) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("X")) {
                return null;
            }
            if (!input.isBlank()) {
                return input;
            }
            messageUI.displayInvalidMessage(errorMessage);
        }
    }

    public String getUserInputWithRegex(String prompt, String errorMessage, String regex) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim().toUpperCase();
            if (input.equalsIgnoreCase("X")) {
                return null;
            }
            if (input.matches(regex)) {
                return input;
            }
            messageUI.displayInvalidMessage(errorMessage);
        }
    }

    public String getUpdateChoice() {
        System.out.println("Which detail would you like to update?");
        System.out.println("1. Diagnosis Description");
        System.out.println("2. Treatment Details");
        System.out.println("3. Quantity");
        System.out.println("4. Follow-Up Date");
        System.out.println("5. Exit");
        System.out.print("Enter your choice (1-5): ");
        return scanner.nextLine().trim();
    }

    public String getUpdatedValue(String field) {
        System.out.print("Enter new " + field + ": ");
        return scanner.nextLine().trim();
    }

    public boolean getConfirmation(String message) {
        while (true) {
            System.out.print(message);
            String input = scanner.nextLine().trim().toUpperCase();
            if (input.equals("Y")) {
                return true;
            } else if (input.equals("N")) {
                return false;
            } else {
                messageUI.displayInvalidMessage("Error: Please enter 'Y' for Yes or 'N' for No.");
            }
        }
    }

    public String getTreatmentId() {
        return getUserInput("Enter Treatment ID (or 'X' to exit): ", "Error: Treatment ID cannot be blank.");
    }

    // ===== Display Details =====
    public void displayTreatmentDetails(Treatment treatment) {
        if (treatment != null) {
            System.out.println("\n=====================================================================");
            System.out.println("Treatment Details:");
            System.out.println("ID: " + treatment.getTreatmentId());
            System.out.println("Patient Name: " + treatment.getPatientName());
            System.out.println("Doctor Name: " + treatment.getDoctorName());
            System.out.println("Diagnosis: " + treatment.getDiagnosis());
            System.out.println("Treatment: " + treatment.getTreatmentDetails());
            System.out.println("Quantity: " + treatment.getQuantity());
            System.out.println("PaymentStatus: " + treatment.getPaymentStatus());
            System.out.println("Date: " + treatment.getTreatmentDate());
            System.out.println("=====================================================================");
        } else {
            messageUI.displayInvalidMessage("Treatment record not found.");
        }
    }

    // ascending & filter
    public void displayTreatmentReport(DoubleLinkedList<Treatment> treatments, String title) {
        if (treatments.sizeOf() > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date = new Date();

            System.out.println("===============================================================================================================================================================================================");
            System.out.println(title);
            System.out.println("Total Records: " + treatments.sizeOf());
            System.out.println("Generated On: " + sdf.format(date));
            System.out.println("===============================================================================================================================================================================================");

            System.out.printf("%-12s %-20s %-20s %-20s %-20s %-5s %-25s %-20s%n",
                    "TreatmentID", "PatientName", "DoctorName", "Diagnosis", "Treatment", "Quantity", "PaymentStatus", "Date");

            System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");

            for (int i = 0; i < treatments.sizeOf(); i++) {
                Treatment t = treatments.get(i);
                System.out.printf("%-12s %-20s %-20s %-20s %-20s %-5s %-25s %-20s%n",
                        t.getTreatmentId(),
                        t.getPatientName(),
                        t.getDoctorName(),
                        t.getDiagnosis(),
                        t.getTreatmentDetails(),
                        t.getQuantity(),
                        t.getPaymentStatus() ? "Pay" : "Unpay",
                        sdf.format(t.getTreatmentDate()));
            }

            System.out.println("===============================================================================================================================================================================================");
        } else {
            messageUI.displayInvalidMessage("No treatments available to display.");
        }
    }

    // descending
    public void displayTreatmentReport(ArrayList<Treatment> treatments, String title) {
        if (treatments.sizeOf() > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date = new Date();

            System.out.println("===============================================================================================================================================================================================");
            System.out.println(title);
            System.out.println("Total Records: " + treatments.sizeOf());
            System.out.println("Generated On: " + sdf.format(date));
            System.out.println("===============================================================================================================================================================================================");

            System.out.printf("%-12s %-20s %-20s %-20s %-20s %-5s %-25s %-20s%n",
                    "TreatmentID", "PatientName", "DoctorName", "Diagnosis", "Treatment", "Quantity", "PaymentStatus", "Date");

            System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");

            for (int i = 0; i < treatments.sizeOf(); i++) {
                Treatment t = treatments.get(i);
                System.out.printf("%-12s %-20s %-20s %-20s %-5s %-25s %-20s%n",
                        t.getTreatmentId(),
                        t.getPatientName(),
                        t.getDoctorName(),
                        t.getDiagnosis(),
                        t.getTreatmentDetails(),
                        t.getQuantity(),
                        t.getPaymentStatus() ? "Pay" : "Unpay",
                        sdf.format(t.getTreatmentDate()));
            }
            System.out.println("===============================================================================================================================================================================================");
        } else {
            messageUI.displayInvalidMessage("No treatments available to display.");
        }
    }

    // ===== Extra (Filter/Sort) =====
    public String getFilterChoice() {
        System.out.println("Select filter option:");
        System.out.println("1. Filter by Doctor Name");
        System.out.println("2. Filter by Patient Name");
        System.out.println("3. Last 10 Treatments");
        System.out.println("4. Exit");
        System.out.print("Enter your choice (1-4): ");
        return scanner.nextLine().trim();
    }

    public String getSortChoice() {
        System.out.println("Select sort option:");
        System.out.println("1. Ascending by Date");
        System.out.println("2. Descending by Date");
        System.out.print("Enter your choice (1-2): ");
        return scanner.nextLine().trim();
    }

    // ===== Messages =====
    public void displaySuccess(String message) {
        System.out.println("[SUCCESS] " + message);
    }

    public void displayError(String message) {
        System.out.println("[ERROR] " + message);
    }

}
