package com.transparentdiscord.UI;

import net.dv8tion.jda.core.entities.Guild;
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
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;

        vertScrollBar = scrollPane.getVerticalScrollBar();
        vertScrollBar.setUnitIncrement(16);
    }

    /**
     * Add a list of PrivateChats to the UI
     * @param channels the channels to add
     */
    public void addPrivateChannels(List<PrivateChannel> channels) {
        for (UIChannelListItem item : UIChannelListItem.loadPrivateChannels(channels))
            channelList.add(item,c,0);
    }

    /**
     * Add a list of Guilds to the UI
     * @param guilds the guilds to add
     */
    public void addGuilds(List<Guild> guilds) {
        for (UIChannelListItem item : UIChannelListItem.loadGuilds(guilds))
            channelList.add(item,c,0);
    }

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
