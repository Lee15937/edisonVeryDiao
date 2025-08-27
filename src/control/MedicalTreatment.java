package control;

import DAO.Dao;
import adt.ArrayList;
import adt.ArrayStack;
import adt.DoubleLinkedList;
import boundary.MedicalTreatmentUI;
import entity.Treatment;
import entity.Consultation;
import entity.Consultation.Status;
import utility.Command;
import utility.MessageUI;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.InputMismatchException;


/**
 *
 * @author kosoo
 */
public class MedicalTreatment {

    private static final String FILE_NAME = "src\\DAO\\treatment.txt";
    private Dao dao = new Dao();
    private MessageUI messageUI = new MessageUI();
    private Command command = new Command();
    private MedicalTreatmentUI medicalTreatmentUI = new MedicalTreatmentUI();

    public void runTreatment() {

        int choice = 0;

        while (true) {
            try {
                messageUI.clearScreen();
                medicalTreatmentUI.displayTreatmentMenu();
                choice = medicalTreatmentUI.getMenuChoice();
                switch (choice) {
                    case 1:
                        addDiagnosis();
                        break;
                    case 2:
                        updateTreatments();
                        break;
                    case 3:
                        deleteTreatments();
                        break;
                    case 4:
                        searchTreatments();
                        break;
                    case 5:
                        listTreatment();
                        break;
                    case 6:
                        filterTreatment();
                        break;
                    case 7:
                        generateSummaryReport();
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
                medicalTreatmentUI.getScanner().nextLine();
            }
        }
    }

    public void addDiagnosis() {
        try {
            ConsultationManagement consultationManagement = new ConsultationManagement();
            ArrayList<Consultation> consultations = consultationManagement.readConsultationFromFileAsArrayList();

            if (consultations.isEmpty()) {
                messageUI.displayInvalidMessage("No consultations found. Please create a consultation first.");
                command.pressEnterToContinue();
                return;
            }

            ArrayList<Consultation> checkInConsultations = new ArrayList<>();
            for (Consultation c : consultations) {
                if (c.getStatus() == Status.CHECKED_IN) {   // compare enum directly
                    checkInConsultations.add(c);
                }
            }
            if (checkInConsultations.isEmpty()) {
                messageUI.displayInvalidMessage("No consultations with CHECK_IN status found. Diagnosis can only be added for CHECK_IN consultations.");
                command.pressEnterToContinue();
                return;
            }

            Consultation selectedConsultation = null;

            while (true) {
                System.out.println("\nSelect Consultation to add Diagnosis:");
                for (int i = 0; i < checkInConsultations.sizeOf(); i++) {
                    Consultation c = checkInConsultations.get(i);
                    System.out.println((i + 1) + ". " + c.getConsultationID()
                            + " | Patient: " + c.getPatientName()
                            + " | Doctor: " + c.getDoctorName()
                            + " | Status: " + c.getStatus());
                }

                String input = medicalTreatmentUI.getUserInput("Enter choice (1-" + checkInConsultations.sizeOf() + " or 'X' to exit): ",
                        "Error: Choice cannot be blank.");

                if (input == null) { // user pressed X
                    messageUI.displayValidMessage("Operation canceled by user.");
                    command.pressEnterToContinue();
                    return;
                }

                try {
                    int choice = Integer.parseInt(input);
                    if (choice >= 1 && choice <= checkInConsultations.sizeOf()) {
                        selectedConsultation = checkInConsultations.get(choice - 1);
                        break;
                    } else {
                        messageUI.displayInvalidMessage("Invalid choice. Please try again.");
                    }
                } catch (NumberFormatException e) {
                    messageUI.displayInvalidMessage("Invalid input. Please enter a number or 'X' to exit.");
                }
            }

            String id = generateTreatmentId();
            System.out.println("Treatment ID: " + id);

            Treatment treatment = medicalTreatmentUI.gatherTreatmentDetails();

            if (treatment == null) {
                messageUI.displayInvalidMessage("Operation canceled by user.");
                command.pressEnterToContinue();
                return;
            }

            treatment.setPatientName(selectedConsultation.getPatientName());
            treatment.setDoctorName(selectedConsultation.getDoctorName());
            treatment.setTreatmentId(id);

            DoubleLinkedList<Treatment> treatments = readTreatmentFromFileAsDLL();
            treatments.add(treatment);

            dao.saveToFile(treatments, FILE_NAME);

            // After adding treatment, update consultation status
            boolean updated = consultationManagement.updateStatusToCompleted(selectedConsultation.getConsultationID());
            if (updated) {
                messageUI.displayValidMessage("Consultation " + selectedConsultation.getConsultationID() + " marked as COMPLETED.");
            } else {
                messageUI.displayInvalidMessage("Unable to update Consultation status. It may already be COMPLETED.");
            }

            messageUI.displayValidMessage("Treatment added successfully with ID: " + id);
            command.pressEnterToContinue();

        } catch (Exception e) {
            messageUI.displayInvalidMessage("Error adding Treatments: " + e.getMessage());
        }
    }

    public String generateTreatmentId() {
        String lastId = getLastTreatmentId();
        if (lastId == null) {
            return "T001"; // First treatment starts from T001
        } else {
            int newId = Integer.parseInt(lastId.substring(1)) + 1;
            return "T" + String.format("%03d", newId);
        }
    }

    public String getLastTreatmentId() {
        DoubleLinkedList<Treatment> treatments = dao.readTextFile(FILE_NAME, 1, parts -> {
            String treatmentId = parts[0];

            Treatment treatment = new Treatment();
            treatment.setTreatmentId(treatmentId);
            return treatment;
        });

        if (treatments.sizeOf() == 0) {
            treatments.clear();
            return null;
        }

        int maxId = 0;

        for (int i = 0; i < treatments.sizeOf(); i++) {
            Treatment treatment = treatments.get(i);
            try {
                if (treatment.getTreatmentId() != null && treatment.getTreatmentId().startsWith("T")) {
                    int currentId = Integer.parseInt(treatment.getTreatmentId().substring(1));
                    if (currentId > maxId) {
                        maxId = currentId;
                    }
                }
            } catch (NumberFormatException e) {
                System.out.println("Error parsing Treatment ID: " + treatment.getTreatmentId());
            }
        }

        treatments.clear();
        return "T" + String.format("%03d", maxId);
    }

    public void updateTreatments() {
        try {
            String treatmentId = medicalTreatmentUI.getTreatmentId();
            if (treatmentId == null) {
                messageUI.displayInvalidMessage("Operation canceled by user.");
                return;
            }

            DoubleLinkedList<Treatment> treatments = readTreatmentFromFileAsDLL();
            Treatment treatmentToUpdate = null;

            for (int i = 0; i < treatments.sizeOf(); i++) {
                Treatment t = treatments.get(i);
                if (t.getTreatmentId().equals(treatmentId)) {
                    treatmentToUpdate = t;
                    break;
                }
            }

            if (treatmentToUpdate != null) {
                medicalTreatmentUI.displayTreatmentDetails(treatmentToUpdate);

                String choice = medicalTreatmentUI.getUpdateChoice();
                switch (choice) {
                    case "1":
                        String newDiagnosis = medicalTreatmentUI.getUpdatedValue("Diagnosis Description");
                        treatmentToUpdate.setDiagnosis(newDiagnosis);
                        break;
                    case "2":
                        String newTreatment = medicalTreatmentUI.getUpdatedValue("Treatment Details");
                        treatmentToUpdate.setTreatmentPlan(newTreatment);
                        break;
                    case "3":
                        String newQuantity = medicalTreatmentUI.getUserInputWithRegex("Enter Quantity (or 'X' to exit): ",
                                "Error: Please enter a valid quantity (numeric).", "\\d+");
                        treatmentToUpdate.setQuantity(Integer.parseInt(newQuantity));
                        break;
                    case "4":
                        String newDateStr = medicalTreatmentUI.getUpdatedValue("Follow-Up Date (yyyy-MM-dd)");
                        try {
                            Date newDate = new SimpleDateFormat("yyyy-MM-dd").parse(newDateStr);
                            treatmentToUpdate.setTreatmentDate(newDate);
                        } catch (Exception e) {
                            messageUI.displayInvalidMessage("Invalid date format. Please use yyyy-MM-dd.");
                            return;
                        }
                        break;
                    case "5": // Exit
                        messageUI.displayValidMessage("Returning to menu...");
                        command.pressEnterToContinue();
                        return;
                    default:
                        messageUI.displayInvalidMessage("Invalid choice.");
                        command.pressEnterToContinue();
                        return;
                }

                // Step 4: Save changes back
                dao.saveToFile(treatments, FILE_NAME);
                messageUI.displayValidMessage("Treatment details updated successfully.");
                medicalTreatmentUI.displayTreatmentDetails(treatmentToUpdate);

            } else {
                messageUI.displayInvalidMessage("Treatment ID not found.");
            }
        } catch (Exception e) {
            messageUI.displayInvalidMessage("Error updating treatment: " + e.getMessage());
        }

        command.pressEnterToContinue();
    }

    public void searchTreatments() {
        String treatmentId = medicalTreatmentUI.getTreatmentId();

        if (treatmentId == null || treatmentId.trim().isEmpty()) {
            messageUI.displayInvalidMessage("Operation canceled by user.");
            command.pressEnterToContinue();
            return;
        }

        try {
            ArrayList<Treatment> treatments = dao.readTextFileAsArrayList(FILE_NAME, 6, this::parseTreatmentFromParts);

            Treatment foundTreatment = null;
            for (Treatment t : treatments) {
                if (t.getTreatmentId().equalsIgnoreCase(treatmentId)) {
                    foundTreatment = t;
                    break;
                }
            }

            if (foundTreatment != null) {
                System.out.println("\n\nTreatment Found: ");
                medicalTreatmentUI.displayTreatmentDetails(foundTreatment);
            } else {
                messageUI.displayInvalidMessage("Treatment ID not found.");
            }

            command.pressEnterToContinue();
        } catch (Exception e) {
            messageUI.displayInvalidMessage("Error searching treatment: " + e.getMessage());
            command.pressEnterToContinue();
        }
    }

    public void deleteTreatments() {

        String treatmentId = medicalTreatmentUI.getTreatmentId();
        if (treatmentId == null || treatmentId.trim().isEmpty()) {
            messageUI.displayInvalidMessage("Operation canceled by user.");
            return; // User chose to exit
        }

        Treatment userInputTreatment = new Treatment(treatmentId);

        try {
            DoubleLinkedList<Treatment> treatments = readTreatmentFromFileAsDLL();

            Treatment treatmentToRemove = null;

            for (int i = 0; i < treatments.sizeOf(); i++) {
                Treatment treatment = treatments.get(i);

                if (treatment.equals(userInputTreatment)) {
                    treatmentToRemove = treatment;
                    treatments.remove(i);
                    break;
                }
            }

            if (treatmentToRemove != null) {
                medicalTreatmentUI.displayTreatmentDetails(treatmentToRemove);

                boolean confirmed = medicalTreatmentUI.getConfirmation(
                        "Are you sure you want to delete this treatment? (Y/N): ");

                if (confirmed) {
                    // Save changes to file
                    dao.saveToFile(treatments, FILE_NAME);
                    messageUI.displayValidMessage("Treatment removed successfully.");
                    command.pressEnterToContinue();

                } else {
                    // If cancelled, add back to list
                    treatments.add(treatmentToRemove);
                    messageUI.displayInvalidMessage("Treatment removal cancelled.");
                    command.pressEnterToContinue();
                }

            } else {
                messageUI.displayInvalidMessage("Treatment ID not found.");
                command.pressEnterToContinue();
            }

        } catch (Exception e) {
            messageUI.displayInvalidMessage("Error removing treatment: " + e.getMessage());
            command.pressEnterToContinue();
        }
    }

    public void listTreatment() {

        String choice = medicalTreatmentUI.getSortChoice();

        switch (choice) {
            case "1":
                listTreatmentByAscending();
                break;
            case "2":
                listTreatmentByDescending();
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
                listTreatment();
        }
    }

    public void listTreatmentByAscending() {
        try {
            DoubleLinkedList<Treatment> treatments = readTreatmentFromFileAsDLL();

            if (treatments.sizeOf() > 0) {
                medicalTreatmentUI.displayTreatmentReport(treatments, "All Treatments by Ascending Order");
            } else {
                messageUI.displayInvalidMessage("No treatments available to display.");
            }

            command.pressEnterToContinue();
        } catch (Exception e) {
            messageUI.displayInvalidMessage("Error listing treatments: " + e.getMessage());
            command.pressEnterToContinue();
        }
    }

    public void listTreatmentByDescending() { // id by decending
        ArrayStack<Treatment> treatments = readTreatmentFromFileAsArrayStack();// for decending also array stack no list
        ArrayList<Treatment> newTreatments = new ArrayList<>();
        while (!treatments.isEmpty()) {
            newTreatments.add(treatments.pop());
        }
        if (!newTreatments.isEmpty()) {
            medicalTreatmentUI.displayTreatmentReport(newTreatments, "All Treatments List by Descending Order");
            command.pressEnterToContinue();
        } else {
            messageUI.displayInvalidMessage("No Treatments available to display.");
            command.pressEnterToContinue();
        }
    }

    public void filterTreatment() {
        String choice = medicalTreatmentUI.getFilterChoice();

        switch (choice) {

            case "1":
                filterTreatmentByDoctor();
                break;
            case "2":
                filterTreatmentByPatient();
                break;
            case "3":
                filterLast10Treatments();
                break;
            case "4":
                messageUI.displayValidMessage("Exit...");
                command.pressEnterToContinue();
                return;
            default:
                messageUI.displayInvalidMessage("Invalid choice. Please try again.");
                filterTreatment();
        }
    }

    private void filterTreatmentByDoctor() {
        String doctorName = medicalTreatmentUI.getUserInputWithRegex(
                "Enter Doctor Name (or 'X' to exit): ",
                "Error: Invalid input. Please enter a valid Doctor name.",
                "^[A-Za-z\\s]+|X$" // allow alphabets, spaces, or 'X'
        );

        if (doctorName.equalsIgnoreCase("X")) {
            return; // exit filter
        }

        DoubleLinkedList<Treatment> treatmentList = readTreatmentFromFileAsDLL();

        DoubleLinkedList<Treatment> filteredList = (DoubleLinkedList<Treatment>) treatmentList.where(treatment -> {
            return treatment.getDoctorName().equalsIgnoreCase(doctorName);
        });

        if (filteredList.sizeOf() > 0) {
            medicalTreatmentUI.displayTreatmentReport(filteredList, "Treatment Report for Doctor: " + doctorName);
            command.pressEnterToContinue();
        } else {
            messageUI.displayInvalidMessage("No treatments found for Doctor: " + doctorName);
            command.pressEnterToContinue();
        }
    }

    private void filterTreatmentByPatient() {
        // Step 1: Ask user for patient name
        String patientName = medicalTreatmentUI.getUserInputWithRegex(
                "Enter Patient Name (or 'X' to exit): ",
                "Error: Invalid input. Please enter a valid Patient name.",
                "^[A-Za-z\\s]+|X$" // allow alphabets, spaces, or 'X'
        );

        if (patientName.equalsIgnoreCase("X")) {
            return;
        }

        DoubleLinkedList<Treatment> treatmentList = readTreatmentFromFileAsDLL();

        DoubleLinkedList<Treatment> filteredList = (DoubleLinkedList<Treatment>) treatmentList.where(treatment -> {
            return treatment.getPatientName().equalsIgnoreCase(patientName);
        });

        if (filteredList.sizeOf() > 0) {
            medicalTreatmentUI.displayTreatmentReport(filteredList, "Treatment Report for Patient: " + patientName);
            command.pressEnterToContinue();
        } else {
            messageUI.displayInvalidMessage("No treatments found for Patient: " + patientName);
            command.pressEnterToContinue();
        }
    }

    private void filterLast10Treatments() {
        ArrayStack<Treatment> treatmentStack = readTreatmentFromFileAsArrayStack();

        ArrayList<Treatment> latest10Treatments = new ArrayList<>();

        while (!treatmentStack.isEmpty() && latest10Treatments.sizeOf() < 10) {
            latest10Treatments.add(treatmentStack.pop());
        }

        if (!latest10Treatments.isEmpty()) {
            medicalTreatmentUI.displayTreatmentReport(latest10Treatments, "Latest 10 Treatment Registration Records");
            command.pressEnterToContinue();
        } else {
            messageUI.displayInvalidMessage("No treatment record found.");
            command.pressEnterToContinue();
        }

        latest10Treatments.clear();
        treatmentStack.clear();
    }

    private void generateSummaryReport() {
        DoubleLinkedList<Treatment> treatmentList = readTreatmentFromFileAsDLL();

        if (treatmentList.sizeOf() > 0) {

            medicalTreatmentUI.displayTreatmentReport(treatmentList, "Treatment Summary Report");

            System.out.println("\n=== Summary of Treatments by Medicine ===");
            ArrayList<String> countedMedicines = new ArrayList<>();

            for (int i = 0; i < treatmentList.sizeOf(); i++) {
                Treatment t = treatmentList.get(i);
                String medicine = t.getTreatmentDetails(); // Assuming Treatment has getMedicineName()

                if (!countedMedicines.contains(medicine)) {
                    int quantity = 0;

                    // Count quantity across all treatments
                    for (int j = 0; j < treatmentList.sizeOf(); j++) {
                        Treatment t2 = treatmentList.get(j);
                        if (t2.getTreatmentDetails().equalsIgnoreCase(medicine)) {
                            quantity += t2.getQuantity(); // Assuming Treatment has getQuantity()
                        }
                    }

                    System.out.println("Medicine: " + medicine + " | Total Quantity: " + quantity);
                    countedMedicines.add(medicine);
                }
            }

            // Count treatments per doctor
            System.out.println("\nTreatments by Doctor:");
            for (int i = 0; i < treatmentList.sizeOf(); i++) {
                Treatment t = treatmentList.get(i);
                String doctor = t.getDoctorName();
                int count = 0;

                // Count how many times this doctor appears in the list
                for (int j = 0; j < treatmentList.sizeOf(); j++) {
                    if (treatmentList.get(j).getDoctorName().equalsIgnoreCase(doctor)) {
                        count++;
                    }
                }

                System.out.println(doctor + ": " + count);
            }

            // Count treatments per patient
            System.out.println("\nTreatments by Patient:");
            for (int i = 0; i < treatmentList.sizeOf(); i++) {
                Treatment t = treatmentList.get(i);
                String patient = t.getPatientName();
                int count = 0;

                for (int j = 0; j < treatmentList.sizeOf(); j++) {
                    if (treatmentList.get(j).getPatientName().equalsIgnoreCase(patient)) {
                        count++;
                    }
                }

                System.out.println(patient + ": " + count);
            }

            System.out.println("\nTotal Treatments Recorded: " + treatmentList.sizeOf());
            command.pressEnterToContinue();

        } else {
            messageUI.displayInvalidMessage("No treatments found.");
            command.pressEnterToContinue();
        }
    }

    private DoubleLinkedList<Treatment> readTreatmentFromFileAsDLL() {
        return dao.readTextFile(FILE_NAME, 8, this::parseTreatmentFromParts);
    }

    private ArrayStack<Treatment> readTreatmentFromFileAsArrayStack() {
        return dao.readTextFileAsArrayStack(FILE_NAME, 8, this::parseTreatmentFromParts);
    }

    ArrayList<Treatment> readTreatmentFromFileAsArrayList() {
        return dao.readTextFileAsArrayList(FILE_NAME, 8, this::parseTreatmentFromParts);
    }

    private Treatment parseTreatmentFromParts(String[] parts) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy");
        try {
            String id = parts[0];
            String patientName = parts[1];
            String doctorName = parts[2];
            String diagnosis = parts[3];
            String treatmentDetails = parts[4];
            int quantity = Integer.parseInt(parts[5]);
            boolean paymentStatus = parts[6].equalsIgnoreCase("Pay");
            Date date = sdf.parse(parts[7]);

            Treatment treatment = new Treatment(id, patientName, doctorName, diagnosis, treatmentDetails, quantity, paymentStatus);
            treatment.setTreatmentDate(new Date());
            return treatment;
        } catch (Exception e) {
            System.err.println("Error parsing treatment record: " + e.getMessage());
            return null;
        }
    }

    public static void MedicalTreatmentRun() {
        MedicalTreatment medicalTreatment = new MedicalTreatment();
        medicalTreatment.runTreatment();
    }

}
