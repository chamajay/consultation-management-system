/*
 * Copyright (c) 2023 Chamath Jayasena
 * Westminster Skin Consultation Centre
 * OOP (L5) CW
 * UoW ID - w1898955
 * IIT ID - 20211387
 */

package com.cj.wscc.gui;

import javax.swing.*;
import java.awt.*;

public class FullImageViewerWindow extends JFrame {
    public FullImageViewerWindow(Image img) {
        super("Full Image Viewer");

        // Get the dimensions of the screen
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;

        // Scale the image to fit within the screen dimensions if the image is too large
        int width = img.getWidth(null);
        int height = img.getHeight(null);
        if (width > screenWidth || height > screenHeight) {
            double scaleFactor = Math.min((double) screenWidth / width, (double) screenHeight / height);
            width = (int) (width * scaleFactor / 1.2);
            height = (int) (height * scaleFactor / 1.2);
            img = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        }

        // Set the frame size as the scaled image size
        setSize(img.getWidth(null), img.getHeight(null));
        setLocationRelativeTo(null);
        setResizable(false);

        JLabel jLabel = new JLabel();
        jLabel.setIcon(new ImageIcon(img));

        // Add the JLabel to a JScrollPane
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(jLabel);
        scrollPane.getViewport().setBackground(Color.white);
        add(scrollPane);
    }
}
