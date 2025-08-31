/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package control;

import boundary.ConsultationUI;
import java.util.Scanner;
import boundary.PharmacyUI;
import utility.Utils;

/**
 *
 * @author kosoo
 */
public class ClinicManagementDriver {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        Scanner wait = new Scanner(System.in);
        int choice;

        do {
            System.out.println("================================================================");
            System.out.println("                       Main Menu:");
            System.out.println("================================================================");
            System.out.println("1. Patient Management Subsystem");
            System.out.println("2. Doctor Management Subsystem");
            System.out.println("3. Consultation Management Subsystem");
            System.out.println("4. Medical Treatment Management Subsystem");
            System.out.println("5. Pharmacy Management Subsystem");
            System.out.println("6. Exit");
            System.out.print("Enter your choice (1-6): ");

            choice = scanner.nextInt();
            PatientRegistration patientManagement = new PatientRegistration();
            
            switch (choice) {
                case 1:               
                    patientManagement.runPatientManagement();
                    break;
                case 2:
                    DoctorManagement.DoctorManagementRun();
                    break;
                case 3:
                    ConsultationUI ui = new ConsultationUI();
                    ConsultationManagement cm = new ConsultationManagement();

                    boolean exit = false;
                    while (!exit) {
                        ui.displayConsultationMenu();
                        int choices = ui.getChoice();
                        switch (choices) {
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
                                Utils.printCenteredTitle("CLICK ENTER TO CONTINUE", 115);   
                                wait.nextLine();
                                break;
                            case 6:
                                cm.listConsultations();
                                Utils.printCenteredTitle("CLICK ENTER TO CONTINUE", 115);
                                wait.nextLine();
                                break;
                            case 7:
                                cm.generateConsultationReport();
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
                    break;
                case 4:
                    MedicalTreatment.MedicalTreatmentRun();
                    break;
                case 5:
                    // Pharmacy
                    PharmacyManagementCTRL pharmacyCTRL = new PharmacyManagementCTRL();
                    PharmacyUI pharmacyUI = new PharmacyUI(pharmacyCTRL);
                    pharmacyUI.displayMenu();
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
