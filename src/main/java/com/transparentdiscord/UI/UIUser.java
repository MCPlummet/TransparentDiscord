package com.transparentdiscord.UI;

import com.transparentdiscord.Main;
import net.dv8tion.jda.client.entities.Friend;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.ArrayList;
import java.util.function.Consumer;

/**
 * Created by liam on 7/12/17.
 * Represents a User
 */
public class UIUser extends JPanel {

    private User user;
    private JLabel displayName;     //A JLabel containing the name of the channel/guild
    private JLabel icon;
    private final int ICON_WIDTH = 40;

    public UIUser(User user) {
        setLayout(new BorderLayout());

        setBorder(new MatteBorder(0,0,1,0,Color.GRAY));

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

        this.user = user;

        displayName = new JLabel(user.getName());
        displayName.setBorder(new EmptyBorder(10,10,10,10));

        icon = new JLabel(Main.resizeToWidth(Main.getImage(user),ICON_WIDTH));

        add(displayName, BorderLayout.CENTER);
        add(icon, BorderLayout.WEST);

        //When clicked, open a private chat with this user
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                if (user.hasPrivateChannel()) {
                    Main.openChat(user.getPrivateChannel());
                } else {
                    user.openPrivateChannel().queue(new Consumer<PrivateChannel>() {
                        @Override
                        public void accept(PrivateChannel privateChannel) {
                            Main.openChat(privateChannel);
                            Main.channelList.addPrivateChannel(privateChannel);
                        }
                    });
                }
            }
        });
    }

    /**
     * Batch convert a list of users to UI elements
     * @param users the list of users to convert
     * @return a list of UIUsers created from the list of users
     */
    public static List<UIUser> loadUsers(List<User> users) {
        ArrayList<UIUser> list = new ArrayList<>();
        for (User u : users)
            list.add(new UIUser(u));
        return list;
    }

    /**
     * Batch convert a list of friends to UI elements
     * @param friends the list of friends to convert
     * @return the dying screams of your friends as their souls are bound to this hideous UI
     */
    public static List<UIUser> loadFriends(List<Friend> friends) {
        ArrayList<UIUser> list = new ArrayList<>();
        for (Friend f : friends)
            list.add(new UIUser(f.getUser()));
        return list;
    }
}
