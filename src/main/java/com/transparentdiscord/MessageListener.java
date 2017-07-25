package com.transparentdiscord;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.user.UserTypingEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

/**
 * Created by liam on 6/20/17.
 * Listens for events from Discord and responds to them
 */
public class MessageListener extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        JDA jda = event.getJDA();
        long responseNumber = event.getResponseNumber();

        User author = event.getAuthor();                    //The user that sent the message
        Message message = event.getMessage();               //The message that was received
        MessageChannel channel = event.getChannel();        //The channel the message was sent in

        TransparentDiscord.receiveMessage(message, channel);
    }

    @Override
    public void onUserTyping(UserTypingEvent event) {
        //TODO add user typing actions
    }
}
