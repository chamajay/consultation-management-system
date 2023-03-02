/*
 * Copyright (c) 2023 Chamath Jayasena
 * Westminster Skin Consultation Centre
 * OOP (L5) CW
 * UoW ID - w1898955
 * IIT ID - 20211387
 */

package com.cj.wscc.gui;

import com.cj.wscc.console.Consultation;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ViewConsultationWindow extends JFrame {
    private final Consultation selectedConsultation;
    private final DecimalFormat df = new DecimalFormat("0.00");

    public ViewConsultationWindow(Consultation selectedConsultation) {
        super("View Consultation");
        setSize(900, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        this.selectedConsultation = selectedConsultation;

        // Add top panel
        TopPanelComp topPanelComp = new TopPanelComp();
        add(topPanelComp, BorderLayout.NORTH);

        // Main panel
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 5));
        mainPanel.setBackground(Color.white);

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 1;
        c.weightx = 1;
        c.gridx = 0;
        c.gridy = 0;

        // Add doctor component
        MyLabelComp doctorComp = new MyLabelComp(
                "Doctor",
                "Dr. " + selectedConsultation.getDoctor().getFullName()
        );
        mainPanel.add(doctorComp, c);

        MyLabelComp dateTimeComp = new MyLabelComp(
                "Date & Time",
                selectedConsultation.getDate() + " " + selectedConsultation.getTime()
        );
        c.gridx = 0;
        c.gridy = 1;
        mainPanel.add(dateTimeComp, c);

        // Add patient id component
        MyLabelComp patientIdComp = new MyLabelComp(
                "Patient ID",
                Integer.toString(selectedConsultation.getPatient().getPatientId())
        );
        c.gridx = 0;
        c.gridy = 2;
        mainPanel.add(patientIdComp, c);

        // Add patient name component
        MyLabelComp patientNameComp = new MyLabelComp(
                "Patient Name",
                selectedConsultation.getPatient().getName()
        );
        c.gridx = 0;
        c.gridy = 3;
        mainPanel.add(patientNameComp, c);

        // Add patient surname component
        MyLabelComp patientSurnameComp = new MyLabelComp(
                "Patient Surname",
                selectedConsultation.getPatient().getSurname()
        );
        c.gridx = 0;
        c.gridy = 4;
        mainPanel.add(patientSurnameComp, c);

        // Add patient dob component
        String dob = "";
        if (selectedConsultation.getPatient().getDob() != null) {
            dob = selectedConsultation.getPatient().getDob().toString();
        }
        MyLabelComp patientDobComp = new MyLabelComp("Patient DOB", dob);
        c.gridx = 0;
        c.gridy = 5;
        mainPanel.add(patientDobComp, c);

        // Add patient mobile number component
        MyLabelComp patientMobile = new MyLabelComp(
                "Patient Mobile",
                selectedConsultation.getPatient().getMobiNo()
        );
        c.gridx = 0;
        c.gridy = 6;
        mainPanel.add(patientMobile, c);

        add(mainPanel, BorderLayout.CENTER);

        DurationCostComp durationCostComp = new DurationCostComp(
                Integer.toString(selectedConsultation.getDurationHours()),
                df.format(selectedConsultation.getCost())
        );
        c.gridx = 0;
        c.gridy = 7;
        mainPanel.add(durationCostComp, c);

        // Right panel
        RightPanelComp rightPanelComp = new RightPanelComp();

        // Add notes component
        NotesComp notesComp = new NotesComp(selectedConsultation.getNotes());
        c.weighty = 0.4;
        c.gridx = 0;
        c.gridy = 0;
        rightPanelComp.add(notesComp, c);

        // Add image viewer component
        ImageViewerComp imageViewerComp = new ImageViewerComp();
        c.weighty = 1;
        c.gridx = 0;
        c.gridy = 1;

        // Run decryptworker if there are images
        if (selectedConsultation.getImageFiles() != null &&
                selectedConsultation.getImageFiles().size() > 0) {
            DecryptWorker worker = new DecryptWorker(selectedConsultation.getImageFiles(), imageViewerComp);
            worker.execute();
        }
        rightPanelComp.add(imageViewerComp, c);

        add(rightPanelComp, BorderLayout.EAST);
    }

    public class TopPanelComp extends JPanel {
        public TopPanelComp() {
            setLayout(new BorderLayout());
            setBackground(new Color(0x3C4048));
            setPreferredSize(new Dimension(0, 50));
            setLayout(new BorderLayout());

            JLabel topLabel = new JLabel(
                    "Consultation " + selectedConsultation.getId(),
                    SwingConstants.CENTER
            );
            topLabel.setFont(new Font("SansSerif", Font.PLAIN, 25));
            topLabel.setForeground(Color.white);

            add(topLabel, BorderLayout.CENTER);
        }
    }

    public class ImageViewerComp extends JPanel {
        private final JLabel imageLabel;
        private LinkedHashMap<BufferedImage, Image> images = new LinkedHashMap<>();
        private int imgNo = 0;

        public ImageViewerComp() {
            int noOfImgs = selectedConsultation.getImageFiles() == null ? 0 : selectedConsultation.getImageFiles().size();
            String title = String.format("Images (%d)", noOfImgs);
            setBorder(new CompoundBorder(BorderFactory.createTitledBorder(title), new EmptyBorder(5, 5, 5, 5)));
            setBackground(Color.white);

            imageLabel = new JLabel();
            imageLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        FullImageViewerWindow fullImageViewerWindow = new FullImageViewerWindow(getOriginalImageAt(imgNo));
                        fullImageViewerWindow.setVisible(true);
                    }
                }
            });

            // Container panel
            JPanel container = new JPanel(new BorderLayout(0, 5));
            container.setBackground(Color.white);

            // Add the JLabel to a JScrollPane
            JScrollPane scrollPane = new JScrollPane();
            scrollPane.setPreferredSize(new Dimension(450, 380));
            scrollPane.setViewportView(imageLabel);
            scrollPane.getViewport().setBackground(Color.white);
            container.add(scrollPane, BorderLayout.CENTER);

            JPanel btnContainer = new JPanel(new FlowLayout());
            btnContainer.setBackground(Color.white);
            JButton prevBtn = new JButton("Previous");
            prevBtn.addActionListener(actionEvent -> {
                if (images != null && images.size() > 0) {
                    if (imgNo == 0) {
                        imgNo = images.size() - 1;
                    } else {
                        --imgNo;
                    }
                    imageLabel.setIcon(new ImageIcon(getScaledImageAt(imgNo)));
                }
            });
            JButton nextBtn = new JButton("Next");
            nextBtn.addActionListener(actionEvent -> {
                if (images != null && images.size() > 0) {
                    if (imgNo == images.size() - 1) {
                        imgNo = 0;
                    } else {
                        ++imgNo;
                    }
                    imageLabel.setIcon(new ImageIcon(getScaledImageAt(imgNo)));
                }
            });
            btnContainer.add(prevBtn);
            btnContainer.add(nextBtn);
            container.add(btnContainer, BorderLayout.SOUTH);

            add(container);
        }

        public void setImages(LinkedHashMap<BufferedImage, Image> images) {
            this.images = images;
            imageLabel.setIcon(new ImageIcon(getScaledImageAt(0)));
        }

        public Image getScaledImageAt(int position) {
            // Get the value at the second position
            int i = 0;
            HashMap<Object, Object> map;
            for (Map.Entry<BufferedImage, Image> entry : images.entrySet()) {
                if (i == position) {
                    return entry.getValue();
                }
                i++;
            }
            return null;
        }

        public Image getOriginalImageAt(int position) {
            // Get the value at the second position
            int i = 0;
            HashMap<Object, Object> map;
            for (Map.Entry<BufferedImage, Image> entry : images.entrySet()) {
                if (i == position) {
                    return entry.getKey();
                }
                i++;
            }
            return null;
        }
    }

    public class RightPanelComp extends JPanel {
        public RightPanelComp() {
            setLayout(new GridBagLayout());
            setPreferredSize(new Dimension(500, 0));
            setBorder(new EmptyBorder(10, 5, 10, 10));
            setBackground(Color.white);
        }
    }

    public class NotesComp extends JPanel {
        private final JTextArea textArea;

        public NotesComp(String txt) {
            setLayout(new BorderLayout());
            setBorder(new CompoundBorder(BorderFactory.createTitledBorder("Notes"), new EmptyBorder(0, 5, 5, 5)));
            setBackground(Color.white);

            textArea = new JTextArea(txt);
            textArea.setRows(4);
            textArea.setFont(new Font("SansSerif", Font.PLAIN, 20));
            textArea.setEditable(false);
            textArea.setFocusable(false);
            textArea.setBackground(Color.white);
            add(new JScrollPane(textArea), BorderLayout.CENTER);
        }

        public JTextArea getTextArea() {
            return textArea;
        }
    }

    public static class MyLabelComp extends JPanel {
        private final JLabel myLabel;

        public MyLabelComp(String title, String label) {
            setLayout(new BorderLayout());
            setBorder(new CompoundBorder(BorderFactory.createTitledBorder(title), new EmptyBorder(0, 5, 5, 5)));
            setBackground(Color.white);

            myLabel = new JLabel(label);
            myLabel.setFont(new Font("SansSerif", Font.PLAIN, 20));
            add(myLabel);
        }
    }

    public class DurationCostComp extends JPanel {
        JLabel durationLabel;
        JLabel costLabel;

        public DurationCostComp(String duration, String cost) {
            setLayout(new GridBagLayout());
            setBackground(Color.white);

            GridBagConstraints c = new GridBagConstraints();
            c.weightx = 0.5;
            c.weighty = 0.5;
            c.fill = GridBagConstraints.BOTH;

            JPanel durationContainer = new JPanel(new BorderLayout());
            durationContainer.setBackground(Color.white);
            durationContainer.setBorder(new CompoundBorder(BorderFactory.createTitledBorder("Duration (h)"), new EmptyBorder(0, 5, 5, 5)));

            durationLabel = new JLabel(duration);
            durationLabel.setFont(new Font("SansSerif", Font.PLAIN, 20));
            durationContainer.add(durationLabel);
            add(durationContainer, c);

            JPanel costContainer = new JPanel(new BorderLayout());
            costContainer.setBackground(Color.white);

            costContainer.setBorder(new CompoundBorder(BorderFactory.createTitledBorder("Cost (£)"), new EmptyBorder(0, 5, 5, 5)));
            costLabel = new JLabel();
            costLabel.setText(cost);
            costLabel.setFont(new Font("SansSerif", Font.PLAIN, 20));
            costContainer.add(costLabel);
            add(costContainer, c);
        }
    }

}
