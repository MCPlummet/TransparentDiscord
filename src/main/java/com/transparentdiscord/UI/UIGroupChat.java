package com.transparentdiscord.UI;

import com.transparentdiscord.TransparentDiscord;
import com.transparentdiscord.UI.Message.UIMessage;
import com.transparentdiscord.UI.Message.UIMessageGroup;
import net.dv8tion.jda.client.entities.Group;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageHistory;
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
            newestGroup = oldestGroup = new UIMessageGroup(messages.get(0));
            messageList.add(oldestGroup,c,0);

            //Going from newest to oldest...
            for (int i = 1; i < messages.size(); i++) {
                if (oldestGroup.canAddMessage(messages.get(i))) {
                    oldestGroup.addMessage(messages.get(i));
                }
                else {
                    oldestGroup = new UIMessageGroup(messages.get(i));
                    messageList.add(oldestGroup,c,0);
                }
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

        add(new UITitleBar(name.toString(), TransparentDiscord.chatWindow), BorderLayout.NORTH);


    }

    @Override
    public void receiveMessage(Message message) {
        if (newestGroup.canAddMessage(message))
            newestGroup.addMessage(message);
        else {
            newestGroup = new UIMessageGroup(message);
            messageList.add(newestGroup, c, messageList.getComponentCount()); //Add the received message at the bottom of the message list
        }

        refresh();
        scrollToBottom();
    }

    @Override
    protected void loadMessageHistory() {
        messageHistory.retrievePast(10).queue(messages -> {
            //Going from newest to oldest...
            for (int i = 0; i < messages.size(); i++) {
                if (oldestGroup.canAddMessage(messages.get(i))) {
                    oldestGroup.addMessage(messages.get(i));
                }
                else {
                    oldestGroup = new UIMessageGroup(messages.get(i));
                    messageList.add(oldestGroup,c,0);
                }
            }
            refresh();
        });
    }
}
