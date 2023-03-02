/*
 * Copyright (c) 2023 Chamath Jayasena
 * Westminster Skin Consultation Centre
 * OOP (L5) CW
 * UoW ID - w1898955
 * IIT ID - 20211387
 */

package com.cj.wscc.gui;

import com.cj.wscc.console.Consultation;
import com.cj.wscc.console.Doctor;
import com.formdev.flatlaf.ui.FlatRoundBorder;
import com.github.lgooddatepicker.components.DateTimePicker;
import com.github.lgooddatepicker.components.TimePickerSettings;
import com.github.lgooddatepicker.optionalusertools.PickerUtilities;
import com.github.lgooddatepicker.optionalusertools.TimeVetoPolicy;

import javax.crypto.Cipher;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Key;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Random;
import java.util.stream.Collectors;

public class MainWindow extends JFrame {
    private final ArrayList<Doctor> doctors;
    private static ArrayList<Consultation> consultations = new ArrayList<>();
    private static DocsTableComp docsTableComp;
    private static ConsTableComp consTableComp;
    private Doctor selectedDoctor;
    private static Consultation selectedConsultation;
    private LocalDate selectedDate;
    private LocalTime selectedTime;

    public MainWindow(ArrayList<Doctor> doctors) {
        super("Westminster Skill Consultation Centre");
        setSize(1280, 720);
        setLocationRelativeTo(null);  // Show the frame in the middle of the screen
        // Don't terminate the program when closing the main window, just dispose the window
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Set doctors arraylist
        this.doctors = doctors;

        // Load saved consultations
        loadConsultations();

        // Add top and left panels
        add(new TopPanelComp(), BorderLayout.NORTH);
        add(new LeftPanelComp(), BorderLayout.WEST);

        // Add right panel
        RightPanelComp rightPanelComp = new RightPanelComp();
        docsTableComp = new DocsTableComp();
        rightPanelComp.add(docsTableComp);
        consTableComp = new ConsTableComp(consultations);
        rightPanelComp.add(consTableComp, 1);
        add(rightPanelComp);

        // Add bottom copyright panel
        CopyrightComp copyrightComp = new CopyrightComp();
        add(copyrightComp, BorderLayout.SOUTH);
    }

    public void handleBtnClick(ActionEvent e) {
        JButton btn = (JButton) e.getSource();
        switch (btn.getText()) {
            case "Check Availability" -> checkAvailability();
            case "Book Consultation" -> bookConsultation();
            case "View Consultation" -> viewConsultation();
            case "Edit Consultation" -> editConsultation();
            case "Remove Consultation" -> removeConsultation();
        }
    }

    public void checkAvailability() {
        if (selectedDoctor == null) {
            JOptionPane.showMessageDialog(
                    null,
                    "Please select a doctor from the Doctors table first",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        } else if (selectedDate == null || selectedTime == null) {
            JOptionPane.showMessageDialog(
                    null,
                    "Please select a date and time",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        } else {
            if (isDoctorAvailable(null)) {
                JOptionPane.showMessageDialog(
                        null,
                        "Doctor is available at the selected time",
                        "Info",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } else {
                JOptionPane.showMessageDialog(
                        null,
                        "Unfortunately, the doctor is unavailable at the chosen time",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    public boolean isDoctorAvailable(Doctor doctor) {
        // If no doctor object is passed, use the selected doctor
        if (doctor == null) {
            doctor = selectedDoctor;
        }

        // Filter and get all the consultations for the selected doctor
        Doctor finalDoctor = doctor;
        ArrayList<Consultation> filteredConsultations = consultations.stream()
                .filter(c -> c.getDoctor().getMedicalLicenseNo().equalsIgnoreCase(finalDoctor.getMedicalLicenseNo()))
                .collect(Collectors.toCollection(ArrayList::new));

        LocalDate date;
        LocalTime time;
        LocalDateTime startDateTime;
        LocalDateTime endDateTime;
        LocalDateTime selectedDateTime = selectedDate.atTime(selectedTime);
        boolean isAvailable = true;
        // Iterate through filtered consultations and compare date times
        for (Consultation c : filteredConsultations) {
            date = c.getDate();
            time = c.getTime();
            startDateTime = date.atTime(time);
            endDateTime = startDateTime.plusHours(c.getDurationHours());

            // Check if the doctor is available at the selected time
            if (selectedDateTime.equals(startDateTime)) {
                isAvailable = false;
            } else if (selectedDateTime.isAfter(startDateTime) && selectedDateTime.isBefore(endDateTime)) {
                isAvailable = false;
            }
        }
        return isAvailable;
    }

    public Doctor getRandomAvailableDoctor() {
        ArrayList<Doctor> availableDoctors = new ArrayList<>();

        // Find doctors with consultations
        ArrayList<Doctor> doctorsWithConsultations = new ArrayList<>();
        for (Consultation c : consultations) {
            if (!doctorsWithConsultations.contains(c.getDoctor())) {
                doctorsWithConsultations.add(c.getDoctor());
            }
        }

        // Get available doctors from the doctors with consultations
        for (Doctor d : doctorsWithConsultations) {
            if (isDoctorAvailable(d)) {
                availableDoctors.add(d);
            }
        }

        // Get doctors without consultations
        for (Doctor d : doctors) {
            if (!doctorsWithConsultations.contains(d)) {
                availableDoctors.add(d);
            }
        }

        // Find available doctors with the same speciality as the selected doctor
        ArrayList<Doctor> availableDoctorsSameSpeciality = new ArrayList<>();
        availableDoctors.forEach(d -> {
            if (d.getSpecialisation().equalsIgnoreCase(selectedDoctor.getSpecialisation())) {
                availableDoctorsSameSpeciality.add(d);
            }
        });

        // If there are doctors with the same speciality available
        // select one randomly from them
        if (availableDoctorsSameSpeciality.size() > 0) {
            Random rand = new Random();
            int bound = availableDoctorsSameSpeciality.size();
            int randIndex = rand.nextInt(bound);
            return availableDoctorsSameSpeciality.get(randIndex);
        }

        // If not, select a doctor randomly from all the available doctors
        Random rand = new Random();
        int bound = availableDoctors.size();
        if (bound > 0) {
            int randIndex = rand.nextInt(bound);
            return availableDoctors.get(randIndex);
        } else {
            return null;
        }
    }

    public void viewConsultation() {
        if (selectedConsultation == null) {
            JOptionPane.showMessageDialog(
                    null,
                    "Please select a consultation from the table to view",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        } else {
            ViewConsultationWindow viewConsultationWindow = new ViewConsultationWindow(selectedConsultation);
            viewConsultationWindow.setVisible(true);
        }
    }

    public void bookConsultation() {
        if (selectedDoctor == null) {
            JOptionPane.showMessageDialog(
                    null,
                    "Please select a doctor from the Doctors table first",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        } else if (selectedDate == null || selectedTime == null) {
            JOptionPane.showMessageDialog(
                    null,
                    "Please select a date and time",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        } else if (!isDoctorAvailable(null)) {
            Doctor randomAvailableDoctor = getRandomAvailableDoctor();

            if (randomAvailableDoctor == null) {
                JOptionPane.showMessageDialog(
                        null,
                        "No doctors are available at the selected time\nPlease choose another time.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            } else {
                String msg = String.format(
                        "<b>Dr. %s</b> is <span style=\"color:red\"><b>not available</b></span> at the selected time.<br>" +
                                "However <b>Dr. %s</b> is available at the selected time.<br>" +
                                "Book consultation with <b>Dr. %s</b>?",
                        selectedDoctor.getFullName(), randomAvailableDoctor.getFullName(),
                        randomAvailableDoctor.getFullName()
                );
                JEditorPane editorPane = new JEditorPane();
                editorPane.setContentType("text/html");
                editorPane.setText(msg);

                // Set editorpane background as the same as a joptionpane's
                JOptionPane jOptionPane = new JOptionPane();
                editorPane.setBackground(jOptionPane.getBackground());

                int result = JOptionPane.showConfirmDialog(
                        null,
                        editorPane,
                        "Confirm",
                        JOptionPane.OK_CANCEL_OPTION
                );
                if (result == JOptionPane.OK_OPTION) {
                    AddConsultationWindow consWindow = new AddConsultationWindow(consultations, randomAvailableDoctor, selectedDate, selectedTime);
                    consWindow.setVisible(true);
                    consWindow.toFront();
                }
            }
        } else {
            AddConsultationWindow consWindow = new AddConsultationWindow(consultations, selectedDoctor, selectedDate, selectedTime);
            consWindow.setVisible(true);
            consWindow.toFront();
        }
    }

    public void editConsultation() {
        if (selectedConsultation == null) {
            JOptionPane.showMessageDialog(
                    null,
                    "Please select a consultation from the table to edit",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        } else {
            EditConsultationWindow editConsultationWindow = new EditConsultationWindow(consultations, selectedConsultation);
            editConsultationWindow.setVisible(true);
        }
    }

    public static void addConsultation(Consultation consultation) {
        consultations.add(consultation);
        // Save consultations object after adding a new consultation
        saveConsultations();
        // Update consultations table
        updateConsTableModel();
    }

    public void removeConsultation() {
        if (selectedConsultation == null) {
            JOptionPane.showMessageDialog(
                    null,
                    "Please select a consultation from the table to remove",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        } else {
            int result = JOptionPane.showConfirmDialog(
                    null,
                    String.format("Are you sure you want to remove consultation %s?", selectedConsultation.getId()),
                    "Confirm",
                    JOptionPane.YES_NO_OPTION
            );

            if (result == JOptionPane.YES_OPTION) {
                consultations.remove(selectedConsultation);
                removeConsultationImgFiles(null);
                selectedConsultation = null;
                saveConsultations();
                updateConsTableModel();
                JOptionPane.showMessageDialog(
                        null,
                        "Consultation removed successfully.",
                        "Info",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
        }
    }

    public static void removeConsultationImgFiles(Consultation consultation) {
        // If no consultaiton object passed, use the selected consultation
        if (consultation == null) {
            consultation = selectedConsultation;
        }

        // Return if there are no images in the consultation
        if (consultation.getImageFiles() == null) {
            return;
        }

        // Get the consultation folder
        final File parentDir = consultation.getImageFiles().get(0).getParentFile();
        // Remove image files
        consultation.getImageFiles().forEach(f -> {
            if (f.exists()) {
                try {
                    Files.delete(Paths.get(f.getAbsolutePath()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        // Remove the consultation folder
        if (parentDir.exists()) {
            parentDir.delete();
        }
    }

    public static void loadConsultations() {
        try {
            // Read the encrypted file and decrypt its contents
            FileInputStream fis = new FileInputStream("consultations.bin");
            byte[] encryptedBytes = new byte[fis.available()];
            fis.read(encryptedBytes);
            fis.close();

            // Create a Cipher object and initialize it for decryption
            Key KEY = PasswordBasedKeyAES.getKey();
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, KEY);

            // Decrypt the encrypted bytes
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

            // Deserialize the decrypted bytes to an object
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(decryptedBytes));
            // Assign the decrypted object
            consultations = (ArrayList<Consultation>) ois.readObject();
            ois.close();
        } catch (Exception ignored) {
        }
    }

    public static void saveConsultations() {
        try {
            // Serialize the object to a byte array
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(consultations);
            oos.close();
            byte[] serializedBytes = baos.toByteArray();

            // Create a Cipher object and initialize it for encryption
            Key KEY = PasswordBasedKeyAES.getKey();
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, KEY);

            // Encrypt the serialized bytes
            byte[] encryptedBytes = cipher.doFinal(serializedBytes);

            // Write the encrypted bytes to a file
            FileOutputStream fos = new FileOutputStream("consultations.bin");
            fos.write(encryptedBytes);
            fos.close();
        } catch (Exception ignored) {
        }
    }

    public static void updateConsTableModel() {
        consTableComp.getConsTableModel().fireTableDataChanged();
    }

    public static DocsTableComp getDocsTableComp() {
        return docsTableComp;
    }

    public class TopPanelComp extends JPanel {
        public TopPanelComp() {
            setBackground(new Color(0x3F4E4F));
            setPreferredSize(new Dimension(0, 70));
            setForeground(new Color(0xFFFFFF));
            setLayout(new BorderLayout());

            JLabel topLabel = new JLabel("Westminster Skin Consultation Centre", SwingConstants.CENTER);
            topLabel.setFont(new Font("SansSerif", Font.PLAIN, 30));
            topLabel.setForeground(new Color(0xFFFFFF));
            add(topLabel, BorderLayout.CENTER);
        }
    }

    public class LeftPanelComp extends JPanel {
        public LeftPanelComp() {
            setLayout(new GridLayout(2, 1, 0, 10));
            setBorder(new EmptyBorder(15, 10, 10, 10));
            setBackground(Color.white);

            // Top panel inside left panel
            JPanel innerTopPanel = new JPanel(new GridLayout(3, 1, 0, 20));
            innerTopPanel.setBorder(new CompoundBorder(
                    new FlatRoundBorder(),
                    new EmptyBorder(10, 10, 10, 10))
            );
            innerTopPanel.setBackground(Color.white);

            // Add date time picker inside the inner top panel
            DateTimePickerComp dateTimePickerComp = new DateTimePickerComp();
            innerTopPanel.add(dateTimePickerComp);

            // Add check availability button
            MainBtnComp checkAvailabilityBtn = new MainBtnComp("Check Availability");
            checkAvailabilityBtn.addActionListener(MainWindow.this::handleBtnClick);
            innerTopPanel.add(checkAvailabilityBtn);

            // Add book consultation button
            MainBtnComp bookConsultationBtn = new MainBtnComp("Book Consultation");
            bookConsultationBtn.addActionListener(MainWindow.this::handleBtnClick);
            innerTopPanel.add(bookConsultationBtn);

            add(innerTopPanel);

            // Bottom panel inside left panel
            JPanel innerBottomPanel = new JPanel(new GridLayout(3, 1, 0, 20));
            innerBottomPanel.setBorder(new CompoundBorder(
                    new FlatRoundBorder(),
                    new EmptyBorder(10, 10, 10, 10))
            );
            innerBottomPanel.setBackground(Color.white);

            // Add view consultation button
            MainBtnComp viewConsultationBtn = new MainBtnComp("View Consultation");
            viewConsultationBtn.addActionListener(MainWindow.this::handleBtnClick);
            innerBottomPanel.add(viewConsultationBtn);

            // Add edit consultation button
            MainBtnComp editConsultationBtn = new MainBtnComp("Edit Consultation");
            editConsultationBtn.addActionListener(MainWindow.this::handleBtnClick);
            innerBottomPanel.add(editConsultationBtn);

            // Add remove consultation button
            MainBtnComp removeConsultation = new MainBtnComp("Remove Consultation");
            removeConsultation.addActionListener(MainWindow.this::handleBtnClick);
            innerBottomPanel.add(removeConsultation);

            add(innerBottomPanel);
        }
    }

    public class DateTimePickerComp extends JPanel {
        public DateTimePickerComp() {
            setLayout(new BorderLayout());
            setBackground(Color.white);

            DateTimePicker dateTimePicker = new DateTimePicker();
            dateTimePicker.setBackground(Color.white);

            // Limit time range to from 8am to 5pm using veto policy
            TimePickerSettings timeSettings = dateTimePicker.timePicker.getSettings();
            timeSettings.setVetoPolicy(new SampleTimeVetoPolicy());

            // Set initial date and time
            dateTimePicker.datePicker.setDateToToday();
            selectedDate = dateTimePicker.getDatePicker().getDate();
            dateTimePicker.timePicker.setTimeToNow();
            selectedTime = dateTimePicker.getTimePicker().getTime();

            // Width and height of scaled icon
            int width = 24;
            int height = 24;

            // Add icon to datepicker button
            ImageIcon oriCalIcon = new ImageIcon("resources/calendar_icon.png");
            Image caledarIcon = oriCalIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            JButton datePickerBtn = dateTimePicker.getDatePicker().getComponentToggleCalendarButton();
            datePickerBtn.setPreferredSize(new Dimension(40, datePickerBtn.getPreferredSize().height));
            datePickerBtn.setText("");
            datePickerBtn.setIcon(new ImageIcon(caledarIcon));

            // Add icon to timepicker button
            ImageIcon oriClockIcon = new ImageIcon("resources/clock_icon.png");
            Image clockIcon = oriClockIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            JButton timePickerBtn = dateTimePicker.getTimePicker().getComponentToggleTimeMenuButton();
            timePickerBtn.setPreferredSize(new Dimension(40, timePickerBtn.getPreferredSize().height));
            timePickerBtn.setText("");
            timePickerBtn.setIcon(new ImageIcon(clockIcon));

            // add date and time change listeners
            dateTimePicker.getDatePicker().addDateChangeListener(dateChangeEvent -> selectedDate = dateTimePicker.datePicker.getDate());
            dateTimePicker.getTimePicker().addTimeChangeListener(timeChangeEvent -> selectedTime = dateTimePicker.timePicker.getTime());

            add(dateTimePicker, BorderLayout.CENTER);
        }
    }

    public class MyTableComp extends JTable {
        public MyTableComp(AbstractTableModel tableModel, TableRowSorter rowSorter) {
            super(tableModel);
            setRowSorter(rowSorter);  // Make columns sortable
            setFont(new Font("SansSerif", Font.PLAIN, 17));
            getTableHeader().setReorderingAllowed(false);  // Disable column dragging
            getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 17));
            setRowHeight(30);
            setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        }
    }

    public class DocsTableComp extends JPanel {
        private final MyTableComp myTableComp;
        private final DocsTableModel docsTableModel;

        public DocsTableComp() {
            setLayout(new BorderLayout(0, 5));
            setBackground(new Color(0xFFFFFF));
            TitledBorder titledBorder = new TitledBorder("Doctors");
            titledBorder.setBorder(new FlatRoundBorder());
            titledBorder.setTitleFont(new Font("SansSerif", Font.BOLD, 15));
            setBorder(new CompoundBorder(titledBorder, new EmptyBorder(10, 10, 10, 10)));

            docsTableModel = new DocsTableModel();
            myTableComp = new MyTableComp(docsTableModel, new TableRowSorter<>(docsTableModel));
            // Increase the width of 'specialisation' column
            myTableComp.getColumnModel().getColumn(2).setPreferredWidth(160);
            // Add a focusadapter to the table
            myTableComp.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    consTableComp.getTable().clearSelection();
                    selectedConsultation = null;
                }
            });

            // Add selection listener to table
            myTableComp.getSelectionModel().addListSelectionListener(listSelectionEvent -> {
                int row = myTableComp.getSelectedRow();
                if (!listSelectionEvent.getValueIsAdjusting() && row >= 0) {
                    String docLicense = (String) myTableComp.getValueAt(row, 3);
                    selectedDoctor = doctors.stream()
                            .filter(d -> d.getMedicalLicenseNo().equalsIgnoreCase(docLicense))
                            .findFirst()
                            .orElse(null);
                }
            });

            add(new JScrollPane(myTableComp), BorderLayout.CENTER);
        }

        public JTable getTable() {
            return myTableComp;
        }

        public AbstractTableModel getDocsTableModel() {
            return docsTableModel;
        }
    }

    public class ConsTableComp extends JPanel {
        private final MyTableComp myTableComp;
        private final ConsTableModel consTableModel;

        public ConsTableComp(ArrayList<Consultation> consultations) {
            setLayout(new BorderLayout(0, 5));
            setBackground(new Color(0xFFFFFF));

            TitledBorder titledBorder = new TitledBorder("Consultations");
            titledBorder.setBorder(new FlatRoundBorder());
            titledBorder.setTitleFont(new Font("SansSerif", Font.BOLD, 15));
            setBorder(new CompoundBorder(titledBorder, new EmptyBorder(10, 10, 10, 10)));

            consTableModel = new ConsTableModel(consultations);
            myTableComp = new MyTableComp(consTableModel, new TableRowSorter<>(consTableModel));
            myTableComp.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    docsTableComp.getTable().clearSelection();
                    selectedDoctor = null;
                }
            });

            myTableComp.getSelectionModel().addListSelectionListener(listSelectionEvent -> {
                int row = myTableComp.getSelectedRow();
                if (!listSelectionEvent.getValueIsAdjusting() && row >= 0) {
                    String consId = (String) myTableComp.getValueAt(row, 0);
                    selectedConsultation = consultations.stream()
                            .filter(d -> d.getId().equalsIgnoreCase(consId))
                            .findFirst()
                            .orElse(null);
                }
            });

            add(new JScrollPane(myTableComp), BorderLayout.CENTER);
        }

        public JTable getTable() {
            return myTableComp;
        }

        public AbstractTableModel getConsTableModel() {
            return consTableModel;
        }
    }

    public class RightPanelComp extends JPanel {
        public RightPanelComp() {
            setLayout(new GridLayout(2, 1, 0, 15));
            setPreferredSize(new Dimension(1000, 0));
            setBorder(new EmptyBorder(10, 10, 10, 10));
            setBackground(Color.white);
        }
    }

    public class MainBtnComp extends JButton {
        public MainBtnComp(String txt) {
            super(txt);
            setFont(new Font("SansSerif", Font.BOLD, 16));
            setFocusable(false);
            setFocusPainted(false);
            setBackground(new Color(0xDCD7C9));
            setForeground(Color.black);
        }
    }

    public class CopyrightComp extends JPanel {
        public CopyrightComp() {
            setLayout(new BorderLayout());
            setBorder(new EmptyBorder(0, 0, 10, 10));
            setBackground(Color.white);

            JLabel jLabel = new JLabel("Copyright © 2023 Chamath Jayasena. All rights reserved.");
            jLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
            jLabel.setEnabled(false);
            add(jLabel, BorderLayout.EAST);
        }

    }

    public class DocsTableModel extends AbstractTableModel {
        String[] colNames;
        Class[] colClasses;

        public DocsTableModel() {
            colNames = new String[]{
                    "First Name",
                    "Surname",
                    "Specialisation",
                    "License",
                    "DOB",
                    "Mobile"
            };

            colClasses = new Class[]{
                    String.class,
                    String.class,
                    String.class,
                    String.class,
                    LocalDate.class,
                    String.class
            };
        }

        @Override
        public int getRowCount() {
            return doctors.size();
        }

        @Override
        public int getColumnCount() {
            return colNames.length;
        }

        @Override
        public Object getValueAt(int row, int col) {
            switch (col) {
                case 0 -> {
                    return doctors.get(row).getName();
                }
                case 1 -> {
                    return doctors.get(row).getSurname();
                }
                case 2 -> {
                    return doctors.get(row).getSpecialisation();
                }
                case 3 -> {
                    return doctors.get(row).getMedicalLicenseNo();
                }
                case 4 -> {
                    return doctors.get(row).getDob();
                }
                case 5 -> {
                    return doctors.get(row).getMobiNo();
                }
                default -> {
                    return null;
                }
            }
        }

        public String getColumnName(int col) {
            return colNames[col];
        }

        public Class getColumnClass(int col) {
            return colClasses[col];
        }
    }

    public class ConsTableModel extends AbstractTableModel {
        private final ArrayList<Consultation> consultations;
        private final String[] colNames;

        private final Class[] colClasses;

        public ConsTableModel(ArrayList<Consultation> data) {
            this.consultations = data;

            colNames = new String[]{
                    "ID",
                    "Patient ID",
                    "Patient",
                    "Doctor",
                    "Date",
                    "Time",
                    "Duration (h)",
                    "Cost (£)"
            };

            colClasses = new Class[]{
                    String.class,
                    String.class,
                    String.class,
                    String.class,
                    String.class,
                    String.class,
                    String.class,
                    String.class
            };
        }

        @Override
        public int getRowCount() {
            return consultations.size();
        }

        @Override
        public int getColumnCount() {
            return colNames.length;
        }

        @Override
        public Object getValueAt(int row, int col) {
            DecimalFormat df = new DecimalFormat("0.00");
            switch (col) {
                case 0 -> {
                    return consultations.get(row).getId();
                }
                case 1 -> {
                    return consultations.get(row).getPatient().getPatientId();
                }
                case 2 -> {
                    return consultations.get(row).getPatient().getFullName();
                }
                case 3 -> {
                    return consultations.get(row).getDoctor().getFullName();
                }
                case 4 -> {
                    return consultations.get(row).getDate().toString();
                }
                case 5 -> {
                    return consultations.get(row).getTime().toString();
                }
                case 6 -> {
                    return consultations.get(row).getDurationHours() + "";
                }
                case 7 -> {
                    return df.format(consultations.get(row).getCost());
                }
                default -> {
                    return null;
                }
            }
        }

        public String getColumnName(int col) {
            return colNames[col];
        }

        public Class getColumnClass(int col) {
            return colClasses[col];
        }
    }


    /**
     * SampleTimeVetoPolicy, A veto policy is a way to disallow certain times from being selected in
     * the time picker. A vetoed time cannot be added to the time drop down menu. A vetoed time cannot
     * be selected by using the keyboard or the mouse.
     */
    private static class SampleTimeVetoPolicy implements TimeVetoPolicy {
        /**
         * isTimeAllowed, Return true if a time should be allowed, or false if a time should be vetoed.
         */
        @Override
        public boolean isTimeAllowed(LocalTime time) {
            // Only allow times from 9a to 5p, inclusive.
            return PickerUtilities.isLocalTimeInRange(
                    time, LocalTime.of(8, 00), LocalTime.of(17, 00), true);
        }
    }
}
