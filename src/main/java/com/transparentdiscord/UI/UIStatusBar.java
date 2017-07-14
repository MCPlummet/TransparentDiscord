package com.transparentdiscord.UI;

import com.transparentdiscord.Main;
import net.dv8tion.jda.core.OnlineStatus;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by liam on 7/12/17.
 * Represents the
 */
public class UIStatusBar extends JPanel {

    private JLabel icon;
    private JComboBox<OnlineStatus> status;
    private JLabel newChat;

    public UIStatusBar(ImageIcon image) {
        setLayout(new BorderLayout());

        setBackground(Color.decode("#99AAB5"));

        icon = new JLabel(Main.resizeToWidth(image,25));

        //Create a list of valid statuses; the user should not set their status to offline or unknown
        OnlineStatus[] validStatuses = {OnlineStatus.ONLINE, OnlineStatus.IDLE, OnlineStatus.DO_NOT_DISTURB, OnlineStatus.INVISIBLE};

        status = new JComboBox<OnlineStatus>(validStatuses);
        newChat = new JLabel("Friends");

        icon.setBorder(new EmptyBorder(10,10,10,10));
        status.setBorder(new EmptyBorder(10,10,10,10));
        newChat.setBorder(new EmptyBorder(10,10,10,10));

        newChat.setForeground(Color.WHITE);
        status.setBackground(Color.decode("#99AAB5"));
        status.setForeground(Color.WHITE);

        status.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                Main.setStatus(status.getItemAt(status.getSelectedIndex()));
            }
        });

        newChat.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                Main.friendWindow.setVisible(true);
            }
        });

        newChat.setCursor(new Cursor(Cursor.HAND_CURSOR));

        add(icon, BorderLayout.WEST);
        add(status, BorderLayout.CENTER);
        add(newChat, BorderLayout.EAST);
    }

}
