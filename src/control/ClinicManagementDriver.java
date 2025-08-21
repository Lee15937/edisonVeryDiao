/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package control;

import java.util.Scanner;

/**
 *
 * @author kosoo
 */
public class ClinicManagementDriver {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        int choice;

        do {
            System.out.println("Main Menu:");
            System.out.println("1. Patient Management Subsystem");
            System.out.println("2. Doctor Management Subsystem");
            System.out.println("3. Consultation Management Subsystem");
            System.out.println("4. Medical Treatment Management Subsystem");
            System.out.println("5. Pharmacy Management Subsystem");
            System.out.println("6. Exit");
            System.out.print("Enter your choice (1-6): ");

            choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    //PatientManagement.PatientRun();
                    break;
                case 2:
                    //DoctorManagement.DoctorRun();
                    break;
                case 3:
                    break;
                case 4:
                    MedicalTreatment.MedicalTreatmentRun();
                    break;
                case 5:
                    break;
                case 6:
                    System.out.println("Exiting...");
                    break;
                default:
                    System.out.println("Invalid choice, please enter a number between 1 and 6.");
                    break;
            }

        } while (choice != 6);

        scanner.close();
    }
}
