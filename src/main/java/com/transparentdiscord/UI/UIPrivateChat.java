package com.transparentdiscord.UI;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.PrivateChannel;

import java.awt.*;

import static java.lang.System.out;

/**
 * Created by liam on 6/23/17.
 * Represents a private chat
 */
public class UIPrivateChat extends UIChat {

    GridBagConstraints c; //used to add new messages to the message list

    /**
     * Constructs a private channel from the given parameter
     * @param privateChannel the private channel to construct this UI around
     */
    public UIPrivateChat(PrivateChannel privateChannel) {
        super();
        this.channel = privateChannel;

        //Set up gridbag to add new messages
        c = new GridBagConstraints();
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;

        //Get some message history and add it to the message list
        //Message history in order from newest to oldest
        for (UIMessage m : UIMessage.loadMessages(privateChannel.getHistory().retrievePast(20).complete())) {
            messageList.add(m, c, 0); //Add each message to the top of the list
        }

    }

    @Override
    protected void sendMessage(String message) {
        channel.sendMessage(message).queue();
    }

    @Override
    public void receiveMessage(Message message) {
        messageList.add(new UIMessage(message), c, messageList.getComponentCount()); //Add the received message at the bottom of the message list
        refresh();
    }
}
