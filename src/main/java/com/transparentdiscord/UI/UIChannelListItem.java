package com.transparentdiscord.UI;

import com.transparentdiscord.Main;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.PrivateChannel;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.out;

/**
 * Created by liam on 6/26/17.
 * Represents an individual channel/chat to be used in a UIChannelList
 */
public class UIChannelListItem extends JPanel {

    private MessageChannel channel; //The channel this component represents
    private JLabel channelName;     //A JLabel containing the name of the channel

    /**
     * Constructs a list item around a MessageChannel
     * @param channel the channel to create the list item from
     */
    public UIChannelListItem(MessageChannel channel) {
        this.channel = channel;

        channelName = new JLabel(channel.getName());

        setLayout(new BorderLayout());

        add(channelName, BorderLayout.CENTER);

        setBorder(new MatteBorder(0,0,1,0,Color.GRAY));

        //When clicked, open the chat
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
            Main.openChat(channel);
            }
        });

        setCursor(new Cursor(Cursor.HAND_CURSOR)); //Indicate to the user that this element is meant to be clicked
    }

    /**
     * Batch load a list of UIChannelListItems from a list of PrivateChannels
     * @param channels the private channels to convert to UI elements
     * @return returns the list of channels converted to UI elements
     */
    public static List<UIChannelListItem> loadPrivateChannels(List<PrivateChannel> channels) {
        ArrayList<UIChannelListItem> list = new ArrayList<>();
        for (MessageChannel c : channels)
            list.add(new UIChannelListItem(c));
        return list;
    }
}
