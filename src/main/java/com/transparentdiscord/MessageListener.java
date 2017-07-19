package com.transparentdiscord;

import net.dv8tion.jda.client.entities.Group;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.user.UserTypingEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import static java.lang.System.out;

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

        String msg = message.getContent();                  //Human readable message

        boolean bot = author.isBot();

        if (Main.chatWindows.containsKey(channel.getId())) {                //If the chat is currently open in the UI
            Main.chatWindows.get(channel.getId()).receiveMessage(message);  //Update the chat with the received message
        }
    }

    @Override
    public void onUserTyping(UserTypingEvent event) {

    }
}
