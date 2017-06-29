package com.transparentdiscord.UI;

import net.dv8tion.jda.core.entities.PrivateChannel;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Created by liam on 6/26/17.
 * Represents a list of channels represented by UIChannelListItems
 */
public class UIChannelList extends JPanel {

    protected JPanel channelList;       //The list of channels
    protected JScrollPane scrollPane;   //Allows the user to scroll through the list of channels, should it become too large
    protected JScrollBar vertScrollBar; //The scrollbar of the channel list
    private GridBagConstraints c;       //Used to add items to the channel list

    /**
     * Constructs and empty channel list element
     */
    public UIChannelList() {
        setLayout(new BorderLayout());

        channelList = new JPanel(new GridBagLayout());

        scrollPane = new JScrollPane(channelList);
        add(scrollPane);

        c = new GridBagConstraints();

        vertScrollBar = scrollPane.getVerticalScrollBar();
    }

    /**
     * Constructs a channel list element with a list of PrivateChannels
     * @param channels a list of PrivateChannels to build the channel list from
     */
    public UIChannelList(List<PrivateChannel> channels) {
        this();

        c.gridwidth = GridBagConstraints.REMAINDER;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        for (UIChannelListItem item : UIChannelListItem.loadPrivateChannels(channels))
            channelList.add(item, c, 0); //Add the channel at the top of the list
    }

    //TODO implement PrivateChannel add
    //TODO implement Group add
    //TODO implement TextChannel add

    /**
     * Refresh the channel list to display newly added elements
     */
    protected void refresh() {
        channelList.repaint();
        channelList.revalidate();
        scrollPane.repaint();
        scrollPane.revalidate();
        repaint();
        revalidate();
    }

}
