package com.transparentdiscord.UI;

import net.dv8tion.jda.client.entities.Friend;
import net.dv8tion.jda.core.entities.User;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Created by liam on 7/12/17.
 * Represents a list of users
 */
public class UIUserList extends JPanel {
    protected JPanel userList;          //The list of users
    protected JScrollPane scrollPane;   //Allows the user to scroll through the list of users, should it become too large
    protected JScrollBar vertScrollBar; //The scrollbar of the channel list
    private GridBagConstraints c;       //Used to add items to the channel list

    /**
     * Constructs and empty channel list element
     */
    public UIUserList() {
        setLayout(new BorderLayout());

        userList = new JPanel(new GridBagLayout());

        scrollPane = new JScrollPane(userList);
        add(scrollPane);

        c = new GridBagConstraints();
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;

        vertScrollBar = scrollPane.getVerticalScrollBar();
        vertScrollBar.setUnitIncrement(16);
    }

    /**
     * Add a given user to the UI
     * @param user the user to add
     */
    public void addUser(User user) {
        userList.add(new UIUser(user),c,0);
    }

    /**
     * Add a list of users to the UI
     * @param users the list of users to add
     */
    public void addUsers(List<User> users) {
        for (UIUser item : UIUser.loadUsers(users))
            userList.add(item,c,0);
    }

    /**
     * Add a list of friends to the UI
     * @param friends the list of friends to add
     */
    public void addFriends(List<Friend> friends) {
        for (UIUser item : UIUser.loadFriends(friends))
            userList.add(item,c,0);
    }
}
