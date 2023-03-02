/*
 * Copyright (c) 2023 Chamath Jayasena
 * Westminster Skin Consultation Centre
 * OOP (L5) CW
 * UoW ID - w1898955
 * IIT ID - 20211387
 */

package com.cj.wscc.console;

import java.io.File;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

/**
 * Consultation class represents a consultation in the consultation centre.
 * This class implements Serializable interface to be able to serialize objects
 * made using this class.
 */
public class Consultation implements Serializable {
    private String id;
    private Doctor doctor;
    private Patient patient;
    private LocalDate date;
    private LocalTime time;
    private int durationHours;
    private double cost;
    private String notes;
    private ArrayList<File> imageFiles;

    public Consultation(String id, Doctor doctor, Patient patient, LocalDate date, LocalTime time, int duration, double cost, String notes, ArrayList<File> files) {
        this.id = id;
        this.doctor = doctor;
        this.patient = patient;
        this.date = date;
        this.time = time;
        this.durationHours = duration;
        this.cost = cost;
        this.notes = notes;
        this.imageFiles = files;
    }

    /**
     * Returns the patient ID
     *
     * @return the patient ID
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the ID of the patient
     *
     * @param id the new ID to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Returns the doctor associated with the consultation
     *
     * @return the doctor
     */
    public Doctor getDoctor() {
        return doctor;
    }

    /**
     * Sets the doctor associated with the consultation
     *
     * @param doctor the new doctor to set
     */
    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    /**
     * Returns the patient associated with the consultation
     *
     * @return the patient
     */
    public Patient getPatient() {
        return patient;
    }

    /**
     * Sets the patient associated with the consultation
     *
     * @param patient the new patient to set
     */
    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    /**
     * Returns the date of the consultation
     *
     * @return the date
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Sets the date of the consultation
     *
     * @param date the new date to set
     */
    public void setDate(LocalDate date) {
        this.date = date;
    }

    /**
     * Returns the time of the consultation
     *
     * @return the time
     */
    public LocalTime getTime() {
        return time;
    }

    /**
     * Sets the time of the consultation
     *
     * @param time the new time to set
     */
    public void setTime(LocalTime time) {
        this.time = time;
    }

    /**
     * Returns the duration in hours of the consultation
     *
     * @return the duration in hours
     */
    public int getDurationHours() {
        return durationHours;
    }

    /**
     * Sets the duration of the consultation
     *
     * @param durationHours the new duration in hours
     */
    public void setDurationHours(int durationHours) {
        this.durationHours = durationHours;
    }

    /**
     * Returns the cost of the consultation
     *
     * @return the cost
     */
    public double getCost() {
        return cost;
    }

    /**
     * Sets the cost of the consultation
     *
     * @param cost the new cost to set
     */
    public void setCost(double cost) {
        this.cost = cost;
    }

    /**
     * Returns the patient notes
     *
     * @return the notes text
     */
    public String getNotes() {
        return notes;
    }

    /**
     * Sets the patient notes of the consultation
     *
     * @param notes the new notes to set
     */
    public void setNotes(String notes) {
        this.notes = notes;
    }

    /**
     * Returns the patient image files
     *
     * @return images files arraylist
     */
    public ArrayList<File> getImageFiles() {
        return imageFiles;
    }

    /**
     * Sets the patient image files
     *
     * @param imageFiles the new image files to set
     */
    public void setImageFiles(ArrayList<File> imageFiles) {
        this.imageFiles = imageFiles;
    }

}
