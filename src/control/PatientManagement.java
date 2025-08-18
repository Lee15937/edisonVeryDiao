/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package control;

import adt.ArrayList;
import adt.ArrayStack;
import adt.DoubleLinkedList;
import boundary.PatientUI;
import DAO.Dao;
import entity.Patient;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.InputMismatchException;
import utility.Command;
import utility.MessageUI;

public class PatientManagement {

    private static final String FILE_NAME = "src\\DAO\\patient.txt";
    private Dao dao = new Dao();
    private MessageUI messageUI = new MessageUI();
    private PatientUI patientUI = new PatientUI();
    private Command command = new Command();

    public void runPatientManagement() {

        int choice = 0;

        while (true) {
            try {
                messageUI.clearScreen();
                patientUI.displayPatientMenu();
                choice = patientUI.getMenuChoice();

                switch (choice) {
                    case 1:
                        addPatient();
                        break;
                    case 2:
                        removePatient();
                        break;
                    case 3:
                        updatePatientDetails();
                        break;
                    case 4:
                        searchPatient();
                        break;
                    case 5:
                        listAllPatients();
                        break;
                    case 6:
                        filterPatients();
                        break;
                    case 7:
                        generateSummaryReports();
                        break;
                    case 8:
                        messageUI.displayExitMessage();
                        command.pressEnterToContinue();
                        return;
                    default:
                        messageUI.displayInvalidChoiceMessage();
                        command.pressEnterToContinue();
                }
            } catch (InputMismatchException e) {
                messageUI.displayInvalidChoiceIntegerMessage();
                command.pressEnterToContinue();
                patientUI.getScanner().nextLine();
            }
        }
    }

    public void addPatient() {
        try {
            String id = generatePatientId();
            System.out.println("Patient ID : " + id);

            Patient patient = patientUI.gatherPatientDetails();

            if (patient == null) {
                messageUI.displayInvalidMessage("Operation canceled by user.");
                command.pressEnterToContinue();
                return;
            }

            patient.setPatientId(id);

            // Read current patients from file
            DoubleLinkedList<Patient> patients = readPatientFromFileAsDLL();
            patients.add(patient);
            
            // need to add a validation to validate ic is it have this person if have con not add this person again because dupplicate
            // Save updated list back to file
            dao.saveToFile(patients, FILE_NAME);

            messageUI.displayValidMessage("Patient added successfully with ID: " + id);
            command.pressEnterToContinue();
            patients.clear();

        } catch (Exception e) {
            messageUI.displayInvalidMessage("Error adding patient: " + e.getMessage());
            command.pressEnterToContinue();
        }
    }

    public String generatePatientId() {
        String lastId = getLastPatientId();
        if (lastId == null) {
            return "1"; // First patient starts from 1
        } else {
            int newId = Integer.parseInt(lastId) + 1;
            return String.valueOf(newId);
        }
    }

    public String getLastPatientId() {

        DoubleLinkedList<Patient> patients = dao.readTextFile(FILE_NAME, 1, parts -> {
            String patientId = parts[0];

            Patient patient = new Patient();
            patient.setPatientId(patientId);
            return patient;
        });

        if (patients.sizeOf() == 0) {
            patients.clear();
            return null;
        }

        int maxId = 0;

        for (int i = 0; i < patients.sizeOf(); i++) {
            Patient patient = patients.get(i);
            try {
                int currentId = Integer.parseInt(patient.getPatientId());
                if (currentId > maxId) {
                    maxId = currentId;
                }
            } catch (NumberFormatException e) {
                patients.clear();
                System.out.println("Error parsing Patient ID: " + patient.getPatientId());
            }
        }
        patients.clear();
        return String.valueOf(maxId);
    }

    private void removePatient() {
        String patientId = patientUI.getPatientId();

        if (patientId == null) {
            messageUI.displayInvalidMessage("Operation canceled by user.");
            command.pressEnterToContinue();
            return;
        }

        Patient userInputPatient = new Patient(patientId);

        try {
            DoubleLinkedList<Patient> patientList = readPatientFromFileAsDLL();

            Patient patientToRemove = null;
            int removeIndex = -1;

            for (int i = 0; i < patientList.sizeOf(); i++) {
                Patient patient = patientList.get(i);
                if (patient.equals(userInputPatient)) {
                    patientToRemove = patient;
                    removeIndex = i;
                    patientList.remove(i);
                    break;
                }
            }

            if (patientToRemove != null) {
                patientUI.displayPatientDetails(patientToRemove);

                boolean confirmed = patientUI.getConfirmation("Are you sure you want to delete this patient? (Y/N): ");

                if (confirmed) {
                    // Save updated list to file
                    dao.saveToFile(patientList, FILE_NAME);
                    messageUI.displayValidMessage("Patient removed successfully.");
                    command.pressEnterToContinue();
                } else {
                    // Undo removal if cancelled
                    patientList.add(removeIndex, patientToRemove);
                    messageUI.displayInvalidMessage("Patient removal cancelled.");
                    command.pressEnterToContinue();
                }
            } else {
                messageUI.displayInvalidMessage("Patient ID not found.");
                command.pressEnterToContinue();
            }

        } catch (Exception e) {
            messageUI.displayInvalidMessage("Error removing patient: " + e.getMessage());
            command.pressEnterToContinue();
        }
    }

    public void updatePatientDetails() {
        String patientId = patientUI.getPatientId();
        if (patientId == null) {
            messageUI.displayInvalidMessage("Operation canceled by user.");
            command.pressEnterToContinue();
            return;
        }

        Patient userInputPatient = new Patient(patientId);

        try {
            DoubleLinkedList<Patient> patients = readPatientFromFileAsDLL();

            Patient patientToUpdate = null;

            for (int i = 0; i < patients.sizeOf(); i++) {
                Patient patient = patients.get(i);
                if (patient.equals(userInputPatient)) {
                    patientToUpdate = patient;
                    break;
                }
            }

            if (patientToUpdate != null) {
                patientUI.displayPatientDetails(patientToUpdate);

                String choice = patientUI.getUpdateChoice();
                switch (choice) {
                    case "1":
                        String newName = patientUI.getUserInput("Enter Patient Name (or 'X' to exit): ",
                                "Error: Name cannot be blank.");
                        patientToUpdate.setName(newName);
                        break;
                    case "2":
                        String newIc = patientUI.getUserInputWithRegex("Enter IC (or 'X' to exit): ",
                                "Error: Invalid IC format. Please enter a 12-digit number.", "\\d{12}");
                        patientToUpdate.setIC(newIc);
                        break;
                    case "3":
                        String newPhone = patientUI.getUserInputWithRegex("Enter Phone Number (or 'X' to exit): ",
                                "Error: Invalid Phone Number. Please enter a 10-digit number.", "\\d{10}");
                        patientToUpdate.setPhoneNo(newPhone);
                        break;
                    case "4":
                        String newGender = patientUI.getUserInputWithRegex("Enter Gender (M/F, or 'X' to exit): ",
                                "Error: Please enter 'M' or 'F'.", "[MF]");
                        patientToUpdate.setGender(newGender);
                        break;
                    case "5":
                        String newAge = patientUI.getUserInputWithRegex("Enter Age (or 'X' to exit): ",
                                "Error: Please enter a valid age (numeric).", "\\d+");
                        patientToUpdate.setAge(Integer.parseInt(newAge));
                        break;
                    case "6":
                        messageUI.displayValidMessage("Returning to Patient Menu...");
                        command.pressEnterToContinue();
                        return;
                    default:
                        messageUI.displayInvalidMessage("Invalid choice.");
                        command.pressEnterToContinue();
                        return;
                }

                dao.saveToFile(patients, FILE_NAME);

                patients.clear();
                messageUI.displayValidMessage("Patient details updated successfully.");
                patientUI.displayPatientDetails(patientToUpdate);

                command.pressEnterToContinue();
            } else {
                messageUI.displayInvalidMessage("Patient ID not found.");
                command.pressEnterToContinue();
            }
        } catch (Exception e) {
            messageUI.displayInvalidMessage("Error updating patient details: " + e.getMessage());
            command.pressEnterToContinue();
        }
    }

    public void searchPatient() {

        String patientId = patientUI.getPatientId();

        if (patientId == null || patientId.trim().isEmpty()) {
            messageUI.displayInvalidMessage("Operation canceled by user.");
            command.pressEnterToContinue();
            return;
        }

        try {
            Patient userInputPatient = new Patient(patientId);

            ArrayList<Patient> patients = dao.readTextFileAsArrayList(FILE_NAME, 6, this::parsePatientFromParts);

            Patient foundPatient = null;
            for (Patient patient : patients) {
                if (patient.equals(userInputPatient)) {
                    foundPatient = patient;
                    break;
                }
            }

            if (foundPatient != null) {
                System.out.println("\n\nPatient Found: ");
                patientUI.displayPatientDetails(foundPatient);
            } else {
                messageUI.displayInvalidMessage("Patient ID not found.");
            }

            command.pressEnterToContinue();
            patients.clear();

        } catch (Exception e) {
            messageUI.displayInvalidMessage("Error searching patient: " + e.getMessage());
            command.pressEnterToContinue();
        }

    }

    public void listAllPatients() {

        String choice = patientUI.getUserSortingPreference();

        switch (choice) {
            case "1":
                listPatientByAscending();
                break;
            case "2":
                listPatientByDescending();
                break;
            case "3":
                messageUI.displayValidMessage("Exit...");
                command.pressEnterToContinue();
            default:
                System.out.println("Invalid choice. Please try again.");
                listAllPatients();  // Recursive call to prompt the user again
        }
    }

    // double linked list
    public void listPatientByAscending() {

        try {
            // Read the donations from the file as a DoublyLinkedList
            DoubleLinkedList<Patient> patients = readPatientFromFileAsDLL();

            if (patients.sizeOf() > 0) {
                patientUI.displayPatientReport(patients, "All Patients List by Ascending Order");
            } else {
                messageUI.displayInvalidMessage("No patients available to display.");
            }

            command.pressEnterToContinue();
        } catch (Exception e) {
            messageUI.displayInvalidMessage("Error listing patient: " + e.getMessage());
            command.pressEnterToContinue();
        }
    }

    // arrayStack read the data then display as arraylist
    public void listPatientByDescending() { // use the stack to let id by decending

        ArrayStack<Patient> patients = readPatientFromFileAsArrayStack();
        ArrayList<Patient> newPatients = new ArrayList<>();
        while (!patients.isEmpty()) {
            newPatients.add(patients.pop());
        }
        if (!newPatients.isEmpty()) {
            patientUI.displayPatientReport(newPatients, "All Patients List by Descending Order");
            command.pressEnterToContinue();
        } else {
            messageUI.displayInvalidMessage("No patients available to display.");
            command.pressEnterToContinue();
        }
    }

    private void filterPatients() {
        String choice = patientUI.getFilterChoice();

        switch (choice) {
            case "1":
                filterPatientByGender();
                break;
            case "2":
                filterLastest10Patients();
                break;
            case "3":
                messageUI.displayValidMessage("Exit...");
                command.pressEnterToContinue();
                return;
            default:
                messageUI.displayInvalidMessage("Invalid choice. Please try again.");
                filterPatients();
        }
    }

    // double linked list
    private void filterPatientByGender() {
        String gender = patientUI.getUserInputWithRegex(
                "Enter Patient Gender (Male = M | Female = F, or 'X' to exit): ",
                "Error: Invalid gender. Please enter 'M' or 'F'.",
                "[MF]"
        );

        DoubleLinkedList<Patient> patientList = readPatientFromFileAsDLL();

        DoubleLinkedList<Patient> filteredList = (DoubleLinkedList<Patient>) patientList.where(
                patient -> patient.getGender().equalsIgnoreCase(gender)
        );

        if (filteredList.sizeOf() > 0) {
            patientUI.displayPatientReport(filteredList, "Filter Patient Result");
            command.pressEnterToContinue();
        } else {
            messageUI.displayInvalidMessage("No patients found with the specified gender.");
            command.pressEnterToContinue();
        }

        patientList.clear();
        filteredList.clear();
    }

    private void filterLastest10Patients() {
        ArrayStack<Patient> patientStack = readPatientFromFileAsArrayStack();

        ArrayList<Patient> latest10Patients = new ArrayList<>();

        while (!patientStack.isEmpty() && latest10Patients.sizeOf() < 10) {
            latest10Patients.add(patientStack.pop());
        }

        if (!latest10Patients.isEmpty()) {
            patientUI.displayPatientReport(latest10Patients, "Latest 10 Patient Registration Records");
            command.pressEnterToContinue();
        } else {
            messageUI.displayInvalidMessage("No patients found.");
            command.pressEnterToContinue();
        }

        latest10Patients.clear();
        patientStack.clear();
    }

    private void generateSummaryReports() {
        ArrayList<Patient> patientList = readPatientFromFileAsArrayList();

        if (!patientList.isEmpty()) {
            int maleCount = 0;
            int femaleCount = 0;

            int below18 = 0;
            int between18And30 = 0;
            int between31And50 = 0;
            int above50 = 0;

            for (Patient patient : patientList) {
                // Count gender
                if (patient.getGender().equalsIgnoreCase("M")) {
                    maleCount++;
                } else if (patient.getGender().equalsIgnoreCase("F")) {
                    femaleCount++;
                }

                // Count age groups
                int age = patient.getAge();
                if (age < 18) {
                    below18++;
                } else if (age <= 30) {
                    between18And30++;
                } else if (age <= 50) {
                    between31And50++;
                } else {
                    above50++;
                }
            }

            // Display report header
            System.out.println("\n\n=====================================");
            System.out.println("   Patient Demographics Report");
            System.out.println("=====================================");

            // Gender summary
            System.out.println("Total Patients: " + patientList.sizeOf());
            System.out.println("Male: " + maleCount);
            System.out.println("Female: " + femaleCount);

            // Age summary
            System.out.println("\n=====================================");
            System.out.println("    Age Distribution:");
            System.out.println("=====================================");
            System.out.println("<18   : " + below18);
            System.out.println("18-30 : " + between18And30);
            System.out.println("31-50 : " + between31And50);
            System.out.println(">50   : " + above50);

            command.pressEnterToContinue();
            patientList.clear();
        } else {
            messageUI.displayInvalidMessage("No patients found.");
            command.pressEnterToContinue();
        }
    }

    private DoubleLinkedList<Patient> readPatientFromFileAsDLL() {
        return dao.readTextFile(FILE_NAME, 7, this::parsePatientFromParts);
    }

    private ArrayStack<Patient> readPatientFromFileAsArrayStack() {
        return dao.readTextFileAsArrayStack(FILE_NAME, 7, this::parsePatientFromParts);
    }

    ArrayList<Patient> readPatientFromFileAsArrayList() {
        return dao.readTextFileAsArrayList(FILE_NAME, 7, this::parsePatientFromParts);
    }

    private Patient parsePatientFromParts(String[] parts) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy");
        try {
            String id = parts[0];
            String ic = parts[1];
            String name = parts[2];
            String gender = parts[3];
            int age = Integer.parseInt(parts[4]);
            String phone = parts[5];
            Date date = sdf.parse(parts[6]);

            Patient patient = new Patient(id, ic, name, gender, age, phone);
            patient.setDate(new Date());
            return patient;
        } catch (Exception e) {
            System.err.println("Error parsing date: " + e.getMessage());
            return null;
        }
    }

    public static void PatientRun() {
        PatientManagement patient = new PatientManagement();
        patient.runPatientManagement();
    }

}
