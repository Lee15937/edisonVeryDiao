package boundary;

import java.util.Scanner;
import adt.SortedLinkedList;
import control.ConsultationManagement;
import adt.SortedLinkedListInterface;

public class ConsultationUI {

    private static ConsultationManagement cm = new ConsultationManagement();
    private Scanner scanner = new Scanner(System.in);

    private SortedLinkedListInterface consultationList = new SortedLinkedList();

    public void displayConsultationMenu() {
        System.out.println("============================================");
        System.out.println("              Consultation                  ");
        System.out.println("============================================");
        System.out.println("1. Add New Consultation Appointment");
        System.out.println("2. Update Consultation Appointment");
        System.out.println("3. Cancel Consultation");
        System.out.println("4. Search Consultation Appointment");
        System.out.println("5. List Awaiting Consultation Appointment");
        System.out.println("6. List All Consultation");
        System.out.println("7. Generate Reports");
        System.out.println("8. Exit");

    }

    public int getChoice() {
        System.out.print("Choose an option: ");
        while (!scanner.hasNextInt()) {
            System.out.println("Invalid input. Please enter a number.");
            scanner.nextLine();
            System.out.print("Choose an option: ");
        }
        int choice = scanner.nextInt();
        scanner.nextLine();
        return choice;
    }

    public void displaySearchConsultationMenu() {
        System.out.println("============================================");
        System.out.println("           Search Consultation              ");
        System.out.println("============================================");
        System.out.println("1. Search by date and time");
        System.out.println("2. Search by Patient");
        System.out.println("3. Search by Doctor");
        System.out.println("4. Exit");

    }

    public void UpdateConsultationAppointmentMenu() {
        System.out.println("============================================");
        System.out.println("           Update Consultation              ");
        System.out.println("============================================");
        System.out.println("1. Update Consultation Date and Time");
        System.out.println("2. Check In");
        System.out.println("3. Complete");
        System.out.println("4. Exit");

    }

    public String getConsultationIDInput() {
        System.out.print("Enter Consultation ID (Enter 0 to cancel): ");
        
        return new Scanner(System.in).nextLine().trim();

    }

    public String getPatientInput() {
        System.out.print("Enter patient name: ");
        return scanner.nextLine();
    }
    
    public String getDoctorInput() {
        System.out.print("Enter doctor name: ");
        return scanner.nextLine();
    }
    
    public String getDateInput() {
        System.out.print("Enter date (YYYY-MM-DD): ");
        return scanner.nextLine();
    }

    public String getTimeInput() {
        System.out.print("Enter time (HH:MM): ");
        return scanner.nextLine();
    }

    public int getConsultationIDInputforCancel() {
        System.out.print("\nEnter Consultation ID (Enter 0 to cancel): ");
        int id = scanner.nextInt();

        return id;
    }

    public void displayConsultationHeader() {
        System.out.printf("%-10s %-12s %-8s %-20s %-20s %-15s%n",
                "ID", "Date", "Time", "Patient", "Doctor", "Status");
        System.out.println("-------------------------------------------------------------------------------------------");
    }
}
