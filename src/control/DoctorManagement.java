/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package control;

import DAO.Dao;
import adt.ArrayList;
import adt.ArrayStack;
import adt.DoubleLinkedList;
import boundary.DoctorUI;
import entity.Doctor;
import java.util.InputMismatchException;
import utility.Command;
import utility.MessageUI;

/**
 *
 * @author kosoo
 */
public class DoctorManagement {

    private static final String FILE_NAME = "src\\DAO\\doctor.txt";
    private Dao dao = new Dao();
    private MessageUI messageUI = new MessageUI();
    private DoctorUI doctorUI = new DoctorUI();
    private Command command = new Command();

    public void runDoctorManagement() {

        int choice = 0;

        while (true) {
            try {
                messageUI.clearScreen();
                doctorUI.displayDoctorMenu();
                choice = doctorUI.getMenuChoice();

                switch (choice) {
                    case 1:
                        addDoctor();
                        break;
                    case 2:
                        removeDoctor();
                        break;
                    case 3:
                        updateDoctorDetails();
                        break;
                    case 4:
                        searchDoctor();
                        break;
                    case 5:
                        listAllDoctor();
                        break;
                    case 6:
                        filterDoctor();
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
                doctorUI.getScanner().nextLine();
            }
        }
    }

    public void addDoctor() {
        try {
            String id = generateDoctorId();
            System.out.println("Doctor ID : " + id);

            Doctor doctor = doctorUI.gatherDoctorDetails();

            if (doctor == null) {
                messageUI.displayInvalidMessage("Operation canceled by user.");
                command.pressEnterToContinue();
                return;
            }

            doctor.setDoctorId(id);

            // Read current doctors from file
            DoubleLinkedList<Doctor> doctors = readDoctorFromFileAsDLL();
            doctors.add(doctor);

            // Save updated list back to file
            dao.saveToFile(doctors, FILE_NAME);

            messageUI.displayValidMessage("Doctor added successfully with ID: " + id);
            command.pressEnterToContinue();
            doctors.clear();

        } catch (Exception e) {
            messageUI.displayInvalidMessage("Error adding doctor: " + e.getMessage());
            command.pressEnterToContinue();
        }
    }

    public String generateDoctorId() {
        String lastId = getLastDoctorId();
        if (lastId == null) {
            return "D001"; // First doctor starts from D001
        } else {
            int newId = Integer.parseInt(lastId.substring(1)) + 1;
            return "D" + String.format("%03d", newId);
        }
    }

    public String getLastDoctorId() {
        DoubleLinkedList<Doctor> doctors = dao.readTextFile(FILE_NAME, 1, parts -> {
            String doctorId = parts[0];

            Doctor doctor = new Doctor();
            doctor.setDoctorId(doctorId);
            return doctor;
        });

        if (doctors.sizeOf() == 0) {
            doctors.clear();
            return null;
        }

        int maxId = 0;

        for (int i = 0; i < doctors.sizeOf(); i++) {
            Doctor doctor = doctors.get(i);
            try {
                if (doctor.getDoctorId() != null && doctor.getDoctorId().startsWith("D")) {
                    int currentId = Integer.parseInt(doctor.getDoctorId().substring(1));
                    if (currentId > maxId) {
                        maxId = currentId;
                    }
                }
            } catch (NumberFormatException e) {
                System.out.println("Error parsing Doctor ID: " + doctor.getDoctorId());
            }
        }

        doctors.clear();
        return "D" + String.format("%03d", maxId);
    }

    private void removeDoctor() {
        String doctorId = doctorUI.getDoctorId();

        if (doctorId == null) {
            messageUI.displayInvalidMessage("Operation canceled by user.");
            command.pressEnterToContinue();
            return;
        }

        Doctor userInputDoctor = new Doctor(doctorId);

        try {
            DoubleLinkedList<Doctor> doctorList = readDoctorFromFileAsDLL();

            Doctor doctorToRemove = null;
            int removeIndex = -1;

            for (int i = 0; i < doctorList.sizeOf(); i++) {
                Doctor doctor = doctorList.get(i);
                if (doctor.equals(userInputDoctor)) {
                    doctorToRemove = doctor;
                    removeIndex = i;
                    doctorList.remove(i);
                    break;
                }
            }

            if (doctorToRemove != null) {
                doctorUI.displayDoctorDetails(doctorToRemove);

                boolean confirmed = doctorUI.getConfirmation("Are you sure you want to delete this doctor? (Y/N): ");

                if (confirmed) {
                    // Save updated list to file
                    dao.saveToFile(doctorList, FILE_NAME);
                    messageUI.displayValidMessage("Doctor removed successfully.");
                    command.pressEnterToContinue();
                } else {
                    // Undo removal if cancelled
                    doctorList.add(removeIndex, doctorToRemove);
                    messageUI.displayInvalidMessage("Doctor removal cancelled.");
                    command.pressEnterToContinue();
                }
            } else {
                messageUI.displayInvalidMessage("Doctor ID not found.");
                command.pressEnterToContinue();
            }

        } catch (Exception e) {
            messageUI.displayInvalidMessage("Error removing doctor: " + e.getMessage());
            command.pressEnterToContinue();
        }
    }

    public void updateDoctorDetails() {
        String doctorId = doctorUI.getDoctorId();
        if (doctorId == null) {
            messageUI.displayInvalidMessage("Operation canceled by user.");
            command.pressEnterToContinue();
            return;
        }

        Doctor userInputDoctor = new Doctor(doctorId);

        try {
            DoubleLinkedList<Doctor> doctors = readDoctorFromFileAsDLL();

            Doctor doctorToUpdate = null;

            for (int i = 0; i < doctors.sizeOf(); i++) {
                Doctor doctor = doctors.get(i);
                if (doctor.equals(userInputDoctor)) {
                    doctorToUpdate = doctor;
                    break;
                }
            }

            if (doctorToUpdate != null) {
                doctorUI.displayDoctorDetails(doctorToUpdate);

                String choice = doctorUI.getUpdateChoice();
                switch (choice) {
                    case "1":
                        String newName = doctorUI.getUserInput("Enter Doctor Name (or 'X' to exit): ",
                                "Error: Name cannot be blank.");
                        doctorToUpdate.setName(newName);
                        break;
                    case "2":
                        String newGender = doctorUI.getUserInputWithRegex("Enter Gender (M/F, or 'X' to exit): ",
                                "Error: Please enter 'M' or 'F'.", "[MFmf]");
                        doctorToUpdate.setGender(newGender);
                        break;
                    case "3":
                        String newPhone = doctorUI.getUserInputWithRegex("Enter Phone Number (or 'X' to exit): ",
                                "Error: Invalid Phone Number. Please enter a 10-digit number.", "\\d{10}");
                        doctorToUpdate.setPhoneNo(newPhone);
                        break;
                    case "5":
                        String newEmail = doctorUI.getUserInputWithRegex("Enter Email (or 'X' to exit): ",
                                "Error: Invalid Email format.", "^[\\w._%+-]+@[\\w.-]+\\.[A-Za-z]{2,6}$");
                        doctorToUpdate.setEmail(newEmail);
                        break;
                    case "6":
                        String newDutySchedule = doctorUI.getUserInputWithRegex(
                                "Enter Duty Schedule (Format: Mon-Fri 09:00-16:00, or 'X' to exit): ",
                                "Error: Duty Schedule must be in format (Day-Day HH:MM-HH:MM). Example: Mon-Fri 09:00-17:00",
                                "^[A-Za-z]{3}-[A-Za-z]{3}\\s\\d{1,2}(:\\d{2})?-[0-2]?\\d(:\\d{2})?$"
                        );
                        doctorToUpdate.setDutySchedule(newDutySchedule);
                        break;
                    case "7":
                        String newAvailabilityStr = doctorUI.getUserInputWithRegex("Is the doctor available? (Y/N, or 'X' to exit): ",
                                "Error: Must be Y or N.", "[YyNn]");
                        boolean availability = newAvailabilityStr.equalsIgnoreCase("Y");
                        doctorToUpdate.setAvailability(availability);
                        break;
                    case "8":
                        messageUI.displayValidMessage("Returning to Doctor Menu...");
                        command.pressEnterToContinue();
                        return;
                    default:
                        messageUI.displayInvalidMessage("Invalid choice.");
                        command.pressEnterToContinue();
                        return;
                }

                dao.saveToFile(doctors, FILE_NAME);

                doctors.clear();
                messageUI.displayValidMessage("Doctor details updated successfully.");
                doctorUI.displayDoctorDetails(doctorToUpdate);

                command.pressEnterToContinue();
            } else {
                messageUI.displayInvalidMessage("Doctor ID not found.");
                command.pressEnterToContinue();
            }
        } catch (Exception e) {
            messageUI.displayInvalidMessage("Error updating doctor details: " + e.getMessage());
            command.pressEnterToContinue();
        }
    }

    public void searchDoctor() {

        String doctorId = doctorUI.getDoctorId();

        if (doctorId == null || doctorId.trim().isEmpty()) {
            messageUI.displayInvalidMessage("Operation canceled by user.");
            command.pressEnterToContinue();
            return;
        }

        try {
            Doctor userInputDoctor = new Doctor(doctorId);

            ArrayList<Doctor> doctors = dao.readTextFileAsArrayList(FILE_NAME, 6, this::parseDoctorFromParts);

            Doctor foundDoctor = null;
            for (Doctor doctor : doctors) {
                if (doctor.equals(userInputDoctor)) {
                    foundDoctor = doctor;
                    break;
                }
            }

            if (foundDoctor != null) {
                System.out.println("\n\nDoctor Found: ");
                doctorUI.displayDoctorDetails(foundDoctor);
            } else {
                messageUI.displayInvalidMessage("Doctor ID not found.");
            }

            command.pressEnterToContinue();
            doctors.clear();

        } catch (Exception e) {
            messageUI.displayInvalidMessage("Error searching doctor: " + e.getMessage());
            command.pressEnterToContinue();
        }

    }

    public void listAllDoctor() {

        String choice = doctorUI.getUserSortingPreference();

        switch (choice) {
            case "1":
                listDoctorByAscending();
                break;
            case "2":
                listDoctorByDescending();
                break;
            case "3":
                messageUI.displayValidMessage("Exit...");
                command.pressEnterToContinue();
            default:
                System.out.println("Invalid choice. Please try again.");
                listAllDoctor();
        }
    }

    // double linked list
    public void listDoctorByAscending() {

        try {
            // Read the donations from the file as a DoublyLinkedList
            DoubleLinkedList<Doctor> doctors = readDoctorFromFileAsDLL();

            if (doctors.sizeOf() > 0) {
                doctorUI.displayDoctorReport(doctors, "All Doctors List by Ascending Order");
            } else {
                messageUI.displayInvalidMessage("No doctors available to display.");
            }

            command.pressEnterToContinue();
        } catch (Exception e) {
            messageUI.displayInvalidMessage("Error listing doctor: " + e.getMessage());
            command.pressEnterToContinue();
        }
    }

    // arrayStack read the data then display as arraylist
    public void listDoctorByDescending() { // use the stack to let id by decending

        ArrayStack<Doctor> doctors = readDoctorFromFileAsArrayStack();
        ArrayList<Doctor> newDoctors = new ArrayList<>();
        while (!doctors.isEmpty()) {
            newDoctors.add(doctors.pop());
        }
        if (!newDoctors.isEmpty()) {
            doctorUI.displayDoctorReport(newDoctors, "All Doctors List by Descending Order");
            command.pressEnterToContinue();
        } else {
            messageUI.displayInvalidMessage("No doctors available to display.");
            command.pressEnterToContinue();
        }
    }

    private void filterDoctor() {
        String choice = doctorUI.getFilterChoice();

        switch (choice) {
            case "1":
                filterDoctorByGender();
                break;
            case "2":
                filterLastest10Doctor();
                break;
            case "3":
                messageUI.displayValidMessage("Exit...");
                command.pressEnterToContinue();
                return;
            default:
                messageUI.displayInvalidMessage("Invalid choice. Please try again.");
                filterDoctor();
        }
    }

    // double linked list
    private void filterDoctorByGender() {
        String gender = doctorUI.getUserInputWithRegex(
                "Enter Doctor Gender (Male = M | Female = F, or 'X' to exit): ",
                "Error: Invalid gender. Please enter 'M' or 'F'.",
                "[MFmf]"
        );

        DoubleLinkedList<Doctor> doctorList = readDoctorFromFileAsDLL();

        DoubleLinkedList<Doctor> filteredList = (DoubleLinkedList<Doctor>) doctorList.where(
                doctor -> doctor.getGender().equalsIgnoreCase(gender)
        );

        if (filteredList.sizeOf() > 0) {
            doctorUI.displayDoctorReport(filteredList, "Filter Doctor Result");
            command.pressEnterToContinue();
        } else {
            messageUI.displayInvalidMessage("No doctors found with the specified gender.");
            command.pressEnterToContinue();
        }

        doctorList.clear();
        filteredList.clear();
    }

    private void filterLastest10Doctor() {
        ArrayStack<Doctor> doctorStack = readDoctorFromFileAsArrayStack();

        ArrayList<Doctor> latest10Doctors = new ArrayList<>();

        while (!doctorStack.isEmpty() && latest10Doctors.sizeOf() < 10) {
            latest10Doctors.add(doctorStack.pop());
        }

        if (!latest10Doctors.isEmpty()) {
            doctorUI.displayDoctorReport(latest10Doctors, "Latest 10 Doctor Registration Records");
            command.pressEnterToContinue();
        } else {
            messageUI.displayInvalidMessage("No doctors found.");
            command.pressEnterToContinue();
        }

        latest10Doctors.clear();
        doctorStack.clear();
    }

    private void generateSummaryReports() {
        ArrayList<Doctor> doctorList = readDoctorFromFileAsArrayList();

        if (!doctorList.isEmpty()) {
            int maleCount = 0;
            int femaleCount = 0;

            int availableCount = 0;
            int notAvailableCount = 0;

            for (Doctor doctor : doctorList) {
                // Count gender
                if (doctor.getGender().equalsIgnoreCase("M")) {
                    maleCount++;
                } else if (doctor.getGender().equalsIgnoreCase("F")) {
                    femaleCount++;
                }

                // Count available groups
                if (doctor.isAvailability()) {
                    availableCount++;
                } else {
                    notAvailableCount++;
                }
            }

            // Display report header
            System.out.println("\n\n=====================================");
            System.out.println("   Doctor Demographics Report");
            System.out.println("=====================================");
            System.out.println("Total Doctors: " + doctorList.sizeOf());
            System.out.println("Male: " + maleCount);
            System.out.println("Female: " + femaleCount);
            System.out.println("=====================================");

            // Age summary
            System.out.println("\n\n=====================================");
            System.out.println("      Doctor Availability Report");
            System.out.println("=====================================");
            System.out.println("Total Doctors     : " + doctorList.sizeOf());
            System.out.println("Available         : " + availableCount);
            System.out.println("Not Available     : " + notAvailableCount);
            System.out.println("=====================================");

            command.pressEnterToContinue();
            doctorList.clear();
        } else {
            messageUI.displayInvalidMessage("No doctors found.");
            command.pressEnterToContinue();
        }
    }

    private DoubleLinkedList<Doctor> readDoctorFromFileAsDLL() {
        return dao.readTextFile(FILE_NAME, 7, this::parseDoctorFromParts);
    }

    private ArrayStack<Doctor> readDoctorFromFileAsArrayStack() {
        return dao.readTextFileAsArrayStack(FILE_NAME, 7, this::parseDoctorFromParts);
    }

    ArrayList<Doctor> readDoctorFromFileAsArrayList() {
        return dao.readTextFileAsArrayList(FILE_NAME, 7, this::parseDoctorFromParts);
    }

    private Doctor parseDoctorFromParts(String[] parts) {
        try {
            String id = parts[0];
            String name = parts[1];
            String gender = parts[2];
            String phoneNo = parts[3];
            String email = parts[4];
            String dutySchedule = parts[5];
            boolean availability = parts[6].equalsIgnoreCase("Available");

            return new Doctor(id, name, gender, phoneNo, email, dutySchedule, availability);
        } catch (Exception e) {
            System.err.println("Error parsing doctor record: " + e.getMessage());
            return null;
        }
    }

    public static void DoctorRun() {
        DoctorManagement doctor = new DoctorManagement();
        doctor.runDoctorManagement();
    }
}
