package com.transparentdiscord.UI;

import com.transparentdiscord.Main;
import net.dv8tion.jda.core.entities.Message;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.out;

/**
 * Created by liam on 6/23/17.
 * Represents a single message as it is to be displayed to the user
 */
public class UIMessage extends JPanel {

    private Message message;    //The message to be displayed
    private JLabel authorIcon;      //A JLabel containing the icon of the message sender
    private JLabel authorName;      //A JLabel containing the name of the message sender
    //TODO make message text wrap
    private JTextArea messageText; //A JLabel containing the message text
    private JLabel timestamp;   //A JLabel containing the time the message was sent
    private JPanel attachments;//A JPanel containing any message attachments
    //TODO add support for images, call history, files, message reactions, profile images
    //TODO implement message coalescing to combine messages sent in succession by the same user

    /**
     * Construct a UIMessage from a given message
     * @param message the Message to convert to a UI element
     */
    public UIMessage(Message message) {
        this.message = message;

        setLayout(new BorderLayout());

        attachments = new JPanel(); //May need to sort out layout
        for (Message.Attachment a : message.getAttachments()) {
            if (a.isImage()) {
                try {
                    //Get the image from the URL and resize it to the width of the chat window
                    ImageIcon image = Main.getImageFromURL(new URL(a.getUrl()));

                    //If the image is animated, we can't use smooth scaling
                    if (a.getUrl().contains(".gif")) image = Main.resizeToWidthAnimated(image, Main.getChatWidth()-30);
                    else image = Main.resizeToWidth(image, Main.getChatWidth()-30);
                    
                    JLabel label = new JLabel(image);
                    image.setImageObserver(this);
                    attachments.add(label);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else {
                JLabel label = new JLabel(a.getFileName());
                label.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        //TODO add file download prompt
                    }
                });
                attachments.add(label);
            }
        }

        authorIcon = new JLabel(Main.getImage(message.getAuthor(),25,25));
        authorIcon.setBorder(new EmptyBorder(10,10,10,10));             //Add a buffer around the authorIcon name
        authorName = new JLabel(message.getAuthor().getName());
        messageText = new JTextArea(message.getContent());
        messageText.setFont(Main.defaultFont.deriveFont(Font.PLAIN, 12));
        messageText.setLineWrap(true);
        messageText.setWrapStyleWord(true);
        messageText.setOpaque(false);
        messageText.setEditable(false);
//      messageText.setBorder(new MatteBorder(0,1,0,0,Color.GRAY));
        timestamp = new JLabel(message.getCreationTime().getHour() + ":" + message.getCreationTime().getMinute());
        timestamp.setBorder(new EmptyBorder(10,10,10,10));          //Add a buffer around the timestamp

        setBorder(new MatteBorder(0,0,1,0,Color.GRAY));             //Add a line at the bottom of the message

        add(attachments, BorderLayout.NORTH);
        add(authorIcon, BorderLayout.WEST);
        add(authorName, BorderLayout.SOUTH);
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
