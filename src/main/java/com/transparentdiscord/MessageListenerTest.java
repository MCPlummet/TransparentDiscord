package com.transparentdiscord;

import net.dv8tion.jda.client.entities.Group;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import static java.lang.System.out;

/**
 * Created by liam on 6/20/17.
 * Listens for events from Discord and responds to them
 */
public class MessageListenerTest extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        JDA jda = event.getJDA();
        long responseNumber = event.getResponseNumber();

        User author = event.getAuthor();                    //The user that sent the message
        Message message = event.getMessage();               //The message that was received
        MessageChannel channel = event.getChannel();        //The channel the message was sent in

        String msg = message.getContent();                  //Human readable message

        boolean bot = author.isBot();

        if (event.isFromType(ChannelType.TEXT)) {           //message sent from Guild TextChannel (a text channel from a server)
            Guild guild = event.getGuild();
            TextChannel textChannel = event.getTextChannel();
            Member member = event.getMember();

            String name;
            if (message.isWebhookMessage())
                name = author.getName();
            else
                name = member.getEffectiveName();

//            out.printf("(%s)[%s]<%s>: %s\n", guild.getName(), textChannel.getName(), name, msg);
        }
        else if (event.isFromType(ChannelType.PRIVATE)) {   //sent in PrivateChannel (PM)
            PrivateChannel privateChannel = event.getPrivateChannel();

            if (Main.chatWindows.containsKey(channel.getId())) {                //If the chat is currently open in the UI
                Main.chatWindows.get(channel.getId()).receiveMessage(message);  //Update the chat with the received message
            }

//            out.printf("[PRIV]<%s>: %s\n", author.getName(), msg);

        }
        else if (event.isFromType(ChannelType.GROUP)) {     //sent in a Group
            Group group = event.getGroup();
            String groupName = group.getName() != null ? group.getName() : "";  //A group name can be null due to it being unnamed.

//            out.printf("[GRP: %s]<%s>: %s\n", groupName, author.getName(), msg);
        }
    }
}
