/**
 *
 * @author Choi Kah Wai
 */

package boundary;

import adt.QueueInterface;
import adt.ListInterface;
import entity.Patient;
import java.util.Scanner;
import java.util.Queue;

public class PatientRegistrationForm {

    Scanner scanner = new Scanner(System.in);
    private static int patientIDCounter = 1;

    public int getPatientChoice() {
        System.out.println("1. Register patient");
        System.out.println("2. View waiting queue");
        System.out.println("3. Serve Next Patient");
        System.out.println("4. View patient list");
        System.out.println("5. Modify patient list");
        System.out.println("6. Patients Reports");
        System.out.println("7. Exit");
        System.out.print("Enter choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine();
        System.out.println();
        return choice;
    }

    public void listPatientQueue(QueueInterface<Patient> patientQueue) {
        System.out.println("\n==============================");
        System.out.println("        Patient Queue       ");
        System.out.println("==============================");

        if (patientQueue.isEmpty()) {
            System.out.println("No patients registered yet.");
        } else {

            String header = String.format("%-5s %-15s %-15s %-10s %-10s %-15s\n",
                    "ID", "IC", "Name", "Age", "Gender", "Phone No");
            System.out.println(header);
            System.out.println("-----------------------------------------------------------------------");

            for (Patient patient : patientQueue.toList()) {
                String row = String.format("%-5s %-15s %-15s %-10d %-10s %-15s",
                        patient.getPatientId(),
                        patient.getIC(),
                        patient.getName(),
                        patient.getAge(),
                        patient.getGender(),
                        patient.getPhoneNo());
                System.out.println(row);
            }
        }
    }

    public void printPatientDetails(Patient patient) {
        System.out.println("Patient Details");
        System.out.println("Patient ID: " + patient.getPatientId());
        System.out.println("Patient IC: " + patient.getIC());
        System.out.println("Patient name: " + patient.getName());
        System.out.println("Patient age: " + patient.getAge());
        System.out.println("Patient gender: " + patient.getGender());
        System.out.println("Patient phone number: " + patient.getPhoneNo());
        System.out.println("Registration date: " + patient.getDate());
    }

    public String inputPatientName() {
        System.out.print("Enter patient name: ");
        String name = scanner.nextLine();
        return name;
    }

    public int inputPatientAge() {
        int age;
        while (true) {
            System.out.print("Enter patient age: ");
            if (scanner.hasNextInt()) {
                age = scanner.nextInt();
                scanner.nextLine();
                if (age > 0) {
                    return age;
                } else {
                    System.out.println("Age must be positive.");
                }
            } else {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine();
            }
        }
    }

    public String inputPatientGender() {
        while (true) {
            System.out.print("Enter patient gender (M/F): ");
            String gender = scanner.nextLine().trim().toUpperCase();

            if (gender.equals("M") || gender.equals("F")) {
                return gender;
            } else {
                System.out.println("Gender must be M or F.");
            }
        }
    }

    public String inputPatientIC() {
        while (true) {
            System.out.print("Enter patient IC number (12 digits): ");
            String icNumber = scanner.nextLine().trim();

            if (icNumber.matches("\\d{12}")) {
                return icNumber;
            } else {
                System.out.println("Invalid IC number. It must be exactly 12 digits.");
            }
        }
    }

    public String inputPatientPhoneNo() {
        while (true) {
            System.out.print("Enter patient phone number: ");
            String phoneNo = scanner.nextLine().trim();
            
            if (phoneNo.matches("\\d{10,11}")) {
                return phoneNo;
            } else {
                System.out.println("Invalid phone number. Please enter 10-11 digits.");
            }
        }
    }

    public Patient inputPatientDetails() {
        String patientIC = inputPatientIC();
        String patientName = inputPatientName();
        String patientGender = inputPatientGender();
        int patientAge = inputPatientAge();
        String patientPhoneNo = inputPatientPhoneNo();
        System.out.println();
        
        String patientID = String.format("P%03d", patientIDCounter++);
        return new Patient(patientID, patientIC, patientName, patientGender, patientAge, patientPhoneNo);
    }

    public Patient nextPatient(Queue<Patient> patientQueue) {
        return patientQueue.poll();
    }

    public void listPatientList(ListInterface<Patient> patientList) {
        System.out.println("\n==============================");
        System.out.println("        Patient List      ");
        System.out.println("==============================");

        if (patientList.isEmpty()) {
            System.out.println("No patient records yet.");
        } else {

            String header = String.format("%-5s %-15s %-15s %-10s %-10s %-15s\n",
                    "ID", "IC", "Name", "Age", "Gender", "Phone No");
            System.out.println(header);
            System.out.println("-----------------------------------------------------------------------");

            for (int i = 1; i <= patientList.getNumberOfEntries(); i++) {
                Patient patient = patientList.getEntry(i);
                String row = String.format("%-5s %-15s %-15s %-10d %-10s %-15s",
                        patient.getPatientId(),
                        patient.getIC(),
                        patient.getName(),
                        patient.getAge(),
                        patient.getGender(),
                        patient.getPhoneNo());
                System.out.println(row);
            }
        }
    }

    public boolean updatePatientField(ListInterface<Patient> patientList, String patientID, String field, Object newValue) {
        Patient p = null;
        for (int i = 1; i <= patientList.getNumberOfEntries(); i++) {
            Patient patient = patientList.getEntry(i);
            if (patient.getPatientId().equals(patientID)) {
                p = patient;
                break;
            }
        }

        if (p == null) {
            System.out.println("Patient not found.");
            return false;
        }

        switch (field.toLowerCase()) {
            case "name":
                p.setName((String) newValue);
                break;
            case "age":
                p.setAge((Integer) newValue);
                break;
            case "gender":
                p.setGender((String) newValue);
                break;
            case "ic":
                p.setIC((String) newValue);
                break;
            case "phoneno":
            case "phone":
                p.setPhoneNo((String) newValue);
                break;
            default:
                System.out.println("Invalid field name. Valid fields: name, age, gender, ic, phoneno");
                return false;
        }

        return true;
    }

    public void patientReports(ListInterface<Patient> patientList) {
        int count = patientList.getNumberOfEntries();
        System.out.println("=== Patient Report ===");
        System.out.println("Total patients: " + count);

        int maleCount = 0, femaleCount = 0;
        for (int i = 1; i <= patientList.getNumberOfEntries(); i++) {
            Patient p = patientList.getEntry(i);
            if (p.getGender().equalsIgnoreCase("M")) {
                maleCount++;
            } else if (p.getGender().equalsIgnoreCase("F")) {
                femaleCount++;
            }
        }
        System.out.println("Male patients: " + maleCount);
        System.out.println("Female patients: " + femaleCount);
    }

    public Patient findPatientById(ListInterface<Patient> patientList, String patientID) {
        for (int i = 1; i <= patientList.getNumberOfEntries(); i++) {
            Patient patient = patientList.getEntry(i);
            if (patient.getPatientId().equals(patientID)) {
                return patient;
            }
        }
        return null;
    }

    public String inputPatientID() {
        System.out.print("Enter patient ID: ");
        return scanner.nextLine().trim();
    }

    public int getModificationChoice() {
        System.out.println("\nModify Patient Information");
        System.out.println("1. Update Name");
        System.out.println("2. Update Age");
        System.out.println("3. Update Gender");
        System.out.println("4. Update IC Number");
        System.out.println("5. Update Phone Number");
        System.out.println("6. Back to Main Menu");
        System.out.print("Enter choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine();
        return choice;
    }
}