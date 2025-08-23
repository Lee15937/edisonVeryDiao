package control;

import DAO.Dao;
import adt.ArrayList;
import adt.ArrayStack;
import adt.DoubleLinkedList;
import boundary.MedicalTreatmentUI;
import entity.Treatment;
import entity.Patient;
import entity.Doctor;
import entity.Medicine;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.InputMismatchException;
import utility.Command;
import utility.MessageUI;

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

            // i just need to call the edison control to get the patient details and the doctor details at this part
            PatientRegistration patientManagement = new PatientRegistration();
            DoctorManagement doctorManagement = new DoctorManagement();
            // wait jiawei done the read write file update
            ArrayList<Patient> Patients = patientManagement.readPatientFromFileAsArrayList();
            ArrayList<Doctor> Doctors = doctorManagement.readDoctorFromFileAsArrayList();

            String id = generateTreatmentId();
            System.out.println("Diagnosis ID: " + id);

            Treatment treatment = medicalTreatmentUI.gatherTreatmentDetails();

            if (treatment != null) {
                String patientName = treatment.getPatientName();
                String doctorName = treatment.getDoctorName();

                boolean patientExists = false;

                for (Patient patient : Patients) {
                    if (patient.getName().equals(patientName)) {
                        patientExists = true;
                        break;
                    }
                }

                boolean doctorExists = false;

                for (Doctor doctor : Doctors) {
                    if (doctor.getName().equals(doctorName)) {
                        doctorExists = true;
                        break;
                    }
                }

                if (!patientExists || !doctorExists) {
                    messageUI.displayInvalidMessage("Error: Patient or Doctor Name does not exist. Please provide a valid Name.");
                    command.pressEnterToContinue();
                    return;
                }

                treatment.setTreatmentId(id);

                DoubleLinkedList<Treatment> treatments = readTreatmentFromFileAsDLL();
                treatments.add(treatment);

                dao.saveToFile(treatments, FILE_NAME);

                messageUI.displayValidMessage("Diagnosis added successfully with ID: " + id);
                command.pressEnterToContinue();
            }
        } catch (Exception e) {
            messageUI.displayInvalidMessage("Error adding diagnosis: " + e.getMessage());
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
    
    private void updateTreatments() {
    }

    private void searchTreatments() {
    }

    private void deleteTreatments() {
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
    }

    private void filterTreatmentByPatient() {
    }

    
    private void filterLast10Treatments() { // need stack
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

    private DoubleLinkedList<Treatment> readTreatmentFromFileAsDLL() {
        return dao.readTextFile(FILE_NAME, 6, this::parseTreatmentFromParts);
    }

    private ArrayStack<Treatment> readTreatmentFromFileAsArrayStack() {
        return dao.readTextFileAsArrayStack(FILE_NAME, 6, this::parseTreatmentFromParts);
    }

    ArrayList<Treatment> readTreatmentFromFileAsArrayList() {
        return dao.readTextFileAsArrayList(FILE_NAME, 6, this::parseTreatmentFromParts);
    }

    private Treatment parseTreatmentFromParts(String[] parts) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy");
        try {
            String id = parts[0];
            String patientName = parts[1];
            String doctorName = parts[2];
            String diagnosis = parts[3];
            String treatmentDetails = parts[4];
            Date date = sdf.parse(parts[5]);

            Treatment treatment = new Treatment(id, patientName, doctorName, diagnosis, treatmentDetails);
            treatment.setTreatmentDate(new Date());
            return treatment;
        } catch (Exception e) {
            System.err.println("Error parsing doctor record: " + e.getMessage());
            return null;
        }
    }

    public static void MedicalTreatmentRun() {
        MedicalTreatment medicalTreatment = new MedicalTreatment();
        medicalTreatment.runTreatment();
    }

    

}
