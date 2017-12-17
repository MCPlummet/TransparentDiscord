package com.transparentdiscord.UI;

import com.transparentdiscord.TransparentDiscord;
import net.dv8tion.jda.client.entities.Group;
import net.dv8tion.jda.core.entities.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.out;

/**
 * Created by liam on 6/26/17.
 * Represents an individual channel/chat to be used in a UIChannelList
 */
public class UIChannelListItem extends JPanel implements Comparable<UIChannelListItem> {

    private JLabel displayName;     //A JLabel containing the name of the channel/guild
    private JLabel messagePreview;  //Previews the most recent message
    private JPanel content;         //Holds the displayName and messagePreview JLabels
    private JLabel icon;
    private final int ICON_WIDTH = 40;
    private final String id;
    private OffsetDateTime time;

    /**
     * Sets up the common settings for the ListItem. The empty constructor should never be used on its own, so private.
     */
    private UIChannelListItem(String id) {
        setLayout(new BorderLayout());

        setBorder(new MatteBorder(0,0,1,0,Color.GRAY));

        content = new JPanel(new BorderLayout());
        this.id = id;

        MouseAdapter hoverChange = new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                setBackground(Color.decode("#99AAB5"));
                content.setBackground(Color.decode("#99AAB5"));
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                setBackground(Color.WHITE);
                content.setBackground(Color.WHITE);
            }
        };

        addMouseListener(hoverChange);

        setCursor(new Cursor(Cursor.HAND_CURSOR)); //Indicate to the user that this element is meant to be clicked
    }

    /**
     * Constructs a list item around a MessageChannel
     * @param channel the channel to create the list item from
     */
    public UIChannelListItem(PrivateChannel channel) {
        this(channel.getId());

        displayName = new JLabel(channel.getName());
        displayName.setBorder(new EmptyBorder(5,0,0,0));
        displayName.setFont(TransparentDiscord.boldFont.deriveFont(Font.PLAIN, 12));

        try {
            messagePreview = new JLabel();
            messagePreview.setFont(TransparentDiscord.defaultFont.deriveFont(Font.PLAIN,12));
            Message m = channel.getHistory().retrievePast(1).complete().get(0);
            messagePreview.setText(m.getAuthor().getName() +": " + m.getContent());
            time = m.getCreationTime();
            content.add(messagePreview, BorderLayout.CENTER);
        } catch (Exception e) {
            out.println("Channel " + channel.getName() + " does not have any messages");
        }

        icon = new JLabel(TransparentDiscord.resizeToWidth(TransparentDiscord.getImage(channel),ICON_WIDTH));
        icon.setBorder(new EmptyBorder(5,5,5,5));

        content.add(displayName, BorderLayout.NORTH);
        add(content, BorderLayout.CENTER);
        add(icon, BorderLayout.WEST);

        //When clicked, open the chat
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                TransparentDiscord.openChat(channel);
            }
        });
    }

    /**
     * Constructs a list item around a guild
     * @param guild
     */
    public UIChannelListItem(Guild guild) {
        this(guild.getId());

        displayName = new JLabel(guild.getName());
        displayName.setFont(TransparentDiscord.defaultFont.deriveFont(Font.PLAIN, 16));
        displayName.setBorder(new EmptyBorder(10,10,10,10));

        icon = new JLabel(TransparentDiscord.resizeToWidth(TransparentDiscord.getImage(guild),ICON_WIDTH));

        add(displayName, BorderLayout.CENTER);
        add(icon, BorderLayout.WEST);

        UIChannelList channelList = new UIChannelList(false);
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

    /**
     * Constructs a list item around a text channel
     * @param channel
     */
    public UIChannelListItem(TextChannel channel) {
        this(channel.getId());

        displayName = new JLabel(channel.getName());
        displayName.setFont(TransparentDiscord.defaultFont.deriveFont(Font.PLAIN, 16));
        displayName.setBorder(new EmptyBorder(10,10,10,10));

        add(displayName, BorderLayout.CENTER);

        //When clicked, open the chat
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                TransparentDiscord.openChat(channel);
            }
        });
    }

    /**
     * Constructs a list item around a group
     * @param group
     */
    public UIChannelListItem(Group group) {
        this(group.getId());

        StringBuilder name = new StringBuilder();
        if (group.getName() == null) {
            for (User user : group.getUsers())
                name.append(user.getName() + ", ");
            name.deleteCharAt(name.length()-1);
            name.deleteCharAt(name.length()-1);
        } else {
            name.append(group.getName());
        }
        displayName = new JLabel(name.toString());
        displayName.setBorder(new EmptyBorder(5,0,0,0));
        displayName.setFont(TransparentDiscord.boldFont.deriveFont(Font.PLAIN, 12));

        messagePreview = new JLabel();
        messagePreview.setFont(TransparentDiscord.defaultFont.deriveFont(Font.PLAIN,12));

        try {
            Message m = group.getHistory().retrievePast(1).complete().get(0);
            messagePreview.setText(m.getAuthor().getName() +": " + m.getContent());
            time = m.getCreationTime();
            content.add(messagePreview, BorderLayout.CENTER);
        } catch (Exception e) {
            out.println("Channel " + group.getName() + " does not have any messages");
        }

        icon = new JLabel(TransparentDiscord.resizeToWidth(TransparentDiscord.getImage(group),ICON_WIDTH));
        icon.setBorder(new EmptyBorder(5,5,5,5));

        content.add(displayName, BorderLayout.NORTH);
        add(content, BorderLayout.CENTER);
        add(icon, BorderLayout.WEST);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                TransparentDiscord.openChat(group);
            }
        });
    }

    public String getID() { return id; }
    public OffsetDateTime getTime() { return time; }

    public void updatePreview(Message message) {
        if (message.getContent().equals("") && message.getAttachments().size() > 0) {
            if (message.getAttachments().get(0).isImage()) {
                messagePreview.setText(message.getAuthor().getName() +" sent an image.");
            }
            else {
                messagePreview.setText(message.getAuthor().getName() +" sent a file.");
            }
        }
        else {
            messagePreview.setText(message.getAuthor().getName() +": " + message.getContent());
        }
        time = message.getCreationTime();
        revalidate();
        repaint();
    }

    @Override
    public int compareTo(UIChannelListItem other) {
        if (getTime() == null) return 1;
        if (other.getTime() == null) return -1;

        if (getTime().isAfter(other.getTime())) return -1;
        else if (getTime().isBefore(other.getTime())) return 1;
        else return 0;
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

    /**
     * Batch load a list of TextChannels
     * @param channels
     * @return a list of TextChannels converted to UI elements
     */
    public static List<UIChannelListItem> loadTextChannels(List<TextChannel> channels) {
        ArrayList<UIChannelListItem> list = new ArrayList<>();
        for (TextChannel c : channels) {
            list.add(new UIChannelListItem(c));
        }
        return list;
    }

    /**
     * Batch load a list of Groups
     * @param groups
     * @return a list of Groups converted to UI elements
     */
    public static List<UIChannelListItem> loadGroups(List<Group> groups) {
        ArrayList<UIChannelListItem> list = new ArrayList<>();
        for (Group g : groups) {
            list.add(new UIChannelListItem(g));
        }
        return list;
    }
}
