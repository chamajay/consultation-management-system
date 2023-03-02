/*
 * Copyright (c) 2023 Chamath Jayasena
 * Westminster Skin Consultation Centre
 * OOP (L5) CW
 * UoW ID - w1898955
 * IIT ID - 20211387
 */

package com.cj.wscc.console;

import java.time.LocalDate;

/**
 * The Patient class represents a patient in the consultation centre.
 * This class extends the Person class and includes additional information
 * specific to patients.
 */
public class Patient extends Person {
    private int patientId;

    public Patient(String name, String surname, LocalDate DOB, String mobiNo, int patientId) {
        super(name, surname, DOB, mobiNo);
        this.patientId = patientId;
    }

    /**
     * Returns the ID of the patient
     *
     * @return the patient ID
     */
    public int getPatientId() {
        return patientId;
    }

    /**
     * Sets the patient ID of the patient
     *
     * @param patientId the new ID to set
     */
    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

}
