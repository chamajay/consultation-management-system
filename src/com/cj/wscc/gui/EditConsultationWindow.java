/*
 * Copyright (c) 2023 Chamath Jayasena
 * Westminster Skin Consultation Centre
 * OOP (L5) CW
 * UoW ID - w1898955
 * IIT ID - 20211387
 */

package com.cj.wscc.gui;

import com.cj.wscc.console.Consultation;
import com.cj.wscc.console.Patient;
import com.github.lgooddatepicker.components.DatePicker;

import javax.crypto.Cipher;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class EditConsultationWindow extends JFrame {
    private ArrayList<Consultation> consultations;
    private Consultation selectedConsultation;
    private MyPatientIdComp patientIdComp;
    private DurationCostComp durationCostComp;
    private MyTxtFieldComp patientNameComp;
    private MyTxtFieldComp patientSurnameComp;
    private MyDOBComp patientDobComp;
    private MyTxtFieldComp patientMobileComp;
    private NotesComp notesComp;
    private FileChooserComp fileChooserComp;
    private Patient selectedPatient;
    private final DecimalFormat df = new DecimalFormat("0.00");

    public EditConsultationWindow(ArrayList<Consultation> consultations, Consultation selectedConsultation) {
        super("Edit Consultation");
        setSize(900, 700);
        setLocationRelativeTo(null);  // Show the frame in the middle of the screen
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        this.consultations = consultations;
        this.selectedConsultation = selectedConsultation;
        this.selectedPatient = selectedConsultation.getPatient();

        // Add top panel to the frame
        add(new TopPanelComp(), BorderLayout.NORTH);

        // Center panel
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[]{50};
        JPanel centerPanel = new JPanel(gridBagLayout);
        centerPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        centerPanel.setBackground(new Color(0xFFFFFF));

        // Add doctor component to the center panel
        MyTxtFieldComp doctorComp = new MyTxtFieldComp("Doctor");
        JTextField docTxtField = (JTextField) doctorComp.getComponents()[0];
        docTxtField.setText("Dr. " + selectedConsultation.getDoctor().getFullName());
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

        // Add name and surname components to the center panel
        patientNameComp = new MyTxtFieldComp("Patient Name");
        patientNameComp.getTxtField().setText(selectedConsultation.getPatient().getName());
        patientSurnameComp = new MyTxtFieldComp("Patient Surname");
        patientSurnameComp.getTxtField().setText(selectedConsultation.getPatient().getSurname());
        c.gridx = 0;
        c.gridy = 2;
        centerPanel.add(patientNameComp, c);
        c.gridx = 1;
        c.gridy = 2;
        centerPanel.add(patientSurnameComp, c);

        // Add dob component to the center panel
        c.gridx = 0;
        c.gridy = 3;
        patientDobComp = new MyDOBComp();
        patientDobComp.getDatePicker().setDate(selectedConsultation.getPatient().getDob());
        centerPanel.add(patientDobComp, c);

        // Add mobile component to the center panel
        c.gridx = 1;
        c.gridy = 3;
        patientMobileComp = new MyTxtFieldComp("Patient Mobile");
        patientMobileComp.getTxtField().setText(selectedConsultation.getPatient().getMobiNo());
        centerPanel.add(patientMobileComp, c);

        // Add date component to the center panel
        c.gridx = 1;
        c.gridy = 0;
        JPanel date = new MyTxtFieldComp("Date & Time");
        JTextField dateTxtField = (JTextField) date.getComponents()[0];
        dateTxtField.setText(
                selectedConsultation.getDate().toString() + " " + selectedConsultation.getTime().toString()
        );
        dateTxtField.setEditable(false);
        dateTxtField.setFocusable(false);
        centerPanel.add(date, c);

        // Add duration and cost components to the center panel
        c.gridx = 1;
        c.gridy = 1;
        durationCostComp = new DurationCostComp();
        durationCostComp.getDurationTxtField().setText(selectedConsultation.getDurationHours() + "");
        durationCostComp.getCostTxtField().setText(df.format(selectedConsultation.getCost()));
        centerPanel.add(durationCostComp, c);

        // Add notes component to the center panel
        c.gridx = 0;
        c.gridy = 4;
        notesComp = new NotesComp();
        notesComp.getTextArea().setText(selectedConsultation.getNotes());
        centerPanel.add(notesComp, c);

        // Add file chooser component to the center panel
        c.gridx = 1;
        c.gridy = 4;
        fileChooserComp = new FileChooserComp();
        // Check if the consultation has any image files
        if (selectedConsultation.getImageFiles() != null) {
            File[] files = selectedConsultation.getImageFiles().toArray(new File[0]);
            StringBuilder sb = new StringBuilder();
            for (File file : files) {
                sb.append(file.getName()).append("\n");
            }
            fileChooserComp.getFileChooser().setSelectedFiles(files);
            fileChooserComp.textField.setText(sb.toString());
        }
        centerPanel.add(fileChooserComp, c);

        // Add id component to the center panel
        patientIdComp = new MyPatientIdComp();
        patientIdComp.getJComboBox().setSelectedItem(selectedConsultation.getPatient().getPatientId());
        c.gridx = 0;
        c.gridy = 1;
        centerPanel.add(patientIdComp, c);

        // Add center panel to the frame
        add(centerPanel, BorderLayout.CENTER);

        // Add button panel to the frame
        add(new BtnPanelComp(), BorderLayout.SOUTH);
    }

    public void saveConsultation(ActionEvent event) {
        // Check if all required fields are filled
        String idStr = (patientIdComp.getJComboBox().getSelectedItem() == null) ? ""
                : patientIdComp.getJComboBox().getSelectedItem().toString();
        String name = patientNameComp.getTxtField().getText();
        String surname = patientSurnameComp.getTxtField().getText();
        String mobile = patientMobileComp.getTxtField().getText();
        String duration = durationCostComp.getDurationTxtField().getText();

        LinkedHashMap<String, String> requiredValues = new LinkedHashMap<>() {{
            put("Patient ID", idStr);
            put("Duration", duration);
            put("Patient Name", name);
            put("Patient Surname", surname);
            put("Patient Mobile", mobile);
        }};

        // Iterate each entry of hashmap and check if the value is empty
        for (Map.Entry<String, String> entry : requiredValues.entrySet()) {
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

        // Check whether an existing patient or a new one
        if (selectedPatient == null) {
            Patient patient = new Patient(
                    name,
                    surname,
                    patientDobComp.getDatePicker().getDate(),
                    mobile,
                    id
            );
            selectedConsultation.setPatient(patient);
        } else {
            // Update patient info
            selectedPatient.setPatientId(id);
            selectedPatient.setName(name);
            selectedPatient.setSurname(surname);
            selectedPatient.setDob(patientDobComp.getDatePicker().getDate());
            selectedPatient.setMobiNo(mobile);
            selectedConsultation.setPatient(selectedPatient);
        }

        try {
            ArrayList<File> files = encryptImgFiles(selectedConsultation.getId());
            selectedConsultation.setId(selectedConsultation.getId());
            selectedConsultation.setDoctor(selectedConsultation.getDoctor());
            selectedConsultation.setDate(selectedConsultation.getDate());
            selectedConsultation.setTime(selectedConsultation.getTime());
            selectedConsultation.setDurationHours(Integer.parseInt(durationCostComp.getDurationTxtField().getText()));
            selectedConsultation.setCost(Double.parseDouble(durationCostComp.getCostTxtField().getText().replace("£", "").trim()));
            selectedConsultation.setNotes(notesComp.getTextArea().getText());
            selectedConsultation.setImageFiles(files);
            MainWindow.saveConsultations();
            MainWindow.updateConsTableModel();

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

        // Check if user has selected new files
        if (selectedConsultation.getImageFiles() != null) {
            StringBuilder sb = new StringBuilder();
            for (File file : selectedConsultation.getImageFiles()) {
                sb.append(file.getName()).append("\n");
            }
            if (fileChooserComp.getTextField().getText().equals(sb.toString())) {
                return selectedConsultation.getImageFiles();
            }
        }

        // Remove existing files before adding new ones
        MainWindow.removeConsultationImgFiles(selectedConsultation);

        final String DEST = "patient_img" + "/" + consultationId;
        // Create the directories if they don't exist
        try {
            Files.createDirectories(Paths.get(DEST));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ArrayList<File> newFiles = new ArrayList<>();
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
                newFiles.add(outputFile);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        fileChooserComp.getFileChooser().setSelectedFiles(null);
        return newFiles;
    }

    public String getFileExtension(String fileName) {
        String extension = "";
        int index = fileName.lastIndexOf('.');
        if (index > 0) {
            extension = fileName.substring(index + 1);
        }
        return "." + extension;
    }

    public class MyTxtFieldComp extends JPanel {
        private final JTextField txtField;

        public MyTxtFieldComp(String label) {
            setLayout(new BorderLayout());
            setBorder(new CompoundBorder(BorderFactory.createTitledBorder(label), new EmptyBorder(0, 5, 5, 5)));
            setBackground(new Color(0xFFFFFF));

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

        public MyPatientIdComp() {
            setLayout(new BorderLayout());
            setBorder(new CompoundBorder(BorderFactory.createTitledBorder("Patient ID"), new EmptyBorder(0, 5, 5, 5)));
            setBackground(Color.white);

            // Get existing patient ids
            ArrayList<Integer> suggestions = new ArrayList<>();
            for (Consultation c : consultations) {
                suggestions.add(c.getPatient().getPatientId());
            }

            comboBox = new JComboBox(suggestions.toArray());
            comboBox.setEditable(true);
            comboBox.setSelectedItem(null);
            comboBox.setFont(new Font("SansSerif", Font.PLAIN, 20));
            // Add a document listener to the text field of combobox
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

        try {
            if (selectedPatient != null) {
                // If only one consultation which is the editing one, 15 else 25
                long count = consultations.stream()
                        .filter(c -> c.getPatient().getPatientId() == selectedPatient.getPatientId())
                        .count();
                cost = count > 1 ? hours * 25 : hours * 15;
            } else {
                cost = hours * 15;
            }
            durationCostComp.getCostTxtField().setText(df.format(cost));
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

        // Check if the patient has another consultation
        Consultation consultation = consultations.stream()
                .filter(c -> c.getPatient().getPatientId() == id)
                .findFirst()
                .orElse(null);
        // If the patient has another consultation
        if (consultation != null) {
            selectedPatient = consultation.getPatient();
        } else {
            selectedPatient = null;
        }
    }

    public class MyDOBComp extends JPanel {
        DatePicker datePicker;

        public MyDOBComp() {
            setLayout(new BorderLayout());
            setBorder(new CompoundBorder(BorderFactory.createTitledBorder("Patient DOB"), new EmptyBorder(0, 5, 5, 5)));
            setBackground(Color.white);

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

            JPanel durationContainer = new JPanel(new BorderLayout());
            durationContainer.setBackground(Color.white);
            durationContainer.setBorder(new CompoundBorder(BorderFactory.createTitledBorder("Duration (h)"), new EmptyBorder(0, 5, 5, 5)));
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

            JPanel costContainer = new JPanel(new BorderLayout());
            costContainer.setBackground(Color.white);
            costContainer.setBorder(new CompoundBorder(BorderFactory.createTitledBorder("Cost (£)"), new EmptyBorder(0, 5, 5, 5)));
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

    public class NotesComp extends JPanel {
        private final JTextArea textArea;

        public NotesComp() {
            setLayout(new BorderLayout());
            setBorder(new CompoundBorder(BorderFactory.createTitledBorder("Notes"), new EmptyBorder(0, 5, 5, 5)));
            setBackground(Color.white);

            textArea = new JTextArea();
            textArea.setRows(4);
            textArea.setFont(new Font("SansSerif", Font.PLAIN, 20));
            add(new JScrollPane(textArea), BorderLayout.CENTER);
        }

        public JTextArea getTextArea() {
            return textArea;
        }
    }

    public class TopPanelComp extends JPanel {
        public TopPanelComp() {
            setLayout(new BorderLayout());
            setBackground(new Color(0x256D85));
            setPreferredSize(new Dimension(0, 50));
            setLayout(new BorderLayout());

            JLabel topLabel = new JLabel(
                    "Edit Consultation " + selectedConsultation.getId(),
                    SwingConstants.CENTER
            );
            topLabel.setFont(new Font("SansSerif", Font.PLAIN, 25));
            topLabel.setForeground(Color.white);
            add(topLabel, BorderLayout.CENTER);
        }
    }

    public class FileChooserComp extends JPanel {
        private final JFileChooser fileChooser;
        private final JTextArea textField;

        public FileChooserComp() {
            setLayout(new BorderLayout());
            setBorder(new CompoundBorder(BorderFactory.createTitledBorder("Add Images"), new EmptyBorder(0, 5, 5, 5)));
            setBackground(Color.white);

            fileChooser = new JFileChooser();
            fileChooser.setMultiSelectionEnabled(true);
            fileChooser.setPreferredSize(new Dimension(1000, 500));
            fileChooser.setAcceptAllFileFilterUsed(false);
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

        public JTextArea getTextField() {
            return textField;
        }
    }

    public class BtnPanelComp extends JPanel {
        public BtnPanelComp() {
            FlowLayout flowLayout = new FlowLayout(FlowLayout.RIGHT);
            flowLayout.setHgap(10);
            setLayout(flowLayout);
            setBorder(new EmptyBorder(0, 0, 5, 5));
            setBackground(Color.white);

            JButton saveBtn = new JButton("Save");
            saveBtn.setPreferredSize(new Dimension(100, 40));
            saveBtn.addActionListener(EditConsultationWindow.this::saveConsultation);
            add(saveBtn);

            JButton cancelBtn = new JButton("Cancel");
            cancelBtn.setPreferredSize(new Dimension(100, 40));
            cancelBtn.addActionListener(EditConsultationWindow.this::closeWindow);
            add(cancelBtn);
        }
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
