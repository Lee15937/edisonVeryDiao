package control;

import entity.Patient;
import DAO.Dao;
import java.util.function.Function;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;
import adt.*;

public class PatientRegistration {

    QueueInterface<Patient> patientQueue = new patientQueue<>();
    ListInterface<Patient> patientList = new ArrayList<>();

    private Dao<Patient> dao = new Dao<>();

    public static final String PATIENT_FILE = "src/DAO/patient.txt";

    public QueueInterface<Patient> getPatientQueue() {
        return patientQueue;
    }

    public ListInterface<Patient> getPatientList() {
        return patientList;
    }

    public void loadPatientsFromFile() {
        Function<String[], Patient> patientMapper = parts -> {
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
        };

        ArrayList<Patient> loadedPatients = Dao.readTextFileAsArrayList(PATIENT_FILE, 6, patientMapper);

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

    public void savePatientsToFile() {
        dao.saveToFile(patientList, PATIENT_FILE);
        System.out.println("✅ Patient data saved.");
    }
}
