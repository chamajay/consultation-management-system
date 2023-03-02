/*
 * Copyright (c) 2023 Chamath Jayasena
 * Westminster Skin Consultation Centre
 * OOP (L5) CW
 * UoW ID - w1898955
 * IIT ID - 20211387
 */

package com.cj.wscc.gui;

import javax.crypto.Cipher;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.security.Key;
import java.util.ArrayList;
import java.util.LinkedHashMap;

// https://www.geeksforgeeks.org/swingworker-in-java/
public class DecryptWorker extends SwingWorker<Void, Void> {
    private final ArrayList<File> encryptedImgs;
    private LinkedHashMap<BufferedImage, Image> decryptedImgs = new LinkedHashMap<>();
    private final ViewConsultationWindow.ImageViewerComp imageViewPanel;

    public DecryptWorker(ArrayList<File> encryptedImgs, ViewConsultationWindow.ImageViewerComp imageViewPanel) {
        this.encryptedImgs = encryptedImgs;
        this.imageViewPanel = imageViewPanel;
    }

    @Override
    protected Void doInBackground() {
        // Show the progress bar component
        ProgressBarComp progressBarComp = new ProgressBarComp();
        progressBarComp.setVisible(true);

        int currentFile = 0;
        int totalFiles = encryptedImgs.size();
        for (File f : encryptedImgs) {
            if (f.exists()) {
                // Get decrypted original image
                BufferedImage originalImg = getDecryptedImg(f);
                // Get a scaled image of the original image to show as a thumbnail
                Image scaledImage = getScaledImg(originalImg, 450);
                // Put both images in the linked hashmap
                decryptedImgs.put(originalImg, scaledImage);
                // update progress bar after every decryption
                progressBarComp.getProgressBar().setValue((int) (((double) currentFile / totalFiles) * 100));
                ++currentFile;
            }
        }
        // Close the progress bar window
        progressBarComp.dispose();
        return null;
    }

    @Override
    protected void done() {
        try {
            // Set the images of the imageview panel after decryption
            imageViewPanel.setImages(decryptedImgs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Image getScaledImg(BufferedImage img, int width) {
        int originalWidth = img.getWidth();
        int originalHeight = img.getHeight();
        // To keep the original ratio, use a scalefactor
        double scaleFactor = (double) width / originalWidth;
        int desiredHeight = (int) (originalHeight * scaleFactor);
        // https://www.baeldung.com/java-resize-image
        return img.getScaledInstance(width, desiredHeight, Image.SCALE_SMOOTH);
    }

    public BufferedImage getDecryptedImg(File file) {
        BufferedImage decryptedImg = null;
        // Decrypt the image file
        // https://www.baeldung.com/java-aes-encryption-decryption
        try {
            Key KEY = PasswordBasedKeyAES.getKey();
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, KEY);
            FileInputStream inputStream = new FileInputStream(file);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
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
            ByteArrayInputStream decryptedInputStream = new ByteArrayInputStream(outputStream.toByteArray());
            decryptedImg = ImageIO.read(decryptedInputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return decryptedImg;
    }

    public static class ProgressBarComp extends JFrame {
        private final JProgressBar progressBar;

        public ProgressBarComp() {
            super("Decrypting");
            setResizable(false);
            setLocationRelativeTo(null);

            JPanel panel = new JPanel(new GridBagLayout());
            panel.setBorder(new EmptyBorder(20, 20, 20, 20));
            GridBagConstraints c = new GridBagConstraints();
            c.fill = GridBagConstraints.BOTH;
            c.weighty = 1;
            c.weightx = 1;
            c.gridx = 0;
            c.gridy = 0;
            c.insets = new Insets(0, 0, 10, 0);  // Set the vertical gap to 10 pixels

            JLabel text = new JLabel("Decrypting image files...");
            text.setFont(new Font("SansSerif", Font.PLAIN, 15));
            text.setHorizontalAlignment(JLabel.CENTER);
            panel.add(text, c);

            progressBar = new JProgressBar();
            progressBar.setMinimum(0);
            progressBar.setMaximum(100);
            progressBar.setPreferredSize(new Dimension(300, 30));
            c.gridx = 0;
            c.gridy = 1;
            panel.add(progressBar, c);

            add(panel, BorderLayout.CENTER);
            pack();
        }

        public JProgressBar getProgressBar() {
            return progressBar;
        }
    }

}
