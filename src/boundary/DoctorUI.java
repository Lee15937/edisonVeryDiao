/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package boundary;

import adt.ArrayList;
import adt.DoubleLinkedList;
import entity.Doctor;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import utility.Command;
import utility.MessageUI;

public class DoctorUI {

    private Scanner scanner = new Scanner(System.in);
    private MessageUI messageUI = new MessageUI();
    private Command command = new Command();

    public Scanner getScanner() {
        return scanner;
    }

    public void displayDoctorMenu() {
        System.out.println("===================================");
        System.out.println(" Doctor Management System");
        System.out.println("===================================");
        System.out.println("1. Register Doctor");
        System.out.println("2. Remove Doctor");
        System.out.println("3. Update Doctor Record");
        System.out.println("4. Search Doctor");
        System.out.println("5. List Doctors");
        System.out.println("6. Filter Doctors");
        System.out.println("7. Generate Summary Report");
        System.out.println("8. Exit");
    }

    public int getMenuChoice() {
        System.out.print("Choose an option: ");
        int choice = scanner.nextInt();
        scanner.nextLine();
        return choice;
    }

    public Doctor gatherDoctorDetails() {
        String name = getUserInput("Enter Doctor Name (or 'X' to exit): ",
                "Error: Name cannot be blank.");
        if (name == null) {
            return null;
        }

        String gender = getUserInputWithRegex("Enter Gender (M/F, or 'X' to exit): ",
                "Error: Gender must be 'M' or 'F'.", "[MFmf]");
        if (gender == null) {
            return null;
        }

        String phoneNo = getUserInputWithRegex(
                "Enter Phone Number (10-11 digits, or 'X' to exit): ",
                "Error: Invalid Phone Number.",
                "\\d{10,11}");
        if (phoneNo == null) {
            return null;
        }

        String email = getUserInputWithRegex(
                "Enter Email (or 'X' to exit): ",
                "Error: Invalid Email format.",
                "^[\\w._%+-]+@[\\w.-]+\\.[A-Za-z]{2,6}$");
        if (email == null) {
            return null;
        }

        // Duty Schedule (e.g., "Mon-Fri 9AM-5PM")
        String dutySchedule = getUserInputWithRegex(
                "Enter Duty Schedule (Format: Mon-Fri 09:00-18:00, or 'X' to exit): ",
                "Error: Duty Schedule must be in format (Day-Day HH:MM-HH:MM). Example: Mon-Fri 09:00-17:00",
                "^[A-Za-z]{3}-[A-Za-z]{3}\\s\\d{1,2}(:\\d{2})?-[0-2]?\\d(:\\d{2})?$"
        );
        if (dutySchedule == null) {
            return null;
        }

        // Availability (Y/N)
        String availabilityStr = getUserInputWithRegex(
                "Is the doctor available? (Y/N, or 'X' to exit): ",
                "Error: Must be Y or N.",
                "[YyNn]");
        if (availabilityStr == null) {
            return null;
        }
        boolean availability = availabilityStr.equalsIgnoreCase("Y");

        return new Doctor(name, gender, phoneNo, email, dutySchedule, availability);
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

    public String getDoctorId() {
        return getUserInput("Enter Doctor ID (or 'X' to exit): ", "Error: Doctor ID cannot be blank.");
    }

    public boolean getConfirmation(String message) {
        while (true) {
            System.out.print(message + " (Y/N): ");
            String input = scanner.nextLine().trim().toUpperCase();
            if (input.equals("Y")) {
                return true;
            }
            if (input.equals("N")) {
                return false;
            }
            messageUI.displayInvalidMessage("Error: Please enter 'Y' or 'N'.");
            command.pressEnterToContinue();
        }
    }

    public void displayDoctorDetails(Doctor doctor) {
        if (doctor != null) {
            System.out.println("\n\n==================================");
            System.out.println("Doctor Details:");
            System.out.println("ID: " + doctor.getDoctorId());
            System.out.println("Name: " + doctor.getName());
            System.out.println("Phone Number: " + doctor.getPhoneNo());
            System.out.println("Email: " + doctor.getEmail());
            System.out.println("Duty Schedule: " + doctor.getDutySchedule());
            System.out.println("Availability: " + (doctor.isAvailability() ? "Available" : "Not Available"));
            System.out.println("==================================");
        } else {
            messageUI.displayInvalidMessage("Doctor not found.");
        }
    }

    public void displayDoctorReport(DoubleLinkedList<Doctor> doctors, String title) {
        if (doctors.sizeOf() > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy");
            Date date = new Date();

            System.out.println("====================================================================================================================");
            System.out.println(title);
            System.out.println("Total Records: " + doctors.sizeOf());
            System.out.println("Generated On: " + sdf.format(date));
            System.out.println("====================================================================================================================");

            System.out.printf("%-10s %-20s %-15s %-30s %-20s %-15s%n",
                    "ID", "Name", "Phone", "Email", "Duty Schedule", "Availability");

            System.out.println("--------------------------------------------------------------------------------------------------------------------");

            for (int i = 0; i < doctors.sizeOf(); i++) {
                Doctor doctor = doctors.get(i);
                System.out.printf("%-10s %-20s %-15s %-30s %-20s %-15s%n",
                        doctor.getDoctorId(),
                        doctor.getName(),
                        doctor.getPhoneNo(),
                        doctor.getEmail(),
                        doctor.getDutySchedule(),
                        doctor.isAvailability() ? "Available" : "Not Available");
            }

            System.out.println("====================================================================================================================");
        } else {
            messageUI.displayInvalidMessage("No doctors available to display.");
        }
    }

    public void displayDoctorReport(ArrayList<Doctor> doctors, String title) {
        if (doctors.sizeOf() > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy");
            Date date = new Date();

            System.out.println("====================================================================================================================");
            System.out.println(title);
            System.out.println("Total Doctors: " + doctors.sizeOf());
            System.out.println("Date: " + sdf.format(date));
            System.out.println("====================================================================================================================");

            System.out.printf("%-10s %-20s %-20s %-15s %-10s %-5s %-20s%n",
                    "ID", "Name", "Specialty", "Phone", "Gender", "Age", "Date Joined");

            System.out.println("--------------------------------------------------------------------------------------------------------------------");

            for (Doctor doctor : doctors) {
                System.out.printf("%-10s %-20s %-15s %-30s %-20s %-15s%n",
                        doctor.getDoctorId(),
                        doctor.getName(),
                        doctor.getPhoneNo(),
                        doctor.getEmail(),
                        doctor.getDutySchedule(),
                        doctor.isAvailability() ? "Available" : "Not Available");
            }
            System.out.println("====================================================================================================================");
        } else {
            messageUI.displayInvalidMessage("No doctors available to display.");
        }
    }

    public String getUpdateChoice() {
        System.out.println("Which detail would you like to update?");
        System.out.println("1. Name");
        System.out.println("2. Phone Number");
        System.out.println("3. Email");
        System.out.println("4. Duty Schedule");
        System.out.println("5. Availability");
        System.out.println("6. Exit");
        System.out.print("Enter your choice (1-6): ");
        return scanner.nextLine().trim();
    }

    public String getUpdatedValue(String field) {
        System.out.print("Enter new " + field + ": ");
        return scanner.nextLine().trim();
    }

    public String getUserSortingPreference() {
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
        System.out.println("1. Filter by Doctor Gender");
        System.out.println("2. Filter by Specialty");
        System.out.println("3. Exit");
        System.out.print("Enter your choice (1-3): ");
        return scanner.nextLine().trim();
    }
}
