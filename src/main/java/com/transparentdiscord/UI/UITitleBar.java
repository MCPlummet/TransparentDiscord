package com.transparentdiscord.UI;

import com.transparentdiscord.Main;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by liam on 7/10/17.
 * Represents a window bar of sorts, as most windows have no border
 */
public class UITitleBar extends JPanel {

    public UITitleBar(String titleText, JFrame parent) {
        setBackground(Color.decode("#7289DA"));
        setLayout(new BorderLayout());

        JLabel title = new JLabel(titleText);
        title.setBorder(new EmptyBorder(20,20,20,20));
        add(title, BorderLayout.WEST);

        JLabel closeButton = new JLabel("X");
        closeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                parent.setVisible(false);
            }
        });
        closeButton.setBorder(new EmptyBorder(20,20,20,20));
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        add(closeButton, BorderLayout.EAST);
    }


    public UITitleBar() {
        setBackground(Color.decode("#7289DA"));
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Transparent Discord");
        title.setBorder(new EmptyBorder(20,20,20,20));
        add(title, BorderLayout.WEST);

        JLabel closeButton = new JLabel("X");
        closeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                Main.bubbleWindow.dispose();
                System.exit(0);
            }
        });
        closeButton.setBorder(new EmptyBorder(20,20,20,20));
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        add(closeButton, BorderLayout.EAST);
    }

}
