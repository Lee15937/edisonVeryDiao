/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package control;

import java.util.Scanner;
import boundary.PatientRegistrationForm;
import entity.Patient;

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
                    PatientRegistration registration = new PatientRegistration();
                    PatientRegistrationForm form = new PatientRegistrationForm();

                    registration.loadPatientsFromFile();
                    int patientChoice;

                    do {
                        patientChoice = form.getPatientChoice();
                        switch (patientChoice) {
                            case 1:
                                Patient patient = form.inputPatientDetails();
                                registration.patientQueue.enqueue(patient);
                                registration.patientList.add(patient);
                                form.printPatientDetails(patient);
                                registration.savePatientsToFile();
                                break;

                            case 2:
                                form.listPatientQueue(registration.getPatientQueue());
                                break;

                            case 3:
                                if (!registration.patientQueue.isEmpty()) {
                                    Patient next = registration.patientQueue.dequeue();
                                    System.out.println("Serving next patient:");
                                    form.printPatientDetails(next);
                                } else {
                                    System.out.println("No patients in queue.");
                                }
                                break;

                            case 4:
                                form.listPatientList(registration.getPatientList());
                                break;

                            case 5:
                                System.out.print("Enter patient ID to modify: ");
                                String patientID = scanner.nextLine();

                                Patient patientToModify = form.findPatientById(registration.getPatientList(), patientID);

                                if (patientToModify != null) {
                                    System.out.println("\nCurrent patient details:");
                                    form.printPatientDetails(patientToModify);

                                    int modifyChoice = form.getModificationChoice();
                                    switch (modifyChoice) {
                                        case 1:
                                            patientToModify.setName(form.inputPatientName());
                                            break;
                                        case 2:
                                            patientToModify.setAge(form.inputPatientAge());
                                            break;
                                        case 3:
                                            patientToModify.setGender(form.inputPatientGender());
                                            break;
                                        case 4:
                                            patientToModify.setIC(form.inputPatientIC());
                                            break;
                                        case 5:
                                            patientToModify.setPhoneNo(form.inputPatientPhoneNo());
                                            break;
                                        case 6:
                                            break;
                                        default:
                                            System.out.println("Invalid choice.");
                                    }

                                    registration.savePatientsToFile();
                                    System.out.println("Patient updated successfully!");
                                } else {
                                    System.out.println("Patient with ID " + patientID + " not found.");
                                }
                                break;

                            case 6:
                                form.patientReports(registration.getPatientList());
                                break;

                            case 7:
                                System.out.println("Returning to Main Menu...");
                                break;

                            default:
                                System.out.println("Invalid choice, please enter 1–7.");
                        }

                    } while (patientChoice != 7);
                    
                    break;
                case 2:
                    //DoctorManagement.DoctorRun();
                    break;
                case 3:
                    // Consultation
                    break;
                case 4:
                    MedicalTreatment.MedicalTreatmentRun();
                    break;
                case 5:
                    // Pharmacy
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
