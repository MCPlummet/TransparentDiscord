package com.transparentdiscord.UI;

import com.transparentdiscord.Main;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.requests.Route;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liam on 6/26/17.
 * Represents an individual channel/chat to be used in a UIChannelList
 */
public class UIChannelListItem extends JPanel {

    private JLabel displayName;     //A JLabel containing the name of the channel/guild

    /**
     * Constructs a list item around a MessageChannel
     * @param channel the channel to create the list item from
     */
    public UIChannelListItem(MessageChannel channel) {
        displayName = new JLabel(channel.getName());

        setLayout(new BorderLayout());

        add(displayName, BorderLayout.CENTER);

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

    public UIChannelListItem(Guild guild) {
        displayName = new JLabel(guild.getName());

        setLayout(new BorderLayout());

        add(displayName, BorderLayout.CENTER);

        setBorder(new MatteBorder(0,0,1,0,Color.GRAY));

        //When clicked, open the chat
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                //TODO open guild window???
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

    /**
     * Batch load a list of UIChannelLIstItems from a list of Guilds
     * @param guilds the guilds to convert to UI elements
     * @return returns the list of guilds converted to UI elements
     */
    public static List<UIChannelListItem> loadGuilds(List<Guild> guilds) {
        ArrayList<UIChannelListItem> list = new ArrayList<>();
        for (Guild g : guilds)
            list.add(new UIChannelListItem(g));
        return list;
    }
}
