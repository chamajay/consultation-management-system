/*
 * Copyright (c) 2023 Chamath Jayasena
 * Westminster Skin Consultation Centre
 * OOP (L5) CW
 * UoW ID - w1898955
 * IIT ID - 20211387
 */

package com.cj.wscc.console;

import java.time.LocalDate;
import java.util.Objects;

/**
 * The Doctor class represents a doctor in the consultation centre.
 * This class extends the Person class and includes additional information
 * specific to doctors.
 */
public class Doctor extends Person {
    private String medicalLicenseNo;
    private String specialisation;

    public Doctor(String name, String surname, LocalDate DOB, String mobiNo, String medicalLicenseNo, String specialisation) {
        super(name, surname, DOB, mobiNo);
        this.medicalLicenseNo = medicalLicenseNo;
        this.specialisation = specialisation;
    }

    /**
     * Returns the medical license number of the doctor
     *
     * @return the medical license number
     */
    public String getMedicalLicenseNo() {
        return medicalLicenseNo;
    }

    /**
     * Sets the medical license number of the doctor
     *
     * @param medicalLicenseNo the new licence number to set
     */
    public void setMedicalLicenseNo(String medicalLicenseNo) {
        this.medicalLicenseNo = medicalLicenseNo;
    }

    /**
     * Returns the specialisation
     *
     * @return the spcecialisation
     */
    public String getSpecialisation() {
        return specialisation;
    }

    /**
     * Sets the specialisation
     *
     * @param specialisation the new specialisation to set
     */
    public void setSpecialisation(String specialisation) {
        this.specialisation = specialisation;
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     * This implementation compares the full name and license number of this Doctor object to the
     * full name and license number of the other object. If both the name and age are equal, then
     * the objects are considered equal.
     *
     * @param obj the other object to compare
     * @return {@code true} if this object is the same as the obj argument; {@code false} otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Person)) {
            return false;
        }
        Doctor other = (Doctor) obj;
        return Objects.equals(getFullName(), other.getFullName()) && Objects.equals(getMedicalLicenseNo(), other.getMedicalLicenseNo());
    }

}
