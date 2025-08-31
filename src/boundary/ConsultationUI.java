
package boundary;

import java.util.Scanner;
import adt.SortedLinkedList;
import control.ConsultationManagement;
import adt.SortedLinkedListInterface;
import java.time.LocalDate;
import utility.Utils;

public class ConsultationUI {

    private static ConsultationManagement cm = new ConsultationManagement();
    private Scanner scanner = new Scanner(System.in);

    private SortedLinkedListInterface consultationList = new SortedLinkedList();

    public void displayConsultationMenu() {
        Utils.printCenteredTitle("Consultation", 40);
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
        Utils.printCenteredTitle("Search Consultation", 40);
        System.out.println("1. Search by date");
        System.out.println("2. Search by Patient");
        System.out.println("3. Search by Doctor");
        System.out.println("4. Exit");

    }

    public void UpdateConsultationAppointmentMenu() {
        Utils.printCenteredTitle("Update Consultation", 40);
        System.out.println("1. Update Consultation Date and Time");
        System.out.println("2. Change Consultation Doctor");
        System.out.println("3. Check In");
        System.out.println("4. Complete");
        System.out.println("5. Exit");

    }

    public void generateConsultationAppointmentMenu() {
        Utils.printCenteredTitle("Generate Consultation Appointment Summary Report", 50);
        System.out.println("1. Yearly Summary Report");
        System.out.println("2. Monthly Summary Report On Each Day");
        System.out.println("3. Exit");

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
        System.out.printf("%-10s %-15s %-20s %-10s %-20s %-12s %-8s %-12s%n",
                "ID", "Patient IC", "Patient Name", "Doc ID", "Doctor Name", "Date", "Time", "Status");
        Utils.printDivider(115);
    }

    public int enterYear() {
        int year;
        int currentYear = LocalDate.now().getYear();
        int maxYear = currentYear + 1; // allow 1 year ahead
        while (true) {
            System.out.print("Enter year (0 = cancel, up to " + maxYear + "): ");
            String input = scanner.nextLine();
            try {
                year = Integer.parseInt(input);
                if (year == 0) {
                    System.out.println("Cancelled. Returning to main menu...");
                    return 0; // signal cancel
                } else if (year > 1900 && year <= maxYear) {
                    return year;
                } else {
                    System.out.println("Invalid year. Please enter between 1900 and " + maxYear + ".");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a numeric year.");
            }
        }
    }

    public int enterMonth() {
        int month;
        while (true) {
            System.out.print("Enter month (1-12, 0 = cancel): ");
            String input = scanner.nextLine();
            try {
                month = Integer.parseInt(input);
                if (month == 0) {
                    System.out.println("Cancelled. Returning to main menu...");
                    return 0; // signal cancel
                } else if (month >= 1 && month <= 12) {
                    return month;
                } else {
                    System.out.println("Invalid month. Please enter 1Ã¢â‚¬â€œ12.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a numeric month.");
            }
        }
    }
}
