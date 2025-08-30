package control;

import entity.Patient;
import DAO.Dao;
import boundary.PatientRegistrationForm;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;
import adt.*;

public class PatientRegistration {
    QueueInterface<Patient> patientQueue = new patientQueue<>();
    ListInterface<Patient> patientList = new ArrayList<>();
    private Dao<Patient> dao = new Dao<>();
    private PatientRegistrationForm form = new PatientRegistrationForm();
    public static final String PATIENT_FILE = "src/DAO/patient.txt";

    public QueueInterface<Patient> getPatientQueue() {
        return patientQueue;
    }

    public ListInterface<Patient> getPatientList() {
        return patientList;
    }

    public void loadPatientsFromFile() {
        ArrayList<Patient> loadedPatients = dao.readTextFileAsArrayList(PATIENT_FILE, 6, this::patientMapper);
        for (int i = 0; i < loadedPatients.sizeOf(); i++) {
            Patient patient = loadedPatients.get(i);
            if (patient != null) {
                patientList.add(patient);
                patientQueue.enqueue(patient);
            }
        }
        System.out.println("✅ Loaded " + patientList.getNumberOfEntries() + " patients.");
        if (patientList.getNumberOfEntries() > 0) {
            Patient lastPatient = patientList.getEntry(patientList.getNumberOfEntries());
            String lastId = lastPatient.getPatientId();
            int lastNum = Integer.parseInt(lastId.substring(1));
            Patient.setCounter(lastNum + 1);
        }
    }

    public Patient patientMapper(String[] parts) {
        try {
            String patientId = parts[0].trim();
            String ic = parts[1].trim();
            String name = parts[2].trim();
            String gender = parts[3].trim().toUpperCase();
            int age = Integer.parseInt(parts[4].trim());
            String phoneNo = parts[5].trim();
            Patient patient = new Patient(patientId, ic, name, gender, age, phoneNo);
            if (parts.length > 6 && !parts[6].trim().isEmpty()) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy", Locale.ENGLISH);
                    Date date = sdf.parse(parts[6].trim());
                    patient.setDate(date);
                } catch (ParseException e) {
                    patient.setDate(new Date());
                }
            } else {
                patient.setDate(new Date());
            }
            return patient;
        } catch (Exception e) {
            System.out.println("Error parsing patient record: " + String.join("#", parts));
            return null;
        }
    }

    public ArrayList<Patient> readPatientFromFileAsArrayList() {
        return dao.readTextFileAsArrayList(PATIENT_FILE, 6, this::patientMapper);
    }

    public void savePatientsToFile() {
        dao.saveToFile(patientList, PATIENT_FILE);
        System.out.println("✅ Patient data saved.");
    }

    public void runPatientManagement() {
        loadPatientsFromFile();
        int patientChoice;

        do {
            patientChoice = form.getPatientChoice();
            switch (patientChoice) {
                case 1:
                    registerPatient();
                    break;
                case 2:
                    viewPatientQueue();
                    break;
                case 3:
                    servePatient();
                    break;
                case 4:
                    viewPatientList();
                    break;
                case 5:
                    modifyPatient();
                    break;
                case 6:
                    showPatientReports();
                    break;
                case 7:
                    form.displayMessage("Returning to Main Menu...");
                    break;
                default:
                    form.displayErrorMessage("Invalid choice, please enter 1â€“7.");
            }
        } while (patientChoice != 7);
    }

    private void registerPatient() {
        String name = form.inputPatientName();
        int age = form.inputPatientAge();
        String gender = form.inputPatientGender();
        String ic = form.inputPatientIC();
        String phoneNo = form.inputPatientPhoneNo();
        
        Patient patient = registerNewPatient(name, age, gender, ic, phoneNo);
        form.printPatientDetails(patient);
        savePatientsToFile();
        form.displaySuccessMessage("Patient registered successfully!");
    }

    private void viewPatientQueue() {
        form.listPatientQueue(getPatientQueue());
    }

    private void servePatient() {
        if (!getPatientQueue().isEmpty()) {
            Patient next = serveNextPatient();
            System.out.println("Serving next patient:");
            form.printPatientDetails(next);
        } else {
            form.displayMessage("No patients in queue.");
        }
    }

    private void viewPatientList() {
        form.listPatientList(getPatientList());
    }

    private void modifyPatient() {
        String patientID = form.inputPatientID();
        Patient patientToModify = findPatientById(patientID);

        if (patientToModify != null) {
            System.out.println("\nCurrent patient details:");
            form.printPatientDetails(patientToModify);

            int modifyChoice = form.getModificationChoice();
            boolean updateSuccess = false;
            
            switch (modifyChoice) {
                case 1:
                    String newName = form.inputPatientName();
                    updateSuccess = updatePatientField(patientID, "name", newName);
                    break;
                case 2:
                    int newAge = form.inputPatientAge();
                    updateSuccess = updatePatientField(patientID, "age", newAge);
                    break;
                case 3:
                    String newGender = form.inputPatientGender();
                    updateSuccess = updatePatientField(patientID, "gender", newGender);
                    break;
                case 4:
                    String newIC = form.inputPatientIC();
                    updateSuccess = updatePatientField(patientID, "ic", newIC);
                    break;
                case 5:
                    String newPhoneNo = form.inputPatientPhoneNo();
                    updateSuccess = updatePatientField(patientID, "phoneno", newPhoneNo);
                    break;
                case 6:
                    form.displayMessage("Returning to patient menu...");
                    break;
                default:
                    form.displayErrorMessage("Invalid choice.");
                    return;
            }

            if (modifyChoice >= 1 && modifyChoice <= 5) {
                if (updateSuccess) {
                    savePatientsToFile();
                    form.displaySuccessMessage("Patient updated successfully!");
                } else {
                    form.displayErrorMessage("Failed to update patient.");
                }
            }
        } else {
            form.displayErrorMessage("Patient with ID " + patientID + " not found.");
        }
    }

    private void showPatientReports() {
        PatientReport report = generatePatientReports();
        form.displayPatientReports(report.getTotalPatients(), report.getMaleCount(), report.getFemaleCount());
    }

    public Patient serveNextPatient() {
        return patientQueue.dequeue();
    }

    public boolean updatePatientField(String patientID, String field, Object newValue) {
        Patient patient = findPatientById(patientID);
        
        if (patient == null) {
            return false;
        }

        switch (field.toLowerCase()) {
            case "name":
                patient.setName((String) newValue);
                break;
            case "age":
                patient.setAge((Integer) newValue);
                break;
            case "gender":
                patient.setGender((String) newValue);
                break;
            case "ic":
                patient.setIC((String) newValue);
                break;
            case "phoneno":
            case "phone":
                patient.setPhoneNo((String) newValue);
                break;
            default:
                return false;
        }
        return true;
    }

    public Patient findPatientById(String patientID) {
        for (int i = 1; i <= patientList.getNumberOfEntries(); i++) {
            Patient patient = patientList.getEntry(i);
            if (patient.getPatientId().equals(patientID)) {
                return patient;
            }
        }
        return null;
    }

    public PatientReport generatePatientReports() {
        int totalCount = patientList.getNumberOfEntries();
        int maleCount = 0, femaleCount = 0;
        
        for (int i = 1; i <= patientList.getNumberOfEntries(); i++) {
            Patient p = patientList.getEntry(i);
            if (p.getGender().equalsIgnoreCase("M")) {
                maleCount++;
            } else if (p.getGender().equalsIgnoreCase("F")) {
                femaleCount++;
            }
        }
        
        return new PatientReport(totalCount, maleCount, femaleCount);
    }

    public Patient registerNewPatient(String name, int age, String gender, String ic, String phoneNo) {
        Patient newPatient = new Patient("", ic, name, gender, age, phoneNo);
        patientList.add(newPatient);
        patientQueue.enqueue(newPatient);
        return newPatient;
    }

    public boolean isValidField(String field) {
        String[] validFields = {"name", "age", "gender", "ic", "phoneno", "phone"};
        for (String validField : validFields) {
            if (validField.equalsIgnoreCase(field)) {
                return true;
            }
        }
        return false;
    }

    public static class PatientReport {
        private final int totalPatients;
        private final int maleCount;
        private final int femaleCount;

        public PatientReport(int totalPatients, int maleCount, int femaleCount) {
            this.totalPatients = totalPatients;
            this.maleCount = maleCount;
            this.femaleCount = femaleCount;
        }

        public int getTotalPatients() {
            return totalPatients;
        }

        public int getMaleCount() {
            return maleCount;
        }

        public int getFemaleCount() {
            return femaleCount;
        }
    }
}