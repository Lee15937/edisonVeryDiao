/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utility;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.util.Scanner;

/**
 *
 * @author kosoo
 */
public class Command {

    private static Scanner scanner = new Scanner(System.in);

    
    public static void pressEnterToContinue() {
        System.out.println("Press Enter key to continue...");
        scanner.nextLine(); // Waits for the user to press Enter
    }

    /**
     * Reads an integer from the user with validation. Keeps asking until a
     * valid integer is entered.
     */
    public static int readInt(String message) {
        int value;
        while (true) {
            System.out.print(message);
            try {
                value = Integer.parseInt(scanner.nextLine().trim());
                break; // exit loop if valid
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid integer.");
            }
        }
        return value;
    }

    /**
     * Reads an integer within a range (min to max).
     */
    public static int readInt(String message, int min, int max) {
        int value;
        while (true) {
            value = readInt(message);
            if (value >= min && value <= max) {
                break;
            } else {
                System.out.println("Input must be between " + min + " and " + max + ".");
            }
        }
        return value;
    }

    public static void cls() {
        try {
            Robot robot = new Robot();
            robot.setAutoDelay(10);
            robot.keyPress(KeyEvent.VK_CONTROL);
            robot.keyPress(KeyEvent.VK_L);
            robot.keyRelease(KeyEvent.VK_CONTROL);
            robot.keyRelease(KeyEvent.VK_L);
        } catch (AWTException ex) {
        }
    }
}
