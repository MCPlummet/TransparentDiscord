package com.transparentdiscord.UI;

import com.transparentdiscord.Main;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.requests.Route;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.out;

/**
 * Created by liam on 6/26/17.
 * Represents an individual channel/chat to be used in a UIChannelList
 */
public class UIChannelListItem extends JPanel {

    private JLabel displayName;     //A JLabel containing the name of the channel/guild
    private JLabel icon;

    private UIChannelListItem() {
        setLayout(new BorderLayout());

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                setBackground(Color.decode("#99AAB5"));
            }

            @Override public void mouseExited(MouseEvent mouseEvent) {
                setBackground(Color.WHITE);
            }
        });
        setCursor(new Cursor(Cursor.HAND_CURSOR)); //Indicate to the user that this element is meant to be clicked
    }

    /**
     * Constructs a list item around a MessageChannel
     * @param channel the channel to create the list item from
     */
    public UIChannelListItem(PrivateChannel channel) {
        this();

        displayName = new JLabel(channel.getName());
        displayName.setBorder(new EmptyBorder(10,10,10,10));

        icon = new JLabel(Main.resizeToWidth(Main.getImage(channel),50));

        add(displayName, BorderLayout.CENTER);
        add(icon, BorderLayout.WEST);

        setBorder(new MatteBorder(0,0,1,0,Color.GRAY));

        //When clicked, open the chat
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                Main.openChat(channel);
            }
        });
    }

    public UIChannelListItem(Guild guild) {
        this();

        displayName = new JLabel(guild.getName());
        displayName.setBorder(new EmptyBorder(10,10,10,10));

        icon = new JLabel(Main.resizeToWidth(Main.getImage(guild),50));

        add(displayName, BorderLayout.CENTER);
        add(icon, BorderLayout.WEST);

        setBorder(new MatteBorder(0,0,1,0,Color.GRAY));

        UIChannelList channelList = new UIChannelList();
        channelList.addTextChannels(guild.getTextChannels());

        JPopupMenu channelMenu = new JPopupMenu();
        channelMenu.add(channelList);

        //When clicked, open the chat
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                channelMenu.show(e.getComponent(),0,0);
            }
        });
    }

    public UIChannelListItem(TextChannel channel) {
        this();

        displayName = new JLabel(channel.getName());
        displayName.setBorder(new EmptyBorder(10,10,10,10));

        add(displayName, BorderLayout.CENTER);

        setBorder(new MatteBorder(0,0,1,0,Color.GRAY));

        //When clicked, open the chat
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                Main.openChat(channel);
            }
        });
    }

    /**
     * Batch load a list of UIChannelListItems from a list of PrivateChannels
     * @param channels the private channels to convert to UI elements
     * @return returns the list of channels converted to UI elements
     */
    public static List<UIChannelListItem> loadPrivateChannels(List<PrivateChannel> channels) {
        ArrayList<UIChannelListItem> list = new ArrayList<>();
        for (PrivateChannel c : channels) {
            list.add(new UIChannelListItem(c));
        }
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

    public static List<UIChannelListItem> loadTextChannels(List<TextChannel> channels) {
        ArrayList<UIChannelListItem> list = new ArrayList<>();
        for (TextChannel c : channels) {
            list.add(new UIChannelListItem(c));
        }
        return list;
    }
}
