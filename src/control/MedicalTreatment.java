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
import utility.Color;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.InputMismatchException;

/**
 *
 * @author Ko Soon Lee
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
                            + " | Patient IC: " + c.getPatientIC()
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
            
            // convert the consultation String format date time to my Date treatmentDate
            String dateTimeStr = selectedConsultation.getDate() + " " + selectedConsultation.getTime();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date treatmentDate = sdf.parse(dateTimeStr);
            
            treatment.setPatientIC(selectedConsultation.getPatientIC());
            treatment.setPatientName(selectedConsultation.getPatientName());
            treatment.setDoctorName(selectedConsultation.getDoctorName());
            treatment.setTreatmentDate(treatmentDate);
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
                        String newDiagnosis = medicalTreatmentUI.getUserInput(
                                "Enter Diagnosis Description (or 'X' to exit): ",
                                "Error: Diagnosis Description cannot be blank."
                        );

                        if (newDiagnosis == null || newDiagnosis.equalsIgnoreCase("X")) {
                            messageUI.displayInvalidMessage("Operation canceled by user.");
                            command.pressEnterToContinue();
                            return; // keep old value
                        }

                        treatmentToUpdate.setDiagnosis(newDiagnosis);
                        break;

                    case "2":
                        String newTreatment = medicalTreatmentUI.getUserInput(
                                "Enter Treatment Details (or 'X' to exit): ",
                                "Error: Treatment Details cannot be blank."
                        );

                        if (newTreatment == null || newTreatment.equalsIgnoreCase("X")) {
                            messageUI.displayInvalidMessage("Operation canceled by user.");
                            command.pressEnterToContinue();
                            return;
                        }

                        treatmentToUpdate.setTreatmentPlan(newTreatment);
                        break;

                    case "3":
                        String newQuantity = medicalTreatmentUI.getUserInputWithRegex(
                                "Enter Quantity (or 'X' to exit): ",
                                "Error: Please enter a valid quantity (numeric).",
                                "\\d+"
                        );

                        if (newQuantity == null || newQuantity.equalsIgnoreCase("X")) {
                            messageUI.displayInvalidMessage("Operation canceled by user.");
                            command.pressEnterToContinue();
                            return;
                        }

                        treatmentToUpdate.setQuantity(Integer.parseInt(newQuantity));
                        break;

                    case "4":
                        String newDateStr = medicalTreatmentUI.getUpdatedValue(
                                "Follow-Up Date (yyyy-MM-dd HH:mm) or 'X' to exit: "
                        );

                        if (newDateStr == null || newDateStr.equalsIgnoreCase("X")) {
                            messageUI.displayInvalidMessage("Operation canceled by user.");
                            command.pressEnterToContinue();
                            return;
                        }

                        try {
                            Date newDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(newDateStr);
                            treatmentToUpdate.setTreatmentDate(newDate);
                        } catch (Exception e) {
                            messageUI.displayInvalidMessage(
                                    "Invalid date/time format. Please use yyyy-MM-dd HH:mm (e.g., 2025-08-29 14:30)."
                            );
                            command.pressEnterToContinue();
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

    // arrayList for seach
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

    // arrayStack for descending the record
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
        DoubleLinkedList<Treatment> treatmentList = readTreatmentFromFileAsDLL();

        if (treatmentList.sizeOf() == 0) {
            messageUI.displayInvalidMessage("No treatments found.");
            command.pressEnterToContinue();
            return;
        }

        //Collect unique doctor names
        ArrayList<String> doctorNames = new ArrayList<>();
        for (int i = 0; i < treatmentList.sizeOf(); i++) {
            Treatment t = treatmentList.get(i);
            if (!doctorNames.contains(t.getDoctorName())) {
                doctorNames.add(t.getDoctorName());
            }
        }

        if (doctorNames.isEmpty()) {
            messageUI.displayInvalidMessage("No doctor names found in treatments.");
            command.pressEnterToContinue();
            return;
        }

        // Show doctor list to user
        String input = null;
        int choice = -1;
        while (true) {
            System.out.println("\nSelect Doctor to filter treatments:");
            for (int i = 0; i < doctorNames.sizeOf(); i++) {
                System.out.println((i + 1) + ". " + doctorNames.get(i));
            }

            input = medicalTreatmentUI.getUserInput(
                    "Enter choice (1-" + doctorNames.sizeOf() + " or 'X' to exit): ",
                    "Error: Choice cannot be blank."
            );

            if (input == null || input.equalsIgnoreCase("X")) {
                messageUI.displayValidMessage("Operation canceled by user.");
                command.pressEnterToContinue();
                return;
            }

            try {
                choice = Integer.parseInt(input);
                if (choice >= 1 && choice <= doctorNames.sizeOf()) {
                    break; // valid choice
                } else {
                    messageUI.displayInvalidMessage("Invalid choice. Please try again.");
                }
            } catch (NumberFormatException e) {
                messageUI.displayInvalidMessage("Invalid input. Please enter a number or 'X' to exit.");
            }
        }

        String selectedDoctor = doctorNames.get(choice - 1);

        // Filter treatments for that doctor
        DoubleLinkedList<Treatment> filteredList = (DoubleLinkedList<Treatment>) treatmentList.where(treatment
                -> treatment.getDoctorName().equalsIgnoreCase(selectedDoctor)
        );

        if (filteredList.sizeOf() > 0) {
            medicalTreatmentUI.displayTreatmentReport(filteredList, "Treatment Report for Doctor: " + selectedDoctor);
        } else {
            messageUI.displayInvalidMessage("No treatments found for Doctor: " + selectedDoctor);
        }
        command.pressEnterToContinue();
    }

    private void filterTreatmentByPatient() {
        DoubleLinkedList<Treatment> treatmentList = readTreatmentFromFileAsDLL();

        if (treatmentList.sizeOf() == 0) {
            messageUI.displayInvalidMessage("No treatments found.");
            command.pressEnterToContinue();
            return;
        }

        // Collect unique patients (IC + Name)
        ArrayList<String> patientICs = new ArrayList<>();
        ArrayList<String> patientNames = new ArrayList<>();

        for (int i = 0; i < treatmentList.sizeOf(); i++) {
            Treatment t = treatmentList.get(i);
            if (!patientICs.contains(t.getPatientIC())) {
                patientICs.add(t.getPatientIC());
                patientNames.add(t.getPatientName());
            }
        }

        if (patientICs.isEmpty()) {
            messageUI.displayInvalidMessage("No patient IC found in treatments.");
            command.pressEnterToContinue();
            return;
        }

        // Show patient list to user
        String input = null;
        int choice = -1;
        while (true) {
            System.out.println("\nSelect Patient to filter treatments:");
            for (int i = 0; i < patientICs.sizeOf(); i++) {
                System.out.println((i + 1) + ". " + patientICs.get(i) + " | " + patientNames.get(i));
            }

            input = medicalTreatmentUI.getUserInput(
                    "Enter choice (1-" + patientICs.sizeOf() + " or 'X' to exit): ",
                    "Error: Choice cannot be blank."
            );

            if (input == null || input.equalsIgnoreCase("X")) {
                messageUI.displayValidMessage("Operation canceled by user.");
                command.pressEnterToContinue();
                return;
            }

            try {
                choice = Integer.parseInt(input);
                if (choice >= 1 && choice <= patientICs.sizeOf()) {
                    break; // valid choice
                } else {
                    messageUI.displayInvalidMessage("Invalid choice. Please try again.");
                }
            } catch (NumberFormatException e) {
                messageUI.displayInvalidMessage("Invalid input. Please enter a number or 'X' to exit.");
            }
        }

        String selectedPatientIC = patientICs.get(choice - 1);
        String selectedPatientName = patientNames.get(choice - 1);

        // Filter treatments for that patient
        DoubleLinkedList<Treatment> filteredList = (DoubleLinkedList<Treatment>) treatmentList.where(treatment
                -> treatment.getPatientIC().equalsIgnoreCase(selectedPatientIC));

        if (filteredList.sizeOf() > 0) {
            medicalTreatmentUI.displayTreatmentReport(
                    filteredList,
                    "Treatment Report for Patient: " + selectedPatientName + " (IC: " + selectedPatientIC + ")");
        } else {
            messageUI.displayInvalidMessage("No treatments found for Patient: " + selectedPatientIC);
        }
        command.pressEnterToContinue();

    }

    // arrayStack for filter last 10 treatments record
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

    // updated
    private void generateSummaryReport() {
        DoubleLinkedList<Treatment> treatmentList = readTreatmentFromFileAsDLL();

        if (treatmentList.sizeOf() > 0) {

            medicalTreatmentUI.displayTreatmentReport(treatmentList, "Treatment Summary Report");

            // ===== Medicine Summary =====
            DoubleLinkedList<String> medicineNames = new DoubleLinkedList<>();
            DoubleLinkedList<Integer> medicineQuantities = new DoubleLinkedList<>();

            for (int i = 0; i < treatmentList.sizeOf(); i++) {
                Treatment t = treatmentList.get(i);
                String medicine = t.getTreatmentDetails();

                boolean found = false;
                for (int j = 0; j < medicineNames.sizeOf(); j++) {
                    if (medicineNames.get(j).equalsIgnoreCase(medicine)) {
                        medicineQuantities.set(j, medicineQuantities.get(j) + t.getQuantity());
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    medicineNames.add(medicine);
                    medicineQuantities.add(t.getQuantity());
                }
            }

            String[] medArray = new String[medicineNames.sizeOf()];
            int[] qtyArray = new int[medicineQuantities.sizeOf()];
            for (int i = 0; i < medicineNames.sizeOf(); i++) {
                medArray[i] = medicineNames.get(i);
                qtyArray[i] = medicineQuantities.get(i);
            }
            printBarChart(medArray, qtyArray, "Treatments by Medicine");

            // ===== Doctor Summary =====
            DoubleLinkedList<String> doctorNames = new DoubleLinkedList<>();
            DoubleLinkedList<Integer> doctorCounts = new DoubleLinkedList<>();

            for (int i = 0; i < treatmentList.sizeOf(); i++) {
                Treatment t = treatmentList.get(i);
                String doctor = t.getDoctorName();

                boolean found = false;
                for (int j = 0; j < doctorNames.sizeOf(); j++) {
                    if (doctorNames.get(j).equalsIgnoreCase(doctor)) {
                        doctorCounts.set(j, doctorCounts.get(j) + 1);
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    doctorNames.add(doctor);
                    doctorCounts.add(1);
                }
            }

            String[] doctorArray = new String[doctorNames.sizeOf()];
            int[] doctorValArray = new int[doctorCounts.sizeOf()];
            for (int i = 0; i < doctorNames.sizeOf(); i++) {
                doctorArray[i] = doctorNames.get(i);
                doctorValArray[i] = doctorCounts.get(i);
            }
            printBarChart(doctorArray, doctorValArray, "Treatments by Doctor");

            // ===== Patient Summary =====
            DoubleLinkedList<String> patientIdentifiers = new DoubleLinkedList<>();  // IC + Name
            DoubleLinkedList<Integer> patientCounts = new DoubleLinkedList<>();

            for (int i = 0; i < treatmentList.sizeOf(); i++) {
                Treatment t = treatmentList.get(i);

                // Combine IC + Name for display
                String patientIdentifier = t.getPatientIC() + " - " + t.getPatientName();

                boolean found = false;
                for (int j = 0; j < patientIdentifiers.sizeOf(); j++) {
                    if (patientIdentifiers.get(j).equalsIgnoreCase(patientIdentifier)) {
                        patientCounts.set(j, patientCounts.get(j) + 1);
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    patientIdentifiers.add(patientIdentifier);
                    patientCounts.add(1);
                }
            }

            String[] patientArray = new String[patientIdentifiers.sizeOf()];
            int[] patientValArray = new int[patientCounts.sizeOf()];
            for (int i = 0; i < patientIdentifiers.sizeOf(); i++) {
                patientArray[i] = patientIdentifiers.get(i);
                patientValArray[i] = patientCounts.get(i);
            }

            printBarChart(patientArray, patientValArray, "Treatments by Patient");

            System.out.println("\nTotal Treatments Recorded: " + treatmentList.sizeOf());
            command.pressEnterToContinue();

        } else {
            messageUI.displayInvalidMessage("No treatments found.");
            command.pressEnterToContinue();
        }
    }

    private void printBarChart(String[] labels, int[] values, String title) {
        System.out.println("\n=== " + title + " ===");

        // Define some colors (cycle if more labels than colors)
        Color[] colors = {
            Color.RED,
            Color.GREEN,
            Color.YELLOW,
            Color.BLUE,
            Color.CYAN,
            Color.MAGENTA,
            Color.BRIGHTBLUE,
            Color.BRIGHTMAGENTA,
            Color.BRIGHTYELLOW,
            Color.BRIGHTGREEN
        };

        // Find max value for scaling
        for (int i = 0; i < labels.length; i++) {
            int barLength = values[i];
            String bar = new String(new char[barLength]).replace("\0", "#");

            // Pick color for this bar (cycle if labels > colors)
            Color c = colors[i % colors.length];

            // Apply color to bar only
            String coloredBar = c + bar + Color.RESET;

            System.out.printf("%-15s | %-50s (%d)\n", labels[i], coloredBar, values[i]);
        }
    }

    private DoubleLinkedList<Treatment> readTreatmentFromFileAsDLL() {
        return dao.readTextFile(FILE_NAME, 9, this::parseTreatmentFromParts);
    }

    private ArrayStack<Treatment> readTreatmentFromFileAsArrayStack() {
        return dao.readTextFileAsArrayStack(FILE_NAME, 9, this::parseTreatmentFromParts);
    }

    ArrayList<Treatment> readTreatmentFromFileAsArrayList() {
        return dao.readTextFileAsArrayList(FILE_NAME, 9, this::parseTreatmentFromParts);
    }

    private Treatment parseTreatmentFromParts(String[] parts) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            String id = parts[0];
            String patientIC = parts[1];
            String patientName = parts[2];
            String doctorName = parts[3];
            String diagnosis = parts[4];
            String treatmentDetails = parts[5];
            int quantity = Integer.parseInt(parts[6]);
            boolean paymentStatus = parts[7].equalsIgnoreCase("Pay");
            Date date = sdf.parse(parts[8]);

            Treatment treatment = new Treatment(id, patientIC, patientName, doctorName, diagnosis, treatmentDetails, quantity, paymentStatus, date);
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
