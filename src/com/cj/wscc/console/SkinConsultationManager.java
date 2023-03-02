/*
 * Copyright 2022 Chamath Jayasena
 * Westminster Skin Consultation Centre
 * OOP (L5) CW
 * UoW ID - w1898955
 * IIT ID - 20211387
 */

package com.cj.wscc.console;

public interface SkinConsultationManager {
    void menu();
    Doctor addDoctor();
    Doctor deleteDoctor();
    void printDoctors();
    void saveData();
    void loadData();
    void openGUI();
}
