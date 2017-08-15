package com.transparentdiscord.UI.Message;

import com.transparentdiscord.TransparentDiscord;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import org.apache.commons.lang3.text.WordUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;

import static java.lang.System.out;

/**
 * Created by liam on 7/27/17.
 */
public class UIMessageGroup extends JPanel {

    private User user;
    private HashMap<String, UIMessageGroupText> messages;
    private OffsetDateTime mostRecentMessageTime;
    private OffsetDateTime oldestMessageTime;

    private JPanel messagePanel;

    private JLabel authorIcon;      //A JLabel containing the icon of the message sender
    private JLabel messageInfo;      //A JLabel containing the name of the message sender

    private GridBagConstraints c;

    public UIMessageGroup(Message message) {
        user = message.getAuthor();

        messages = new HashMap<>();
        UIMessageGroupText messageComponent = new UIMessageGroupText(message, this);
        messages.put(message.getId(), messageComponent);

        setMaximumSize(new Dimension(TransparentDiscord.UI_WIDTH, Integer.MAX_VALUE));

        mostRecentMessageTime   = message.getCreationTime();
        oldestMessageTime       = message.getCreationTime();

        setLayout(new BorderLayout());

        //Set up gridbag to add new messages20.

        c = new GridBagConstraints();
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;

        messagePanel = new JPanel();
        messagePanel.setLayout(new GridBagLayout());
        messagePanel.setBorder(new EmptyBorder(5,5,5,5));

        if (TransparentDiscord.isSelfUser(user)) {
            messagePanel.setBackground(Color.LIGHT_GRAY);
            setBorder(new EmptyBorder(5,50,5,5));

            messageInfo = new JLabel(getInfo());
            messageInfo.setFont(TransparentDiscord.defaultFont.deriveFont(Font.PLAIN, 12));
            messageInfo.setHorizontalAlignment(JLabel.RIGHT);
            messageInfo.setBorder(new EmptyBorder(0,0,0,5));

            add(messageInfo, BorderLayout.SOUTH);
        }
        else {
            messagePanel.setBackground(Color.WHITE);
            setBorder(new EmptyBorder(5,5,5,5));

            authorIcon = new JLabel(TransparentDiscord.getImage(message.getAuthor(),25,25));
            authorIcon.setBorder(new EmptyBorder(10,10,10,10));
            authorIcon.setVerticalAlignment(JLabel.TOP);

            messageInfo = new JLabel(getInfo());
            messageInfo.setFont(TransparentDiscord.defaultFont.deriveFont(Font.PLAIN, 12));
            messageInfo.setHorizontalAlignment(JLabel.LEFT);
            messageInfo.setBorder(new EmptyBorder(0,45,0,0));

            add(authorIcon, BorderLayout.WEST);
            add(messageInfo, BorderLayout.SOUTH);
        }

        messagePanel.add(new UIMessageGroupText(message, this),c,0);
        if (!message.getAttachments().isEmpty())
            messagePanel.add(new UIMessageGroupAttachment(message),c,0);

       add(messagePanel, BorderLayout.CENTER);
    }

    private String getInfo() {
        ZonedDateTime localTime = mostRecentMessageTime.atZoneSameInstant(ZoneId.systemDefault());
        String time = String.format("%s:%02d", localTime.getHour()%12+1, localTime.getMinute()) + " "
                + ((localTime.getHour()>12) ? "PM" : "AM");

        String month = localTime.getMonth().name().substring(0,3).toLowerCase();
        month = WordUtils.capitalize(month);

        String day = localTime.getDayOfWeek().name().substring(0,3).toLowerCase();
        day = WordUtils.capitalize(day);

        String date = day + ", "
                + month + " "
                + localTime.getDayOfMonth();
        return user.getName() + " \u2022 " + date + " at " + time;
    }

    public boolean canAddMessage(Message message) {
        //If the user is not the same, return false
        if (!message.getAuthor().getId().equals(user.getId())) return false;
        //If the message falls between the messages in this group, return false
//        if (!(message.getCreationTime().isAfter(mostRecentMessageTime) && message.getCreationTime().isBefore(oldestMessageTime))) return false;
        //If the message is newer than the most recent message
        if (message.getCreationTime().isAfter(mostRecentMessageTime))
            //If the time between the given message and the most recent message in this group is greater than an hour, return false
            if (Math.abs(message.getCreationTime().toEpochSecond() - mostRecentMessageTime.toEpochSecond()) > 3600) return false;

        //If the message is older than the oldest message
        if (message.getCreationTime().isBefore(oldestMessageTime))
            //If the time between the given message and the oldest message in this group is greater than an hour, return false
            if (Math.abs(message.getCreationTime().toEpochSecond() - oldestMessageTime.toEpochSecond()) > 3600) return false;

        return true;
    }

    public UIMessageGroupText addMessage(Message message) {
        if (!canAddMessage(message)) return null;
        if (message.getCreationTime().isAfter(mostRecentMessageTime)) {
            mostRecentMessageTime = message.getCreationTime();
            UIMessageGroupText component = new UIMessageGroupText(message, this);

            if (!message.getAttachments().isEmpty()) {
                UIMessageGroupAttachment attachments = new UIMessageGroupAttachment(message);
                messagePanel.add(attachments,c,messagePanel.getComponentCount());
            }

            messageInfo.setText(getInfo());
            messagePanel.add(component,c,messagePanel.getComponentCount());
            return component;
        }
        else {
            oldestMessageTime = message.getCreationTime();
            UIMessageGroupText component = new UIMessageGroupText(message, this);

            if (!message.getAttachments().isEmpty()) {
                UIMessageGroupAttachment attachments = new UIMessageGroupAttachment(message);
                messagePanel.add(attachments,c,messagePanel.getComponentCount());
            }

            messagePanel.add(component,c,0);
            return component;
        }
    }

}
