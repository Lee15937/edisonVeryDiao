package boundary;

import adt.ArrayList;
import adt.DoubleLinkedList;
import entity.Patient;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import utility.Command;
import utility.MessageUI;

public class PatientUI {

    private Scanner scanner = new Scanner(System.in);
    private MessageUI messageUI = new MessageUI();
    private Command command = new Command();

    public Scanner getScanner() {
        return scanner;
    }

    public void displayPatientMenu() {
        System.out.println("===================================");
        System.out.println(" Patient Management System");
        System.out.println("===================================");
        System.out.println("1. Register Patient");
        System.out.println("2. Remove Patient");
        System.out.println("3. Update Patient Record");
        System.out.println("4. Search Patient");
        System.out.println("5. List Patients");
        System.out.println("6. Filter Patients");
        System.out.println("7. Generate Summary Report");
        System.out.println("8. Exit");
    }

    public int getMenuChoice() {
        System.out.print("Choose an option: ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // consume newline
        return choice;
    }

    public Patient gatherPatientDetails() {
        String name = getUserInput("Enter Patient Name (or 'X' to exit): ", "Error: Name cannot be blank.");
        if (name == null) {
            return null;
        }

        String ic = getUserInputWithRegex("Enter Patient IC (12 digits, or 'X' to exit): ",
                "Error: Invalid IC. Must be 12 digits.", "\\d{12}");
        if (ic == null) {
            return null;
        }

        String gender = getUserInputWithRegex("Enter Gender (M/F, or 'X' to exit): ",
                "Error: Gender must be 'M' or 'F'.", "[MFmf]");
        if (gender == null) {
            return null;
        }

        String ageStr = getUserInputWithRegex("Enter Age (number, or 'X' to exit): ",
                "Error: Invalid age.", "\\d{1,3}");
        if (ageStr == null) {
            return null;
        }
        int age = Integer.parseInt(ageStr);

        String phoneNum = getUserInputWithRegex("Enter Phone Number (10-11 digits, or 'X' to exit): ",
                "Error: Invalid Phone Number.", "\\d{10,11}");
        if (phoneNum == null) {
            return null;
        }

        return new Patient(ic, name, gender, age, phoneNum);
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
            command.pressEnterToContinue();
        }
    }

    public String getUserInputWithRegex(String prompt, String errorMessage, String regex) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("X")) {
                return null;
            }
            if (input.matches(regex)) {
                return input;
            }
            messageUI.displayInvalidMessage(errorMessage);
            command.pressEnterToContinue();
        }
    }

    public String getPatientId() {
        return getUserInput("Enter Patient ID (or 'X' to exit): ", "Error: Patient ID cannot be blank.");
    }

    public boolean getConfirmation(String message) {
        while (true) {
            System.out.print(message + " (Y/N): ");
            String input = scanner.nextLine().trim().toUpperCase();
            if (input.equals("Y")) {
                return true;
            } else if (input.equals("N")) {
                return false;
            } else {
                messageUI.displayInvalidMessage("Error: Please enter 'Y' or 'N'.");
                command.pressEnterToContinue();
            }
        }
    }

    // ========= DISPLAY METHODS =========
    public void displayPatientDetails(Patient patient) {
        if (patient != null) {
            System.out.println("\n\n==================================");
            System.out.println("Patient Details:");
            System.out.println("ID: " + patient.getPatientId());
            System.out.println("Name: " + patient.getName());
            System.out.println("IC: " + patient.getIC());
            System.out.println("Phone Number: " + patient.getPhoneNo());
            System.out.println("Gender: " + patient.getGender());
            System.out.println("Age: " + patient.getAge());
            System.out.println("Date Registered: " + patient.getDate());
            System.out.println("==================================");
        } else {
            messageUI.displayInvalidMessage("Patient not found.");
        }
    }

    // ascending & filter
    public void displayPatientReport(DoubleLinkedList<Patient> patients, String title) {
        if (patients.sizeOf() > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy");
            Date date = new Date();

            System.out.println("===============================================================================================================================================================================================");
            System.out.println(title);
            System.out.println("Total Records: " + patients.sizeOf());
            System.out.println("Generated On: " + sdf.format(date));
            System.out.println("===============================================================================================================================================================================================");

            System.out.printf("%-10s %-20s %-15s %-15s %-10s %-5s %-20s%n",
                    "ID", "Name", "IC", "Phone", "Gender", "Age", "Register Date");

            System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");

            for (int i = 0; i < patients.sizeOf(); i++) {
                Patient patient = patients.get(i);
                System.out.printf("%-10s %-20s %-15s %-15s %-10s %-5d %-20s%n",
                        patient.getPatientId(),
                        patient.getName(),
                        patient.getIC(),
                        patient.getPhoneNo(),
                        patient.getGender(),
                        patient.getAge(),
                        patient.getDate() != null ? sdf.format(patient.getDate()) : "N/A");
            }

            System.out.println("===============================================================================================================================================================================================");
        } else {
            messageUI.displayInvalidMessage("No patients available to display.");
        }
    }

    // descending
    public void displayPatientReport(ArrayList<Patient> patients, String title) {
        if (patients.sizeOf() > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy");
            Date date = new Date();

            System.out.println("===============================================================================================================================================================================================");
            System.out.println(title);
            System.out.println("Total Patients: " + patients.sizeOf());
            System.out.println("Date: " + sdf.format(date));
            System.out.println("===============================================================================================================================================================================================");

            System.out.printf("%-10s %-20s %-15s %-15s %-10s %-5s %-20s%n",
                    "ID", "Name", "IC", "Phone", "Gender", "Age", "Register Date");

            System.out.println("------------------------------------------------------------------------------------------------------------------");

            for (Patient patient : patients) {
                System.out.printf("%-10s %-20s %-15s %-15s %-10s %-5d %-20s%n",
                        patient.getPatientId(),
                        patient.getName(),
                        patient.getIC(),
                        patient.getPhoneNo(),
                        patient.getGender(),
                        patient.getAge(),
                        patient.getDate() != null ? sdf.format(patient.getDate()) : "N/A");
            }
            System.out.println("===============================================================================================================================================================================================");
        } else {
            messageUI.displayInvalidMessage("No patients available to display.");
        }
    }

    public String getUpdateChoice() {
        System.out.println("Which detail would you like to update?");
        System.out.println("1. Name");
        System.out.println("2. IC");
        System.out.println("3. Phone Number");
        System.out.println("4. Gender");
        System.out.println("5. Age");
        System.out.println("6. Exit");
        System.out.print("Enter your choice (1-6): ");
        return scanner.nextLine().trim();
    }

    public String getUpdatedValue(String field) {
        System.out.print("Enter new " + field + ": ");
        return scanner.nextLine().trim();
    }

    public String getUserSortingPreference() {
        Scanner scanner = new Scanner(System.in);
        String choice = "";

        while (!choice.equals("1") && !choice.equals("2") && !choice.equals("3")) {
            System.out.println("Please choose sorting order:");
            System.out.println("1. Ascending Order");
            System.out.println("2. Descending Order");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");

            choice = scanner.nextLine().trim();

            if (!choice.equals("1") && !choice.equals("2") && !choice.equals("3")) {
                messageUI.displayInvalidMessage("Invalid input. Please enter '1', '2', or '3'.");
                command.pressEnterToContinue();
            }
        }
        return choice;
    }

    public String getFilterChoice() {
        System.out.println("Select filter option:");
        System.out.println("1. Filter by Patient Gender");
        System.out.println("2. Filter by Recent 10 Patient Registered");
        System.out.println("3. Exit");
        System.out.print("Enter your choice (1-3): ");
        return scanner.nextLine().trim();
    }

}
