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
import com.cj.wscc.console.Patient;
import com.formdev.flatlaf.ui.FlatRoundBorder;
import com.github.lgooddatepicker.components.DatePicker;

import javax.crypto.Cipher;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class AddConsultationWindow extends JFrame {
    private final ArrayList<Consultation> consultations;
    private final MyPatientIdComp patientIdComp;
    private final DurationCostComp durationCostComp;
    private final MyTxtFieldComp patientNameComp;
    private final MyTxtFieldComp patientSurnameComp;
    private final MyDOBComp patientDobComp;
    private final MyTxtFieldComp patientMobileComp;
    private final NotesComp notesComp;
    private final FileChooserComp fileChooserComp;
    private final Doctor selectedDoctor;
    private Patient selectedPatient;
    private final LocalDate selectedDate;
    private final LocalTime selectedTime;
    private final DecimalFormat df = new DecimalFormat("0.00");

    public AddConsultationWindow(ArrayList<Consultation> consultations, Doctor selectedDoctor, LocalDate selectedDate, LocalTime selectedTime) {
        super("Book Consultation");
        setSize(900, 700);
        setLocationRelativeTo(null);  // Show the frame in the middle of the screen
        // Close the frame and release resources when closes the window
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        this.consultations = consultations;
        this.selectedDoctor = selectedDoctor;
        this.selectedDate = selectedDate;
        this.selectedTime = selectedTime;

        // Add top panel
        add(new TopPanelComp(), BorderLayout.NORTH);

        // Center panel
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[]{50};
        JPanel centerPanel = new JPanel(gridBagLayout);
        centerPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        centerPanel.setBackground(Color.white);

        // Add doctor component
        MyTxtFieldComp doctorComp = new MyTxtFieldComp("Doctor");
        JTextField docTxtField = (JTextField) doctorComp.getComponents()[0];
        docTxtField.setText("Dr. " + selectedDoctor.getFullName());
        docTxtField.setEditable(false);
        docTxtField.setFocusable(false);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 1;
        c.weightx = 1;
        c.gridx = 0;
        c.gridy = 0;
        centerPanel.add(doctorComp, c);

        // Add id component
        patientIdComp = new MyPatientIdComp("Patient ID *");
        c.gridx = 0;
        c.gridy = 1;
        centerPanel.add(patientIdComp, c);

        // Add name and surname components
        patientNameComp = new MyTxtFieldComp("Patient Name *");
        patientSurnameComp = new MyTxtFieldComp("Patient Surname *");
        c.gridx = 0;
        c.gridy = 2;
        centerPanel.add(patientNameComp, c);
        c.gridx = 1;
        c.gridy = 2;
        centerPanel.add(patientSurnameComp, c);

        // Add dob component
        c.gridx = 0;
        c.gridy = 3;
        patientDobComp = new MyDOBComp();
        centerPanel.add(patientDobComp, c);

        // Add mobile component
        c.gridx = 1;
        c.gridy = 3;
        patientMobileComp = new MyTxtFieldComp("Patient Mobile *");
        centerPanel.add(patientMobileComp, c);

        // Add date time component
        c.gridx = 1;
        c.gridy = 0;
        JPanel date = new MyTxtFieldComp("Date & Time");
        JTextField dateTxtField = (JTextField) date.getComponents()[0];
        dateTxtField.setText(selectedDate.toString() + " " + selectedTime.toString());
        dateTxtField.setEditable(false);
        dateTxtField.setFocusable(false);
        centerPanel.add(date, c);

        // Add duration and cost components
        c.gridx = 1;
        c.gridy = 1;
        durationCostComp = new DurationCostComp();
        centerPanel.add(durationCostComp, c);

        // Add notes component
        c.gridx = 0;
        c.gridy = 4;
        notesComp = new NotesComp();
        centerPanel.add(notesComp, c);

        // Add image file chooser component
        c.gridx = 1;
        c.gridy = 4;
        fileChooserComp = new FileChooserComp();
        centerPanel.add(fileChooserComp, c);

        add(centerPanel, BorderLayout.CENTER);
        add(new BtnPanelComp(), BorderLayout.SOUTH);
    }

    public static class MyTxtFieldComp extends JPanel {
        private final JTextField txtField;

        public MyTxtFieldComp(String label) {
            setLayout(new BorderLayout());
            setBackground(Color.white);

            TitledBorder titledBorder = new TitledBorder(label);
            titledBorder.setBorder(new FlatRoundBorder());
            setBorder(new CompoundBorder(
                    titledBorder,
                    new EmptyBorder(0, 5, 5, 5)));

            txtField = new JTextField();
            txtField.setFont(new Font("SansSerif", Font.PLAIN, 20));
            add(txtField);
        }

        public JTextField getTxtField() {
            return txtField;
        }
    }

    public class MyPatientIdComp extends JPanel {
        private final JComboBox comboBox;

        public MyPatientIdComp(String label) {
            setLayout(new BorderLayout());
            setBackground(Color.white);

            TitledBorder titledBorder = new TitledBorder(label);
            titledBorder.setBorder(new FlatRoundBorder());
            setBorder(new CompoundBorder(
                    titledBorder,
                    new EmptyBorder(0, 5, 5, 5)));

            // Get existing patient ids
            ArrayList<Integer> suggestions = new ArrayList<>();
            for (Consultation c : consultations) {
                if (!suggestions.contains(c.getPatient().getPatientId())) {
                    suggestions.add(c.getPatient().getPatientId());
                }
            }

            comboBox = new JComboBox(suggestions.toArray());
            comboBox.setEditable(true);
            comboBox.setSelectedItem(null);
            comboBox.setFont(new Font("SansSerif", Font.PLAIN, 20));
            // Add a document listener to the text field to update the cost and patient info when the text changes
            JTextField textField = (JTextField) comboBox.getEditor().getEditorComponent();
            textField.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    setPatient(textField.getText());
                    updateCost();
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    setPatient(textField.getText());
                    updateCost();
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    setPatient(textField.getText());
                    updateCost();
                }
            });
            add(comboBox, BorderLayout.CENTER);
        }

        public JComboBox getJComboBox() {
            return comboBox;
        }
    }

    public static class MyDOBComp extends JPanel {
        DatePicker datePicker;

        public MyDOBComp() {
            setLayout(new BorderLayout());
            setBackground(Color.white);

            TitledBorder titledBorder = new TitledBorder("Patient DOB");
            titledBorder.setBorder(new FlatRoundBorder());
            setBorder(new CompoundBorder(
                    titledBorder,
                    new EmptyBorder(0, 5, 5, 5)));

            datePicker = new DatePicker();
            datePicker.getComponentDateTextField().setFont(new Font("SansSerif", Font.PLAIN, 20));
            add(datePicker);
        }

        public DatePicker getDatePicker() {
            return datePicker;
        }
    }

    public class DurationCostComp extends JPanel {
        JFormattedTextField durationTxtField;
        JTextField costTxtField;

        public DurationCostComp() {
            setLayout(new BorderLayout(5, 0));
            setBackground(Color.white);

            // Duration panel
            JPanel durationContainer = new JPanel(new BorderLayout());
            durationContainer.setBackground(Color.white);
            TitledBorder titledBorder = new TitledBorder("Duration (h) *");
            titledBorder.setBorder(new FlatRoundBorder());
            durationContainer.setBorder(new CompoundBorder(
                    titledBorder,
                    new EmptyBorder(0, 5, 5, 5))
            );
            durationTxtField = new JFormattedTextField();
            durationTxtField.setFont(new Font("SansSerif", Font.PLAIN, 20));
            durationTxtField.addCaretListener(new CaretListener() {
                @Override
                public void caretUpdate(CaretEvent caretEvent) {
                    updateCost();
                }
            });
            durationContainer.add(durationTxtField);
            add(durationContainer, BorderLayout.CENTER);

            // Cost panel
            JPanel costContainer = new JPanel(new BorderLayout());
            costContainer.setBackground(Color.white);
            TitledBorder titledBorder2 = new TitledBorder("Cost (£)");
            titledBorder2.setBorder(new FlatRoundBorder());
            costContainer.setBorder(new CompoundBorder(
                    titledBorder2,
                    new EmptyBorder(0, 5, 5, 5))
            );
            costTxtField = new JTextField();
            costTxtField.setColumns(8);
            costTxtField.setFocusable(false);
            costTxtField.setText("0.00");
            costTxtField.setEditable(false);
            costTxtField.setFont(new Font("SansSerif", Font.PLAIN, 20));
            costContainer.add(costTxtField);
            add(costContainer, BorderLayout.EAST);
        }

        public JTextField getDurationTxtField() {
            return durationTxtField;
        }

        public JTextField getCostTxtField() {
            return costTxtField;
        }
    }

    public static class NotesComp extends JPanel {
        private final JTextArea textArea;

        public NotesComp() {
            setLayout(new BorderLayout());
            setBackground(Color.white);

            TitledBorder titledBorder = new TitledBorder("Notes");
            titledBorder.setBorder(new FlatRoundBorder());
            setBorder(new CompoundBorder(
                    titledBorder,
                    new EmptyBorder(0, 5, 5, 5))
            );

            textArea = new JTextArea();
            textArea.setRows(4);
            textArea.setFont(new Font("SansSerif", Font.PLAIN, 20));
            add(new JScrollPane(textArea), BorderLayout.CENTER);
        }

        public JTextArea getTextArea() {
            return textArea;
        }
    }

    public static class TopPanelComp extends JPanel {
        public TopPanelComp() {
            setLayout(new BorderLayout());
            setBackground(new Color(0x3D8361));
            setPreferredSize(new Dimension(0, 50));
            setForeground(new Color(0xFFFFFF));
            setLayout(new BorderLayout());

            JLabel topLabel = new JLabel("Book Consultation", SwingConstants.CENTER);
            topLabel.setFont(new Font("SansSerif", Font.PLAIN, 25));
            topLabel.setForeground(new Color(0xFFFFFF));
            add(topLabel, BorderLayout.CENTER);
        }
    }

    public static class FileChooserComp extends JPanel {
        private final JFileChooser fileChooser;
        private final JTextArea textField;

        public FileChooserComp() {
            setLayout(new BorderLayout());
            setBackground(Color.white);

            TitledBorder titledBorder = new TitledBorder("Add Images");
            titledBorder.setBorder(new FlatRoundBorder());
            setBorder(new CompoundBorder(
                    titledBorder,
                    new EmptyBorder(0, 5, 5, 5))
            );

            fileChooser = new JFileChooser();
            fileChooser.setMultiSelectionEnabled(true);
            fileChooser.setPreferredSize(new Dimension(1000, 500));
            fileChooser.setAcceptAllFileFilterUsed(false);
            // Only let choose image files (jpg, jpeg, png and gif)
            fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Image files", "jpg", "jpeg", "png", "gif"));
            fileChooser.setDialogTitle("Choose image files");

            JButton chooseBtn = new JButton("Choose Images");

            textField = new JTextArea();
            textField.setRows(3);
            textField.setEditable(false);
            textField.setFocusable(false);
            textField.setFont(new Font("SansSerif", Font.PLAIN, 18));

            // Add action listener to choose button
            chooseBtn.addActionListener(e -> {
                if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    File[] files = fileChooser.getSelectedFiles();
                    StringBuilder sb = new StringBuilder();
                    for (File file : files) {
                        sb.append(file.getName()).append("\n");
                    }
                    textField.setText(sb.toString());
                }
            });

            add(chooseBtn, BorderLayout.NORTH);
            add(new JScrollPane(textField), BorderLayout.CENTER);
        }

        public JFileChooser getFileChooser() {
            return fileChooser;
        }
    }

    public class BtnPanelComp extends JPanel {
        public BtnPanelComp() {
            FlowLayout flowLayout = new FlowLayout(FlowLayout.RIGHT);
            flowLayout.setHgap(10);
            setLayout(flowLayout);
            setBackground(Color.white);
            setBorder(new EmptyBorder(0, 0, 5, 5));

            JButton saveBtn = new JButton("Save");
            saveBtn.setPreferredSize(new Dimension(100, 40));
            saveBtn.addActionListener(AddConsultationWindow.this::saveConsultation);
            add(saveBtn);

            JButton cancelBtn = new JButton("Cancel");
            cancelBtn.setPreferredSize(new Dimension(100, 40));
            cancelBtn.addActionListener(AddConsultationWindow.this::closeWindow);
            add(cancelBtn);
        }
    }

    public void saveConsultation(ActionEvent event) {
        String idStr = (patientIdComp.getJComboBox().getSelectedItem() == null) ? ""
                : patientIdComp.getJComboBox().getSelectedItem().toString();
        String name = patientNameComp.getTxtField().getText();
        String surname = patientSurnameComp.getTxtField().getText();
        String mobile = patientMobileComp.getTxtField().getText();
        String duration = durationCostComp.getDurationTxtField().getText();

        // Check if all required fields are filled.
        LinkedHashMap<String, String> requiredValues = new LinkedHashMap<>() {{
            put("Patient ID", idStr);
            put("Duration", duration);
            put("Patient Name", name);
            put("Patient Surname", surname);
            put("Patient Mobile", mobile);
        }};

        // Iterate each entry of entryset
        // If a required field is empty, show an error message to the user.
        // https://www.programiz.com/java-programming/examples/get-key-from-hashmap-using-value
        for (Map.Entry<String, String> entry : requiredValues.entrySet()) {
            // If give value is equal to value from entry
            if (entry.getValue().isEmpty()) {
                JOptionPane.showMessageDialog(
                        null,
                        entry.getKey() + " field cannot be empty",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }
        }

        // Check if patient id is valid
        int id;
        try {
            id = Integer.parseInt(idStr);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Patient ID must be a number",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        // Check if patient mobile number is valid
        try {
            Integer.parseInt(mobile);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Patient mobile number must be a number",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        // Check if duration is valid
        try {
            Integer.parseInt(duration);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Duration must be a number",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        // Create new patient object
        Patient patient = new Patient(
                name,
                surname,
                patientDobComp.getDatePicker().getDate(),
                mobile,
                id
        );

        // Create new consultation object
        final String consId = getRandID();
        try {
            ArrayList<File> files = encryptImgFiles(consId);
            Consultation consultation = new Consultation(
                    consId,
                    selectedDoctor,
                    patient,
                    selectedDate,
                    selectedTime,
                    Integer.parseInt(duration),
                    Double.parseDouble(durationCostComp.getCostTxtField().getText().replace("£", "").trim()),
                    notesComp.getTextArea().getText(),
                    files
            );

            MainWindow.addConsultation(consultation);

            // Close the window
            closeWindow(event);

            JOptionPane.showMessageDialog(
                    null,
                    "Consultation saved successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setPatient(String patientId) {
        if (patientId.isEmpty()) {
            selectedPatient = null;
            return;
        }

        // If the id is not an integer return
        int id;
        try {
            id = Integer.parseInt(patientId);
        } catch (Exception e) {
            return;
        }

        // Find previous consultation of the selected patient
        Consultation consultation = consultations.stream()
                .filter(c -> c.getPatient().getPatientId() == id)
                .findFirst()
                .orElse(null);

        // Fill the required fields with the values found in the previous consultation
        if (consultation != null) {
            selectedPatient = consultation.getPatient();
            patientNameComp.getTxtField().setText(selectedPatient.getName());
            patientSurnameComp.getTxtField().setText(selectedPatient.getSurname());
            patientDobComp.getDatePicker().setDate(selectedPatient.getDob());
            patientMobileComp.getTxtField().setText(selectedPatient.getMobiNo());
        } else {
            selectedPatient = null;
            patientNameComp.getTxtField().setText("");
            patientSurnameComp.getTxtField().setText("");
            patientDobComp.getDatePicker().setDate(null);
            patientMobileComp.getTxtField().setText("");
        }
    }

    public String getRandID() {
        // Generate a random id for the consultation
        UUID uuid = UUID.randomUUID();
        return uuid.toString().substring(0, 8);
    }

    public ArrayList<File> encryptImgFiles(String consultationId) throws NoSuchAlgorithmException, InvalidKeySpecException {
        File[] files = fileChooserComp.getFileChooser().getSelectedFiles();

        if (files.length == 0) {
            return null;
        }

        final String DEST = "patient_img" + "/" + consultationId;
        // Create the directories if they don't exist
        try {
            Files.createDirectories(Paths.get(DEST));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ArrayList<File> encryptedFiles = new ArrayList<>();
        String randFileName;
        String extension;
        Path dest;
        Key KEY = PasswordBasedKeyAES.getKey();
        for (File f : files) {
            randFileName = getRandID();
            extension = getFileExtension(f.getName());
            dest = Paths.get(DEST, randFileName + extension);

            try {
                Cipher cipher = Cipher.getInstance("AES");
                cipher.init(Cipher.ENCRYPT_MODE, KEY);

                FileInputStream inputStream = new FileInputStream(f);
                File outputFile = new File(dest.toUri());
                FileOutputStream outputStream = new FileOutputStream(outputFile);
                byte[] buffer = new byte[64];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    byte[] output = cipher.update(buffer, 0, bytesRead);
                    if (output != null) {
                        outputStream.write(output);
                    }
                }
                byte[] outputBytes = cipher.doFinal();
                if (outputBytes != null) {
                    outputStream.write(outputBytes);
                }
                inputStream.close();
                outputStream.close();
                encryptedFiles.add(outputFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        fileChooserComp.getFileChooser().setSelectedFiles(null);
        return encryptedFiles;
    }

    public static String getFileExtension(String fileName) {
        String extension = "";
        int index = fileName.lastIndexOf('.');
        if (index > 0) {
            extension = fileName.substring(index + 1);
        }
        return "." + extension;
    }

    public void updateCost() {
        int hours;
        int cost;

        // If the hours is not an integer return
        try {
            hours = !durationCostComp.getDurationTxtField().getText().isEmpty() ?
                    Integer.parseInt(durationCostComp.getDurationTxtField().getText()) : 0;
        } catch (Exception e) {
            return;
        }

        // Calculate and set cost
        if (selectedPatient == null) {
            cost = hours * 15;  // £15 per hour for the first consultation
        } else {
            cost = hours * 25;  // £25 per hour for the next consultations
        }
        durationCostComp.getCostTxtField().setText(df.format(cost));
    }

    public void closeWindow(ActionEvent e) {
        // Close the window
        JButton source = (JButton) e.getSource();
        Container topLevelContainer = source.getTopLevelAncestor();
        if (topLevelContainer instanceof JFrame) {
            ((JFrame) topLevelContainer).dispose();
        } else if (topLevelContainer instanceof JDialog) {
            ((JDialog) topLevelContainer).dispose();
        }
    }

}
