package com.transparentdiscord.UI;

import com.transparentdiscord.Main;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageHistory;
import net.dv8tion.jda.core.entities.PrivateChannel;

import java.awt.*;

/**
 * Created by liam on 6/23/17.
 * Represents a private chat
 */
public class UIPrivateChat extends UIChat {

    private GridBagConstraints c; //used to add new messages to the message list
    private MessageHistory messageHistory;

    public UIPrivateChat(PrivateChannel privateChannel) {
        super();
        this.channel = privateChannel;
        this.messageHistory = privateChannel.getHistory();

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

        add(new UITitleBar(channel.getName(), Main.chatWindow), BorderLayout.NORTH);
    }

    @Override
    protected void sendMessage(String message) {
        channel.sendMessage(message).queue();
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
