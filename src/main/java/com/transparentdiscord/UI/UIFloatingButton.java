package com.transparentdiscord.UI;

import com.transparentdiscord.Main;
import net.dv8tion.jda.client.entities.Group;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import javax.swing.*;

import static java.lang.System.out;

/**
 * Created by liam on 6/22/17.
 * Represents the bubbles used to open different UI elements
 */
public class UIFloatingButton extends JPanel {

    private Point initialClick;     //Keeps track of the mouse position for use in window dragging


    /**
     * Constructs a UIFloatingButton (bubble) that shows and hides a child JFrame when clicked
     * and moves a parent JFrame when dragged
     * @param parent the JFrame that this bubble should move the position of
     * @param child the JFrame that this bubble should show and hide when clicked
     */
    public UIFloatingButton(JFrame parent, JFrame child) {

        setBackground(new Color(0,0,0,0));  //Make it so only the bubble is visible

//        setLayout(new BorderLayout());
        JLabel title = new JLabel(Main.getScaledImageFromFile(getClass().getResource("/images/Discordbubble256.png"),50,50));
        title.setForeground(Color.black);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) { //show and hide the child when clicked
                Main.chatWindow.setVisible(false);
                if (!child.isVisible())
                    child.setVisible(true);
                else
                    child.setVisible(false);
                parent.setBackground(new Color(0,0,0,0));
            }
            @Override
            public void mousePressed(MouseEvent mouseEvent) { //when pressed, record the location of the mouse
                initialClick = mouseEvent.getPoint();
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent mouseEvent) { //when dragged, move the parent by the amount dragged
                int currentX = parent.getLocation().x;
                int currentY = parent.getLocation().y;

                int dX = currentX + mouseEvent.getX() - (currentX + initialClick.x);
                int dY = currentY + mouseEvent.getY() - (currentY + initialClick.y);

                parent.setLocation(currentX + dX, currentY + dY);
                Main.repositionWindows();
            }
        });

        add(title);
        setSize(50,50);
        setCursor(new Cursor(Cursor.HAND_CURSOR));  //Indicate to the user that this element is clickable
    }

    /**
     * Constructs a UIFloatingButton (bubble) that shows a chat window when clicked
     * @param channel
     */
    public UIFloatingButton(MessageChannel channel) {
        setBackground(new Color(0,0,0,0));

        Image i = Main.getImage(channel).getImage();
        i = i.getScaledInstance(50,50,Image.SCALE_SMOOTH);
        JLabel image = new JLabel(new ImageIcon(i));
        image.setSize(50,50);
        add(image);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                Main.openChat(channel);
            }
        });

        setSize(50,50);
        if (channel instanceof TextChannel) {
            TextChannel tc = (TextChannel) channel;
            setToolTipText(tc.getGuild().getName() + " #" + channel.getName());
        } else if (channel instanceof Group) {
            Group group = (Group) channel;

            StringBuilder name = new StringBuilder();
            if (group.getName() == null) {
                for (User user : group.getUsers())
                    name.append(user.getName() + ", ");
                name.deleteCharAt(name.length()-1);
                name.deleteCharAt(name.length()-1);
            } else {
                name.append(group.getName());
            }

            setToolTipText(name.toString());
        } else {
            setToolTipText(channel.getName());  //Set the hover text to the name of the channel
        }
        setCursor(new Cursor(Cursor.HAND_CURSOR));  //Indicate to the user that this element is clickable
    }
}