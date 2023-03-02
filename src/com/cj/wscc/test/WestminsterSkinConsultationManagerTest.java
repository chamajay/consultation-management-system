/*
 * Copyright (c) 2023 Chamath Jayasena
 * Westminster Skin Consultation Centre
 * OOP (L5) CW
 * UoW ID - w1898955
 * IIT ID - 20211387
 */

package com.cj.wscc.test;

import com.cj.wscc.console.Doctor;
import com.cj.wscc.console.WestminsterSkinConsultationManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Test class for the WestminsterSkinConsultationManager class")
class WestminsterSkinConsultationManagerTest {
    private final WestminsterSkinConsultationManager wscm = new WestminsterSkinConsultationManager();

    @Test()
    @DisplayName("Add a new doctor")
    void addDoctor() {
        Doctor expected = new Doctor(
                "John",
                "Doe",
                LocalDate.of(1990, 10, 20),
                "0771234567",
                "WE6534522",
                "Paediatric Dermatology"
        );

        // Number of doctors before adding the doctor
        int noOfDocsBefore = wscm.getNoOfDoctors();

        // https://stackoverflow.com/questions/6415728/junit-testing-with-simulated-user-input
        // Backup System.in to restore it later
        InputStream sysInBackup = System.in;
        String newDocInfo = "John\nDoe\nPaediatric Dermatology\nWE6534522\n1990-10-20\n0771234567";
        ByteArrayInputStream in = new ByteArrayInputStream(newDocInfo.getBytes());
        // Set new System.in
        System.setIn(in);
        // Get the return of the addDoctor method
        Doctor result = wscm.addDoctor();
        // Reset System.in
        System.setIn(sysInBackup);

        assertEquals(expected, result);
        assertEquals(wscm.getNoOfDoctors(), noOfDocsBefore + 1);
    }

    @Test
    @DisplayName("Remove an existing doctor")
    void removeDoctor() {
        // Create a new arraylist of doctors and add one doctor
        ArrayList<Doctor> doctors = new ArrayList<Doctor>(10);
        Doctor expected = new Doctor(
                "john",
                "doe",
                LocalDate.parse("1980-01-16"),
                "0000000000",
                "WE3564G3",
                "Medical Dermatology"
        );
        doctors.add(expected);

        // Set the doctors arraylist
        wscm.setDoctors(doctors);

        // Number of doctors before removing the doctor
        int noOfDocsBefore = wscm.getNoOfDoctors();

        // Backup System.in to restore it later
        InputStream sysInBackup = System.in;
        String lisenceOfDocToRemove = "WE3564G3";
        ByteArrayInputStream in = new ByteArrayInputStream(lisenceOfDocToRemove.getBytes());
        // Set new System.in
        System.setIn(in);
        // Get the return of the removeDoctor method
        Doctor result = wscm.deleteDoctor();
        // Reset System.in
        System.setIn(sysInBackup);

        assertEquals(expected, result);
        assertEquals(wscm.getNoOfDoctors(), noOfDocsBefore - 1);
    }

    @Test
    @DisplayName("Name input validation")
    void isNameValid() {
        assertTrue(wscm.isNameValid("John"));
        assertFalse(wscm.isNameValid("Robert2345"));
        assertFalse(wscm.isNameValid("12345"));
        assertFalse(wscm.isNameValid("!@#$%^&*"));
    }

    @Test
    @DisplayName("Specialisation input validation")
    void isSpecialisationValid() {
        assertTrue(wscm.isSpecialisationValid("cosmetic dermatology"));
        assertFalse(wscm.isSpecialisationValid("dermatology 123"));
        assertFalse(wscm.isSpecialisationValid("dermatology_$dermatology"));
        assertFalse(wscm.isSpecialisationValid("12345"));
        assertFalse(wscm.isSpecialisationValid("!@#$%^&*"));
    }

    @Test
    @DisplayName("License input validation")
    void isLicenseValid() {
        assertTrue(wscm.isLicenseValid("abcdefgh"));
        assertTrue(wscm.isLicenseValid("12345678"));
        assertTrue(wscm.isLicenseValid("abcd1234"));
        assertFalse(wscm.isLicenseValid("$ab^ 123"));
        assertFalse(wscm.isLicenseValid("!@#$%^&*)"));
    }

    @Test
    @DisplayName("DOB input validation")
    void isDobValid() {
        assertTrue(wscm.isDobValid("1990-08-15"));
        assertFalse(wscm.isDobValid("15-08-1990"));
        assertFalse(wscm.isDobValid("1990/08/5"));
        assertFalse(wscm.isDobValid("1234567"));
        assertFalse(wscm.isDobValid("abcdefg"));
        assertFalse(wscm.isDobValid("!@#$%^&*>"));
    }

    @Test
    @DisplayName("Mobile No. input validation")
    void isMobileValid() {
        assertTrue(wscm.isMobileValid("0771234567"));
        assertFalse(wscm.isMobileValid("07712345678"));
        assertFalse(wscm.isMobileValid("abcedsdfea"));
        assertFalse(wscm.isMobileValid("077abcd"));
        assertFalse(wscm.isMobileValid("077-1234567"));
        assertFalse(wscm.isMobileValid("!@#$%^&*"));
    }

    @Test
    @DisplayName("Can only add 10 doctors")
    void addDoctorWhenFull() {
        // Create a new arraylist of 10 dummy doctors
        ArrayList<Doctor> doctors = new ArrayList<Doctor>(10);
        for (int i = 0; i < 10; i++) {
            Doctor doctor = new Doctor(
                    "john",
                    "doe",
                    LocalDate.parse("1980-01-16"),
                    "0000000000",
                    "WE3564G3",
                    "Medical Dermatology"
            );
            doctors.add(doctor);
        }

        // Set the doctors arraylist
        wscm.setDoctors(doctors);

        // Try to add another doctor
        Doctor newDoctor = wscm.addDoctor();

        // Check if the newDoctor is null
        assertNull(newDoctor);
    }

}