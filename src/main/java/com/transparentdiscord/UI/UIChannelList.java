package com.transparentdiscord.UI;

import com.transparentdiscord.UI.Custom.CustomScrollBarUI;
import net.dv8tion.jda.client.entities.Group;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.TextChannel;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by liam on 6/26/17.
 * Represents a list of channels represented by UIChannelListItems
 */
public class UIChannelList extends JPanel {

    private JTabbedPane tabPane;              //Separates Private and Group chats from Guilds
    private JPanel channelList;               //The list of channels
    private JPanel guildList;                 //The list of channels

    private JScrollPane channelScrollPane;    //Allows the user to scroll through the list of channels, should it become too large
    private JScrollBar vertScrollBarChannels; //The scrollbar of the channel list
    private JScrollPane guildScrollPane;      //Allows the user to scroll through the list of guilds
    private JScrollBar vertScrollBarGuilds;   //The scrollbar of the guildlist
    
    private HashMap<String, UIChannelListItem> channelItems; //Keeps track of channels so that they can be updated
    private GridBagConstraints c;               //Used to add items to the channel list
    private boolean tabbed;

    /**
     * Constructs and empty channel list element
     * @param tabbed whether or not to construct the list with tabs
     */
    public UIChannelList(boolean tabbed) {
        setLayout(new BorderLayout());
        this.tabbed = tabbed;

        channelItems = new HashMap<>();

        channelList = new JPanel(new GridBagLayout());
        channelScrollPane = new JScrollPane(channelList);

        if (tabbed) {
            guildList = new JPanel(new GridBagLayout());
            guildScrollPane = new JScrollPane(guildList);

            tabPane = new JTabbedPane();
            tabPane.setBackground(Color.decode("#99AAB5"));
            tabPane.addTab("Chats", channelScrollPane);
            tabPane.addTab("Servers", guildScrollPane);

            vertScrollBarGuilds = guildScrollPane.getVerticalScrollBar();
            vertScrollBarGuilds.setUnitIncrement(16);
            vertScrollBarGuilds.setUI(new CustomScrollBarUI());

            setBackground(Color.decode("#7289DA"));

            add(tabPane);
        } else {
            add(channelScrollPane);
        }



        c = new GridBagConstraints();
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;

        vertScrollBarChannels = channelScrollPane.getVerticalScrollBar();
        vertScrollBarChannels.setUI(new CustomScrollBarUI());
        vertScrollBarChannels.setUnitIncrement(16);
    }

    /**
     * Add a list of PrivateChats to the UI
     * @param channels the channels to add
     */
    public void addPrivateChannels(List<PrivateChannel> channels) {
        List<UIChannelListItem> uiChannels = UIChannelListItem.loadPrivateChannels(channels);
        Collections.sort(uiChannels);
        for (UIChannelListItem item : uiChannels) {
            channelList.add(item, c, channelList.getComponentCount());
            channelItems.put(item.getID(), item);
        }
    }

    /**
     * Add a list of Guilds to the UI
     * @param guilds the guilds to add
     */
    public void addGuilds(List<Guild> guilds) {
        for (UIChannelListItem item : UIChannelListItem.loadGuilds(guilds)) {
            if (tabbed) guildList.add(item, c, 0);
            else channelList.add(item,c,0);
            channelItems.put(item.getID(), item);
        }
    }

    /**
     * Add a list of TextChannels to the UI
     * @param channels the TextChannels to add
     */
    public void addTextChannels(List<TextChannel> channels) {
        for (UIChannelListItem item : UIChannelListItem.loadTextChannels(channels)) {
            channelList.add(item, c, channelList.getComponentCount());
            channelItems.put(item.getID(), item);
        }
    }

    /**
     * Add a list of Groups to the UI
     * @param groups the list of Groups to add
     */
    public void addGroups(List<Group> groups) {
        for (UIChannelListItem item : UIChannelListItem.loadGroups(groups)) {
            channelList.add(item, c, 0);
            channelItems.put(item.getID(), item);
        }
    }

    public void addGroupsAndPrivateChannels(List<Group> groups, List<PrivateChannel> channels) {
        List<UIChannelListItem> uiItems = UIChannelListItem.loadGroups(groups);
        uiItems.addAll(UIChannelListItem.loadPrivateChannels(channels));
        Collections.sort(uiItems);
        for (UIChannelListItem item : uiItems) {
            channelList.add(item, c, channelList.getComponentCount());
            channelItems.put(item.getID(), item);
        }
    }

    /**
     * Add a PrivateChannel to the UI
     * @param privateChannel the PrivateChannel to add
     */
    public void addPrivateChannel(PrivateChannel privateChannel) {
        UIChannelListItem item = new UIChannelListItem(privateChannel);
        channelList.add(item,c,0);
        channelItems.put(item.getID(), item);
    }

    /**
     * Add a Group to the UI
     * @param group the Group to add
     */
    public void addGroup(Group group) {
        UIChannelListItem item = new UIChannelListItem(group);
        channelList.add(item,c,0);
        channelItems.put(item.getID(), item);
    }

    public void update(Message message) {
        if (!(message.getChannel() instanceof TextChannel)) {
            UIChannelListItem item = channelItems.get(message.getChannel().getId());
            channelList.remove(item);
            channelList.add(item,c,0);
            item.updatePreview(message);
            refresh();
        }
    }

    /**
     * Refresh the channel list to display newly added elements
     */
    private void refresh() {
        channelList.revalidate();
        channelList.repaint();
        channelScrollPane.revalidate();
        channelScrollPane.repaint();
        if (tabbed) {
            tabPane.revalidate();
            tabPane.repaint();
        }
        repaint();
        revalidate();
    }

}
