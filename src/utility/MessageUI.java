package utility;

/**
 *
 * @author kosoo
 */
public class MessageUI {

    public static void displayInvalidMessage(String message) {
        printFormattedText(message + "\n", Color.RED);
    }

    public static void displayValidMessage(String message) {
        printFormattedText(message + "\n", Color.GREEN);
    }

    public static void displayInvalidChoiceMessage() {
        printFormattedText("Invalid choice. Please try again.\n", Color.RED);
    }

    public static void displayInvalidChoiceIntegerMessage() {
        printFormattedText("Invalid input. Please enter a number.\n", Color.RED);
    }

    public static void displayExitMessage() {
        printFormattedText("\nExiting system...", Color.GREEN);

    }

    public static void displayInvalidFormat() {
        printFormattedText("Your input is not in correct format: ", Color.RED);
    }

    public static void printFormattedText(String text, Color color) {
        System.out.print(color + text + Color.RESET);
    }

    public static void displayNotFoundMessage() {
        printFormattedText("The result has not found!\n", Color.YELLOW);
    }

    public static void displayFoundMessage(String val) {
        printFormattedText("The result " + val + " has found!\n", Color.GREEN);
    }

    public static void displayAskAgainMessage(String val) {
        printFormattedText("Do you want to " + val + " again?(1 is Yes and 0 is No): ", Color.BRIGHTBLUE);
    }

    public void clearScreen() {
        System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
    }
}
