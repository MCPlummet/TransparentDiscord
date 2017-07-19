package com.transparentdiscord.UI;

import com.transparentdiscord.Main;
import net.dv8tion.jda.client.entities.Group;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageHistory;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.User;

import java.awt.*;

/**
 * Created by liam on 7/11/17.
 * Represents a chat built from a Group object
 */
public class UIGroupChat extends UIChat {
    private GridBagConstraints c; //used to add new messages to the message list
    private MessageHistory messageHistory;

    /**
     * Constructs a group chat from the given parameter
     * @param group the group to construct this UI around
     */
    public UIGroupChat(Group group) {
        super();
        this.channel = group;
        this.messageHistory = group.getHistory();

        //Set up gridbag to add new messages
        c = new GridBagConstraints();
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;

        //Get some message history and add it to the message list
        //Message history in order from newest to oldest
        messageHistory.retrievePast(20).queue(messages -> {
            for (UIMessage m : UIMessage.loadMessages(messages)) {
                messageList.add(m, c, 0); //Add each message to the top of the list
            }
            refresh();
            scrollToBottom();
            doneLoad = true;
        });

        StringBuilder name = new StringBuilder();
        if (group.getName() == null) {
            for (User user : group.getUsers())
                name.append(user.getName() + ", ");
            name.deleteCharAt(name.length()-1);
            name.deleteCharAt(name.length()-1);
        } else {
            name.append(group.getName());
        }

        add(new UITitleBar(name.toString(), Main.chatWindow), BorderLayout.NORTH);


    }

    @Override
    public void receiveMessage(Message message) {
        messageList.add(new UIMessage(message), c, messageList.getComponentCount()); //Add the received message at the bottom of the message list
        refresh();
        scrollToBottom();
    }

    @Override
    protected void loadMessageHistory() {
        messageHistory.retrievePast(10).queue(messages -> {
            for (UIMessage m : UIMessage.loadMessages(messages)) {
                messageList.add(m, c, 0); //Add each message to the top of the list
            }
            refresh();
        });
    }
}
