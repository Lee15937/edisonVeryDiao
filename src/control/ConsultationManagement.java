package Control;

import adt.SortedLinkedList;
import boundary.ConsultationUI;
import java.util.Scanner;
import entity.Consultation;
import utility.Utils;

public class ConsultationManagement {

    ConsultationUI ui = new ConsultationUI();
    private Scanner scanner = new Scanner(System.in);

    // Consultation-specific list (uses sorted list for Consultation)
    private SortedLinkedList<Consultation> consultationList = new SortedLinkedList<>();

    public void addConsultation() {
        System.out.print("Enter patient name: ");
        String patientName = scanner.nextLine();

        System.out.print("Enter doctor name: ");
        String doctorName = scanner.nextLine();

        // Delegates to validation helpers
        String date = Utils.getValidatedDate();
        String time = Utils.getValidatedTime();

        Consultation newConsultation = new Consultation(patientName, doctorName, date, time);

        //Conflict checking before adding
        for (Consultation c : consultationList) {
            if (newConsultation.conflictsWith(c)) {
                System.out.println("Cannot schedule. Conflict with: \n");
                ui.displayConsultationHeader();
                System.out.println(c);
                return; // exit without adding
            }
        }

        boolean added = consultationList.add(newConsultation);
        if (added) {
            System.out.println("Consultation appointment added successfully!");
        } else {
            System.out.println("Failed to add consultation (unexpected error).");
        }
    }

    public void listConsultations() {
        if (consultationList.isEmpty()) {
            System.out.println("No consultations found.");
        } else {
            Utils.printCenteredTitle("ALL CONSULTATIONS", 85);
            consultationList.display();
        }
    }

    public void listAwaitingAppointment() {
        if (consultationList.isEmpty()) {
            System.out.println("No consultations found.");
        } else {
            Utils.printCenteredTitle("SCHEDULED CONSULTATIONS", 85);

            ui.displayConsultationHeader();
            for (Consultation c : consultationList) {  // works if consultationList is Iterable
                if (c.getStatus() == Consultation.Status.SCHEDULED) {
                    System.out.println(c);  // or use your custom display method
                }
            }

        }
    }

    public void cancelAppointment() {
        // Step 1: Check if there are scheduled consultations
        boolean hasScheduled = false;
        for (Consultation c : consultationList) {
            if (c.getStatus() == Consultation.Status.SCHEDULED) {
                hasScheduled = true;
                break;
            }
        }

        if (!hasScheduled) {
            System.out.println("No scheduled consultations available to cancel.");
            return; // go back to main menu
        }

        // Step 2: Show all scheduled consultations
        listAwaitingAppointment();

        // Step 3: Ask for ID
        int id = ui.getConsultationIDInput();
        if (id == 0) { // if user typed 0 or cancelled
            System.out.println("Cancellation aborted by user.");
            return;
        }

        // Step 4: Try to cancel
        boolean success = cancelSearchedAppointment(id);

        // Step 5: Display result
        if (success) {
            System.out.println("Consultation " + id + " has been cancelled and removed.");
        } else {
            System.out.println("Consultation " + id + " not found or not eligible for cancellation.");
        }
    }

    // Cancel consultation by ID (remove from linked list)
    public boolean cancelSearchedAppointment(int consultationId) {
        for (Consultation c : consultationList) {
            if (c.getConsultationID() == consultationId
                    && c.getStatus() == Consultation.Status.SCHEDULED) {
                // Call SortedLinkedList's cancel() to remove the node
                return consultationList.cancel(c);
            }
        }
        return false; // Not found
    }

    public void updateConsultation() {
        boolean exit = false;
        while (!exit) {
            ui.UpdateConsultationAppointmentMenu();
            int choice = ui.getChoice();
            switch (choice) {
                case 1:

                    break;
                case 2:
                    appointmentCheckin();
                    break;
                case 3:
                    appointmentComplete();
                    break;
                case 4:
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid choice, please try again.");
            }
        }
    }

    public boolean appointmentCheckin() {
        int id = ui.getConsultationIDInput();
        if (id == 0) {
            System.out.println("Update cancelled by user.");
            return false;
        }

        boolean success = updateStatusToCheckIn(id);
        if (success) {
            System.out.println("Consultation " + id + " status updated to CHECKED_IN.");
        } else {
            System.out.println("Consultation " + id + " not found or not eligible.");
        }
        return success;
    }

    public boolean appointmentComplete() {
        int id = ui.getConsultationIDInput();
        if (id == 0) {
            System.out.println("Update cancelled by user.");
            return false;
        }

        boolean success = updateStatusToCompleted(id);
        if (success) {
            System.out.println("Consultation " + id + " status updated to COMPLETED.");
        } else {
            System.out.println("Consultation " + id + " not found or not eligible.");
        }
        return success;
    }

// Update status to CHECKED_IN
    public boolean updateStatusToCheckIn(int consultationID) {
        for (Consultation c : consultationList) {   // ✅ use the list's iterator
            if (c.getConsultationID() == consultationID) {
                if (c.getStatus() == Consultation.Status.SCHEDULED) {
                    c.setStatus(Consultation.Status.CHECKED_IN);
                    return true;
                } else {
                    System.out.println("Consultation is not in SCHEDULED state.");
                    return false;
                }
            }
        }
        return false; // Not found
    }

// Update status to COMPLETED
    public boolean updateStatusToCompleted(int consultationID) {
        for (Consultation c : consultationList) {   // ✅ use the list's iterator
            if (c.getConsultationID() == consultationID) {
                if (c.getStatus() == Consultation.Status.CHECKED_IN) {
                    c.setStatus(Consultation.Status.COMPLETED);
                    return true;
                } else {
                    System.out.println("Consultation is not in CHECKED_IN state.");
                    return false;
                }
            }
        }
        return false; // Not found
    }

    public void searchAppointment() {
        boolean exit = false;
        ui.displaySearchConsultationMenu();
        int choice = ui.getChoice();
        while (!exit) {
            switch (choice) {
                case 1:
                    searchByDateTime();
                    break;
                case 2:
                    searchByPatient();
                    break;
                case 3:
                    searchByDoctor();
                    break;
                case 4:
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid choice, please try again.");
            }
        }
    }

    private void searchByDateTime() {
        String date = Utils.getValidatedDateFormat();
        String time = Utils.getValidatedTime();

        boolean found = false;
        ui.displayConsultationHeader();
        for (Consultation c : consultationList) {
            if (c.getDate().equals(date) && c.getTime().equals(time)) {
                System.out.println(c);
                found = true;
            }
        }
        if (!found) {
            System.out.println("No consultation found for the given date and time.");
        }
    }

    private void searchByPatient() {
        String patientName = ui.getPatientInput();

        boolean found = false;
        ui.displayConsultationHeader();
        for (Consultation c : consultationList) {
            if (c.getPatientName().equalsIgnoreCase(patientName)) {
                System.out.println(c);
                found = true;
            }
        }
        if (!found) {
            System.out.println("No consultations found for patient: " + patientName);
        }
    }

    private void searchByDoctor() {
        String doctorName = ui.getDoctorInput();

        boolean found = false;
        ui.displayConsultationHeader();
        for (Consultation c : consultationList) {
            if (c.getDoctorName().equalsIgnoreCase(doctorName)) {
                System.out.println(c);
                found = true;
            }
        }
        if (!found) {
            System.out.println("No consultations found for doctor: " + doctorName);
        }
    }

    public void generateReport() {
        // TODO: implement
    }

    public static void main(String[] args) {
        ConsultationUI ui = new ConsultationUI();
        ConsultationManagement cm = new ConsultationManagement();

        boolean exit = false;
        while (!exit) {
            ui.displayConsultationMenu();
            int choice = ui.getChoice();
            switch (choice) {
                case 1:
                    cm.addConsultation();
                    break;
                case 2:
                    cm.updateConsultation();
                    break;
                case 3:
                    cm.cancelAppointment();
                    break;
                case 4:
                    cm.searchAppointment();
                    break;
                case 5:
                    cm.listAwaitingAppointment();
                    break;
                case 6:
                    cm.listConsultations();
                    break;
                case 7:
                    cm.generateReport();
                    break;
                case 8:
                    System.out.println("Exiting... Goodbye!");
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid choice, please try again.");
            }
            System.out.println();
        }
    }
}
