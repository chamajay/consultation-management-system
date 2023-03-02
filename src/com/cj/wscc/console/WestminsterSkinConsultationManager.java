/*
 * Copyright (c) 2023 Chamath Jayasena
 * Westminster Skin Consultation Centre
 * OOP (L5) CW
 * UoW ID - w1898955
 * IIT ID - 20211387
 */

package com.cj.wscc.console;

import com.cj.wscc.gui.MainWindow;
import com.formdev.flatlaf.intellijthemes.FlatArcIJTheme;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

/**
 * WestminsterSkinConsultationManager class represents a manager that manages Westminster Skin Consultation Centre.
 * This class maintains the list of the doctors and provides all the methods for the system manager.
 * This class implements SkinConsultationManager interface.
 * This is the entry point to the program that contains the main method.
 */
public class WestminsterSkinConsultationManager implements SkinConsultationManager {
    private static final Scanner scanner = new Scanner(System.in);
    private ArrayList<Doctor> doctors = new ArrayList<>();

    public static void main(String[] args) {
        // Need to create an instance of the class itself to access non-static methods
        WestminsterSkinConsultationManager wscm = new WestminsterSkinConsultationManager();
        wscm.menu();
        wscm.loadData();
        while (true) {
            System.out.print("Enter Option (1-6): ");
            switch (scanner.nextInt()) {
                case 1 -> wscm.addDoctor();
                case 2 -> wscm.deleteDoctor();
                case 3 -> wscm.printDoctors();
                case 4 -> wscm.saveData();
                case 5 -> wscm.openGUI();
                case 6 -> {
                    System.out.println("Bye..");
                    System.exit(0);
                }
                default -> System.out.println(wscm.getOpTxt(1) + " Invalid choice. Please try again.\n");
            }
        }
    }

    /**
     * Prints the console menu
     */
    @Override
    public void menu() {
        System.out.println("+--------------------------------------+");
        System.out.printf("| %s |%n", getColoredTxt("Westminster Skin Consultation Centre", "blue"));
        System.out.println("+--------------------------------------+");
        System.out.println("|   1. Add a New Doctor                |");
        System.out.println("|   2. Delete a Doctor                 |");
        System.out.println("|   3. Print the List of Doctors       |");
        System.out.println("|   4. Save Data to File               |");
        System.out.println("|   5. Open GUI                        |");
        System.out.println("|   6. Exit                            |");
        System.out.println("+--------------------------------------+");
    }

    /**
     * Adds a new doctor to the system
     *
     * @return the newly added doctor
     */
    @Override
    public Doctor addDoctor() {
        System.out.printf("%n%s%n", getColoredTxt("[Add a Doctor]", "blue"));

        Scanner scanner = new Scanner(System.in);

        if (getNoOfDoctors() == 10) {
            System.out.println(getOpTxt(1) + " Maximum doctors allocated in the centre\n");
            return null;
        }

        System.out.print("Enter first name: ");
        String fname = scanner.next();
        if (!isNameValid(fname)) {
            System.out.println(getOpTxt(1) + " Name cannot contain numbers or special characters\n");
            return null;
        }

        System.out.print("Enter surname: ");
        String sname = scanner.next();
        if (!isNameValid(sname)) {
            System.out.println(getOpTxt(1) + " Surname cannot contain numbers or special characters\n");
            return null;
        }

        scanner.nextLine();  // Consume leftover input
        System.out.print("Enter specialisation: ");
        String specialisation = scanner.nextLine();
        if (!isSpecialisationValid(specialisation)) {
            System.out.println(getOpTxt(1) + " Specialisation cannot contain numbers or special characters\n");
            return null;
        }

        System.out.print("Enter medical license number: ");
        String mediLicense = scanner.next();
        if (!isLicenseValid(mediLicense)) {
            System.out.println(getOpTxt(1) + " License number cannot contain special characters\n");
            return null;
        } else if (isDoctorAlreadyAdded(mediLicense)) {
            System.out.println(getOpTxt(1) + " A doctor with the given license number already exists in the system\n");
            return null;
        }

        System.out.print("Enter DOB (yyyy-MM-dd): ");
        String dob = scanner.next();
        if (!isDobValid(dob)) {
            System.out.println(getOpTxt(1) + " DOB should not contain letters and should be in the following format 'yyyy-MM-dd'");
            System.out.println(getOpTxt(2) + " E.g. 1997-08-15\n");
            return null;
        }

        System.out.print("Enter mobile number: ");
        String mobile = scanner.next();
        if (!isMobileValid(mobile)) {
            System.out.println(getOpTxt(1) + " Mobile number cannot contain letters or special characters and should be 10 numbers long\n");
            return null;
        }

        // Create and add the new doctor to the doctors arraylist
        Doctor doctor = new Doctor(capitalize(fname), capitalize(sname), LocalDate.parse(dob), mobile, mediLicense, specialisation);
        doctors.add(doctor);

        // Fire data change event to update the table in the GUI
        updateDocsTableModel();

        System.out.printf("%s Dr. %s added successfully%n%n", getOpTxt(0), doctor.getFullName());

        return doctor;
    }

    /**
     * Deletes a doctor from the system using the doctor's license
     *
     * @return the deleted doctor
     */
    @Override
    public Doctor deleteDoctor() {
        System.out.printf("%n%s%n", getColoredTxt("[Delete a Doctor]", "blue"));

        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter medical license no: ");
        String medLic = scanner.next();
        if (!isLicenseValid(medLic)) {
            System.out.print(getOpTxt(1) + " License No. cannot contain special characters\n\n");
            return null;
        }

        // Find the doctor
        Doctor docToDelete = null;
        for (Doctor d : doctors) {
            if (d.getMedicalLicenseNo().equals(medLic)) {
                docToDelete = d;
            }
        }

        if (docToDelete != null) {
            doctors.remove(docToDelete);
            // fire data change event to update the table
            updateDocsTableModel();
            System.out.printf("%s Dr. %s deleted successfully%n", getOpTxt(0), docToDelete.getFullName());

            // Print the details of the removed doctor
            String titleTemplate = "%-12s %-15s %-25s %-12s %-12s %-12s%n";
            String entryTemplate = "%-12s %-15s %-25s %-12s %-12s %-12s%n";
            System.out.printf(titleTemplate, "------------", "---------------", "-------------------------", "------------", "------------", "----------");
            System.out.printf(titleTemplate, "Name", "Surname", "Specialisation", "LicenseNo", "DOB", "Mobile");
            System.out.printf(titleTemplate, "------------", "---------------", "-------------------------", "------------", "------------", "----------");
            System.out.printf(
                    entryTemplate,
                    docToDelete.getName(),
                    docToDelete.getSurname(),
                    docToDelete.getSpecialisation(),
                    docToDelete.getMedicalLicenseNo(),
                    docToDelete.getDob(),
                    docToDelete.getMobiNo()
            );

            System.out.printf("%s Number of doctors in the centre - %d%n", getOpTxt(2), doctors.size());
            System.out.println();
            // Return deleted doctor
            return docToDelete;
        } else {
            System.out.printf("%s Doctor not found%n", getOpTxt(1));
            System.out.println();
            return null;
        }
    }

    /**
     * Prints the doctors in the system sorted by surname
     */
    @Override
    public void printDoctors() {
        System.out.printf("%n%s%n", getColoredTxt("[Print the List of Doctors]", "blue"));
        System.out.printf("%s Number of doctors in the centre - %d%n", getOpTxt(2), doctors.size());
        System.out.println(getColoredTxt("Note:", "yellow") + " Sorted alphabetically by surname");

        // Create a shallow copy of the original doctors array to avoid modifying the original array
        ArrayList<Doctor> doctorsClone = (ArrayList<Doctor>) doctors.clone();
        // Sort the array alphabetically according to doctor's surname
        Collections.sort(doctorsClone);

        // Print the details of the doctor
        String titleTemplate = "%-12s %-15s %-25s %-12s %-12s %-12s%n";
        String entryTemplate = "%-12s %-15s %-25s %-12s %-12s %-12s%n";
        System.out.printf(titleTemplate, "------------", "---------------", "-------------------------", "------------", "------------", "----------");
        System.out.printf(titleTemplate, "Name", "Surname", "Specialisation", "LicenseNo", "DOB", "Mobile");
        System.out.printf(titleTemplate, "------------", "---------------", "-------------------------", "------------", "------------", "----------");

        for (Doctor doctor : doctorsClone) {
            System.out.printf(
                    entryTemplate,
                    doctor.getName(),
                    doctor.getSurname(),
                    doctor.getSpecialisation(),
                    doctor.getMedicalLicenseNo(),
                    doctor.getDob(),
                    doctor.getMobiNo()
            );
        }
        System.out.println();
    }

    /**
     * Saves the doctors information to a file
     */
    @Override
    public void saveData() {
        System.out.printf("%n%s%n", getColoredTxt("[Save Data to File]", "blue"));

        // https://docs.oracle.com/en/java/javase/17/docs/specs/serialization/serial-arch.html#overview
        // Use a try-with-resources statement so the resources will be automatically closed
        // https://www.baeldung.com/java-try-with-resources
        try (
                FileOutputStream fos = new FileOutputStream("doctors.ser");
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(doctors);
            System.out.printf("%s Data saved successfully to 'doctors.ser' file%n", getOpTxt(0));
        } catch (Exception e) {
            System.out.printf("%s Saving data to file failed%n", getOpTxt(1));
        }
        System.out.println();
    }

    /**
     * Loads back saved doctors information to the program
     */
    @Override
    public void loadData() {
        // Deserialize the doctors object in the 'doctors.ser' file and assign it back
        // https://docs.oracle.com/en/java/javase/17/docs/specs/serialization/serial-arch.html#overview
        try (
                FileInputStream fis = new FileInputStream("doctors.ser");
                ObjectInputStream ois = new ObjectInputStream(fis)
        ) {
            doctors = (ArrayList<Doctor>) ois.readObject();
            System.out.println(getOpTxt(0) + " Data loaded back successfully\n");
        } catch (FileNotFoundException e) {
            System.out.println(getOpTxt(2) + " Could not find the data file to load\n");
        } catch (Exception e) {
            System.out.println(getOpTxt(1) + " Could not load program data\n");
        }
    }

    /**
     * Sets up the FlatLaf swing look and feel
     * Creates the GUI window
     */
    @Override
    public void openGUI() {
        System.out.printf("%n%s%n", getColoredTxt("[Open GUI]", "blue"));
        System.out.println("Launching GUI..");
        System.out.println();

        // setup FlatLaf before launching gui
        // https://www.formdev.com/flatlaf/
        FlatArcIJTheme.install();

        MainWindow mainWindow = new MainWindow(doctors);
        mainWindow.setVisible(true);
    }

    /**
     * Returns the number of doctors in the consultation centre
     *
     * @return the number of doctors
     */
    public int getNoOfDoctors() {
        return doctors.size();
    }

    /**
     * Checks if the given string is a valid name
     *
     * @param name The name to check
     * @return `true` if the name contains only letters, `false` if it doesn't
     */
    public boolean isNameValid(String name) {
        return name.matches("[a-zA-Z]*");
    }

    /**
     * Checks if the given string is a valid specialisation
     *
     * @param spe The specialisation to check
     * @return `true` if the spe contains only letters, `false` if it doesn't
     */
    public boolean isSpecialisationValid(String spe) {
        return spe.matches("[a-zA-Z ]*");
    }

    /**
     * Checks if a doctor already exists with the given license number
     * @param licence The licence to check
     * @return `true` if the doctor already exists, `false` otherwise
     */
    public boolean isDoctorAlreadyAdded(String licence) {
        boolean added = false;
        for (Doctor d : doctors) {
            if (d.getMedicalLicenseNo().equals(licence)) {
                added = true;
                break;
            }
        }
        return added;
    }

    /**
     * Checks if the given string is a valid license number
     *
     * @param license The license to check
     * @return `true` if the license contains only letters and numbers, `false` if it doesn't
     */
    public boolean isLicenseValid(String license) {
        return license.matches("[a-zA-Z0-9]*");
    }

    /**
     * Checks if the given dob string is in the correct date format
     *
     * @param dob The dob to check
     * @return `true` if the dob is in the correct format, `false` if it isn't
     */
    public boolean isDobValid(String dob) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);
        try {
            sdf.parse(dob);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    /**
     * Check if the given mobile number string contains only numbers and the length is 10
     *
     * @param mobile The mobile number to check
     * @return `true` if the mobile number is in the correct format, `false` if it isn't
     */
    public boolean isMobileValid(String mobile) {
        return mobile.matches("[0-9]*") && mobile.length() == 10;
    }

    /**
     * Signals the docsTableModel that the data has changed
     * which updates the table view to reflect the changes
     */
    public void updateDocsTableModel() {
        MainWindow.DocsTableModel docsTableModel = MainWindow.getDocsTableComp() != null ? (MainWindow.DocsTableModel) MainWindow.getDocsTableComp().getDocsTableModel() : null;
        if (docsTableModel != null) {
            docsTableModel.fireTableDataChanged();
        }
    }

    /**
     * Sets the arraylist doctors
     *
     * @param doctors The doctor's arraylist to set
     */
    public void setDoctors(ArrayList<Doctor> doctors) {
        this.doctors = doctors;
    }

    /**
     * Returns the colored operation text that matches the id
     *
     * @param id Number that represents operation text
     * @return colored operation string that matched the id
     */
    public String getOpTxt(int id) {
        switch (id) {
            case 0 -> {
                return getColoredTxt("SUCCESS:", "green");
            }
            case 1 -> {
                return getColoredTxt("ERROR:", "red");
            }
            case 2 -> {
                return getColoredTxt("INFO:", "yellow");
            }
            default -> {
                return "";
            }
        }
    }

    /**
     * Returns colored string of the given text
     * https://www.baeldung.com/java-log-console-in-color
     *
     * @param txt String to color
     * @param color Name of the color
     * @return colored string with the text
     */
    public String getColoredTxt(String txt, String color) {
        String reset = "\u001B[0m";
        Map<String, String> colors = new HashMap<String, String>() {{
            put("red", "\u001B[31m");
            put("green", "\u001B[32m");
            put("yellow", "\u001B[33m");
            put("blue", "\u001B[34m");
            put("purple", "\u001B[35m");
            put("cyan", "\u001B[36m");
        }};
        return colors.get(color) + txt + reset;
    }

    /**
     * Returns a string with first letter capitalized
     *
     * @param txt String to capitalize
     * @return Capitalized string
     */
    public String capitalize(String txt) {
        return txt.substring(0, 1).toUpperCase() + txt.substring(1);
    }
}
