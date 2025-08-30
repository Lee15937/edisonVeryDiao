package utility;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class Utils {

    private static final Scanner scanner = new Scanner(System.in);

    public static void printCenteredTitle(String title, int width) {
        int padding = (width - title.length()) / 2;
        String line = "=".repeat(width);
        System.out.println("\n" + line);
        System.out.printf("%" + (padding + title.length()) + "s%n", title);
        System.out.println(line);
    }

    public static String getValidatedDate() {
        String date;
        do {
            System.out.print("Enter date (YYYY-MM-DD): ");
            date = scanner.nextLine();
            if (!isValidDate(date)) {
                System.out.println("Invalid date! Please enter today or a future date in format YYYY-MM-DD.");
            }
        } while (!isValidDate(date));
        return date;
    }

    public static boolean isValidDate(String date) {
        try {
            LocalDate enteredDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            LocalDate today = LocalDate.now();
            return !enteredDate.isBefore(today);
        } catch (DateTimeParseException e) {
            return false;
        }
    }
    
    // Get validated date from user, allow 0 to cancel
    public static String getValidatedDateFormat() {
        String date;
        while (true) {
            System.out.print("Enter date (YYYY-MM-DD) or 0 to cancel: ");
            date = scanner.nextLine().trim();

            if (date.equals("0")) {
                return null; // user canceled
            }

            if (isValidDate(date)) {
                return date; // valid date
            } else {
                System.out.println("Invalid date! Please enter today or a future date in format YYYY-MM-DD.");
            }
        }
    }

    // Get validated time from user, allow 0 to cancel
    public static String getValidatedTime() {
        String time;
        while (true) {
            System.out.print("Enter time (HH:MM) or 0 to cancel: ");
            time = scanner.nextLine().trim();

            if (time.equals("0")) {
                return null; // user canceled
            }

            if (isValidTime(time)) {
                return time;
            } else {
                System.out.println("Invalid time! Please enter in HH:MM format (24-hour).");
            }
        }
    }

    public static boolean isValidTime(String time) {
        try {
            LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm"));
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public static void printDivider(int width) {
        System.out.println("-".repeat(width));
    }
}
