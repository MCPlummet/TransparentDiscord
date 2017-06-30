package com.transparentdiscord.UI;

import com.transparentdiscord.Main;
import net.dv8tion.jda.core.entities.Message;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liam on 6/23/17.
 * Represents a single message as it is to be displayed to the user
 */
public class UIMessage extends JPanel {

    private Message message;    //The message to be displayed
    private JLabel author;      //A JLabel containing the name of the message sender
    //TODO make message text wrap
    private JLabel messageText; //A JLabel containing the message text
    private JLabel timestamp;   //A JLabel containing the time the message was sent
    //TODO add support for images, call history, files, message reactions, profile images
    //TODO implement message coalescing to combine messages sent in succession by the same user

    /**
     * Construct a UIMessage from a given message
     * @param message the Message to convert to a UI element
     */
    public UIMessage(Message message) {
        this.message = message;

        setLayout(new BorderLayout());
        author = new JLabel(Main.getImage(message.getAuthor(),20,20));
        author.setText(message.getAuthor().getName());
        author.setHorizontalTextPosition(JLabel.CENTER);
        author.setVerticalTextPosition(JLabel.BOTTOM);
        author.setBorder(new EmptyBorder(10,10,10,10));             //Add a buffer around the author name
        messageText = new JLabel(message.getContent());
//      messageText.setBorder(new MatteBorder(0,1,0,0,Color.GRAY));
        timestamp = new JLabel(message.getCreationTime().getHour() + ":" + message.getCreationTime().getMinute());
        timestamp.setBorder(new EmptyBorder(10,10,10,10));          //Add a buffer around the timestamp

        setBorder(new MatteBorder(0,0,1,0,Color.GRAY));             //Add a line at the bottom of the message

        add(author, BorderLayout.WEST);
        add(messageText, BorderLayout.CENTER);
        add(timestamp, BorderLayout.EAST);
    }

    /**
     * Batch load a list of UIMessage components from a list of Messages
     * @param messages a list of messages to turn into UIMessages
     * @return a list of UIMessages built from the list of Messages
     */
    public static List<UIMessage> loadMessages(List<Message> messages) {
        ArrayList<UIMessage> list = new ArrayList<>();
        for (Message m : messages)
            list.add(new UIMessage(m));
        return list;
    }

}
