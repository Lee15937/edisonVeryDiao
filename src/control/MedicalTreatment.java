package control;

import adt.ArrayList;
import adt.DoubleLinkedList;
import boundary.MedicalTreatmentUI;
import entity.Treatment;
import entity.Patient;
import entity.Doctor;
import java.util.InputMismatchException;
import utility.Command;
import utility.MessageUI;

/**
 *
 * @author kosoo
 */
public class MedicalTreatment {

    private MessageUI messageUI = new MessageUI();
    private MedicalTreatmentUI medicalTreatmentUI = new MedicalTreatmentUI();
    private Command command = new Command();
    private int diagnosisCounter = 1;

    // Store all diagnoses in memory
    private DoubleLinkedList<Treatment> diagnoses = new DoubleLinkedList<>();

    // Store temporary/dynamic patients and doctors in memory (for now)
    private ArrayList<Patient> patients = new ArrayList<>();
    private ArrayList<Doctor> doctors = new ArrayList<>();

    public MedicalTreatment() {
        doctors.add(new Doctor("D001", "Dr. Wong", "017-6085158", "wong@gmail.com", "Mon-Fri", true));
        doctors.add(new Doctor("D002", "Dr. Lim", "01-6085158", "wong@gmail.com", "Mon-Fri", true));
    }

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
                        // update
                        break;
                    case 3:
                        // delete
                        break;
                    case 4:
                        // search
                        break;
                    case 5:
                        listTreatment();
                        break;
                    case 6:
                        filterTreatment();
                        break;
                    case 7:
                        // report
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
            // 1. Generate ID
            String id = generateDiagnosisId();
            System.out.println("Diagnosis ID: " + id);

            // 2. Ask UI to gather treatment details
            Treatment treatment = medicalTreatmentUI.gatherTreatmentDetails();

            if (treatment != null) {
                String patientId = treatment.getPatientId();
                String doctorId = treatment.getDoctorId();

                // 3. Validate Patient
                boolean patientExists = false;
                for (int i = 0; i < patients.sizeOf(); i++) {
                    if (patients.get(i).getPatientId().equals(patientId)) {
                        patientExists = true;
                        break;
                    }
                }

                // 4. Validate Doctor
                boolean doctorExists = false;
                for (int i = 0; i < doctors.sizeOf(); i++) {
                    if (doctors.get(i).getDoctorId().equals(doctorId)) {
                        doctorExists = true;
                        break;
                    }
                }

                if (!patientExists || !doctorExists) {
                    messageUI.displayInvalidMessage("Error: Patient or Doctor ID does not exist. Please provide a valid ID.");
                    command.pressEnterToContinue();
                    return;
                }

                // 5. Assign treatment ID
                treatment.setTreatmentId(id);

                // 6. Save into memory (DoubleLinkedList)
                diagnoses.add(treatment);

                messageUI.displayValidMessage("Diagnosis added successfully with ID: " + id);
                command.pressEnterToContinue();
            }
        } catch (Exception e) {
            messageUI.displayInvalidMessage("Error adding diagnosis: " + e.getMessage());
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
                listTreatment();  // Recursive call to prompt the user again
        }
    }

    public void listTreatmentByAscending() {
        if (diagnoses.sizeOf() > 0) {
            medicalTreatmentUI.displayTreatmentReport(diagnoses, "All Treatments by Ascending Order");
        } else {
            messageUI.displayInvalidMessage("No treatments available to display.");
        }
        command.pressEnterToContinue();
    }
    
    public void listTreatmentByDescending() { // id by decending
        ArrayList<Treatment> descendingList = new ArrayList<>(); // for decending also array stack no list

        for (int i = diagnoses.sizeOf(); i > 0; i--) {
            descendingList.add(diagnoses.get(i));
        }

        if (!descendingList.isEmpty()) {
            medicalTreatmentUI.displayTreatmentReport(descendingList, "All Treatments by Descending Order");
            command.pressEnterToContinue();
        } else {
            messageUI.displayInvalidMessage("No treatments available to display.");
            command.pressEnterToContinue();
        }
    }

    public void filterTreatment() {
        String choice = medicalTreatmentUI.getFilterChoice();
        String id = null;

        switch (choice) {
            case "1":
                id = medicalTreatmentUI.getUserInput("Enter Doctor ID (or 'X' to exit): ",
                        "Error: Doctor ID cannot be blank.");
                if (id == null) {
                    return;
                }
                break;
            case "2":
                id = medicalTreatmentUI.getUserInput("Enter Patient ID (or 'X' to exit): ",
                        "Error: Patient ID cannot be blank.");
                if (id == null) {
                    return;
                }
                break;
            case "4": // Exit
                return;
            default:
                messageUI.displayInvalidMessage("Invalid filter choice.");
                command.pressEnterToContinue();
                return;
        }

        DoubleLinkedList<Treatment> filteredList = new DoubleLinkedList<>();

        for (int i = 0; i < diagnoses.sizeOf(); i++) {
            Treatment t = diagnoses.get(i);
            if (choice.equals("1") && t.getDoctorId().equalsIgnoreCase(id)) {
                filteredList.add(t);
            } else if (choice.equals("2") && t.getPatientId().equalsIgnoreCase(id)) {
                filteredList.add(t);
            }
        }

        // Display result
        if (filteredList.sizeOf() > 0) {
            medicalTreatmentUI.displayTreatmentReport(filteredList,
                    "Filtered Treatments by " + (choice.equals("1") ? "Doctor ID" : "Patient ID"));
        } else {
            messageUI.displayInvalidMessage("No treatments found for given ID.");
        }

        command.pressEnterToContinue();
    }

    private void filterLast10Treatments() { // need stack
        // Temporary list to hold last 10 treatments
        ArrayList<Treatment> last10Treatments = new ArrayList<>();

        // Count treatments in memory
        int total = diagnoses.sizeOf();
        int count = 0;

        // Traverse from the end (latest treatment)
        for (int i = total; i > 0 && count < 10; i--) {
            Treatment treatment = diagnoses.get(i); // assuming your DLL supports index access
            last10Treatments.add(treatment);
            count++;
        }

        if (!last10Treatments.isEmpty()) {
            medicalTreatmentUI.displayTreatmentReport(last10Treatments, "Last 10 Treatment Records");
            command.pressEnterToContinue();
        } else {
            messageUI.displayInvalidMessage("No treatments found.");
            command.pressEnterToContinue();
        }
    }

    private String generateDiagnosisId() {
        return "T" + String.format("%03d", diagnosisCounter++);
    }

    public static void MedicalTreatmentRun() {
        MedicalTreatment medicalTreatment = new MedicalTreatment();
        medicalTreatment.runTreatment();
    }

}
