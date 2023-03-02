/*
 * Copyright (c) 2023 Chamath Jayasena
 * Westminster Skin Consultation Centre
 * OOP (L5) CW
 * UoW ID - w1898955
 * IIT ID - 20211387
 */

package com.cj.wscc.console;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Person class represents a person.
 * This is the base class for the Doctor and Patient classes, which include
 * additional information specific to each type of person.
 *
 * This class implements Serializable interface to be able to serialize objects
 * made using this class.
 *
 * This class implements Comparable interface to make objects made using the class
 * can be compared
 */
public class Person implements Comparable<Person>, Serializable {
    private String name;
    private String surname;
    private LocalDate dob;
    private String mobiNo;

    public Person(String name, String surname, LocalDate dob, String mobiNo) {
        this.name = name;
        this.surname = surname;
        this.dob = dob;
        this.mobiNo = mobiNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getFullName() {
        return name + " " + surname;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public String getMobiNo() {
        return mobiNo;
    }

    public void setMobiNo(String mobiNo) {
        this.mobiNo = mobiNo;
    }

    // by default compare surnames
    @Override
    public int compareTo(Person person) {
        return this.surname.compareToIgnoreCase(person.getSurname());
    }
}
