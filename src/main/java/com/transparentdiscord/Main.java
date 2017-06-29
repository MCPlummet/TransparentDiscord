package com.transparentdiscord;
import com.transparentdiscord.UI.*;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.exceptions.RateLimitedException;

import javax.security.auth.login.LoginException;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
/**
 * Created by liam on 6/20/17.
 * Controls the initialization of GUI elements and the underlying Discord API
 */

public class Main {

    public static List<Guild> guilds;                       //A list of what Discord calls "servers" (henceforth these will be referred to as guilds
    public static List<PrivateChannel> privateChannels;     //A list of Private/Direct Messages
    public static List<TextChannel> textChannels;           //A list of text channels from Guilds/Servers

    public static HashMap<String, UIChat> chatWindows;      //Maps the ID of a MessageChannel (String) to the UI associated with that channel (UIChat)

    public static JFrame channelWindow;                     //The JFrame responsible for displaying a UIChannelList
    public static JFrame chatWindow;                        //The JFrame responsible for displaying the active UIChat
    public static JFrame bubbleWindow;                      //The JFrame responsible for displaying the chat bubbles

    public static JPanel bubblePane;                        //The JPanel that contains the chat bubbles
    private static GridBagConstraints gbc;                  //Constraints for adding bubbles to bubblePane

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        String  token   = scanner.nextLine();               //Read the user token (obtainable from the Discord web client). Used to log in.
        chatWindows     = new HashMap<>();

        try {
            JDA jda = new JDABuilder(AccountType.CLIENT)    //Initialize the API
                    .setToken(token)                        //Log in with the token specified on stdin
                    .addEventListener(new MessageListenerTest()) //Add a message listener to handle Discord events (messages, calls, etc.)
                    .buildBlocking();                       //Makes sure the API finishes initializing before continuing (as opposed to buildAsync()
            guilds = jda.getGuilds();                       //Get a list of all the guilds the user is a member of
            privateChannels = jda.getPrivateChannels();     //Get a list of all the user's private chats (note: these are chats the user has already created, not one for every contact/friend)
            textChannels = jda.getTextChannels();           //Get a list of all the text chats from the user's guilds

            channelWindow = new JFrame();
            channelWindow.setUndecorated(true);             //Remove the window border
            channelWindow.setSize(300,500);

            chatWindow = new JFrame();
            chatWindow.setUndecorated(true);
            chatWindow.setSize(300,500);

            bubbleWindow = new JFrame();
            bubbleWindow.setUndecorated(true);
            bubbleWindow.setBackground(new Color(0,0,0,0));//Make the window transparent
            bubbleWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //Kill the program if the bubble window is closed

            bubblePane = new JPanel();
            bubblePane.setLayout(new GridBagLayout());
            bubblePane.setBackground(new Color(0,0,0,0));

            gbc = new GridBagConstraints();
            gbc.gridwidth = GridBagConstraints.REMAINDER;
            gbc.weightx = 1;
            gbc.ipady = 45;
            gbc.fill = GridBagConstraints.HORIZONTAL;

            bubblePane.add(new UIFloatingButton(bubbleWindow, channelWindow), gbc, 0); //Add the channel list button
            bubbleWindow.add(bubblePane);
            bubbleWindow.setSize(50,50);
            bubbleWindow.setVisible(true);

            channelWindow.add(new UIChannelList(privateChannels));  //Add a channel list, currently only containing private channels, the the channel window
            //TODO add Groups and Guild Text Channels to the channel list (see UIChannelList)

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (RateLimitedException e) {
            e.printStackTrace();
        } catch (LoginException e) {
            e.printStackTrace();
        }
    }

    /**
     * Repositions chatWindow and channelWindow relative to the bubbles
     */
    public static void repositionWindows() {
        int bubbleX = bubbleWindow.getX();
        int bubbleY = bubbleWindow.getY();

        chatWindow.setLocation(bubbleX-305,bubbleY-(500-bubbleWindow.getHeight()));
        channelWindow.setLocation(bubbleX-305,bubbleY-(500-bubbleWindow.getHeight()));
    }

    /**
     * Given a channel, opens a UIChat inside chatWindow with that channel, either retrieving
     * it from chatWindows or initializing a new one
     * @param channel the channel to open
     */
    public static void openChat(MessageChannel channel) {
        if (channel instanceof PrivateChannel) {                        //If the channel is a private channel...
            UIPrivateChat pc;
            if (chatWindows.containsKey(channel.getId()))               //If the channel has already been opened...
                pc = (UIPrivateChat) chatWindows.get(channel.getId());  //Retrieve the UIChat object from chatWindows
            else {
                pc = new UIPrivateChat((PrivateChannel) channel);       //Otherwise, create a new UIChat
                chatWindows.put(channel.getId(), pc);                   //Put it in chatWindows
                addBubble(pc.getChannel());                             //And add a bubble for it
            }

            chatWindow.getContentPane().removeAll();    //Clear the chatWindow, removing any previously opened chats
            chatWindow.add(pc);                         //Add the new UIChat
            chatWindow.revalidate();
            chatWindow.repaint();                       //Reload the chatWindow
            chatWindow.setVisible(true);                //Make sure chatWindow is displayed
            channelWindow.setVisible(false);            //Hide the channel list
        }
    }

    /**
     * Adds a bubble to the bubble list for a given channel
     * Clicking the button will open the given channel inside a UIChat inside chatWindow
     * @param channel the channel to add a bubble for
     */
    public static void addBubble(MessageChannel channel) {
        bubblePane.add(new UIFloatingButton(channel), gbc, 0);
        bubblePane.revalidate();
        bubblePane.repaint();
        resizeBubbles();
        repositionWindows();
    }

    /**
     * Resizes the bubble window to appropriately fit the current number of bubbles
     */
    private static void resizeBubbles() {
        //TODO switch to more specific, less esoteric math (use variables to set bubble size, etc.)
        bubbleWindow.setLocation(bubbleWindow.getX(), bubbleWindow.getY() - 50);    //Move the bubbles down 50 pixels (the size of a bubble)
        bubbleWindow.setSize(50,bubblePane.getComponentCount()*65);                 //Increase the size of the bubble container
        bubbleWindow.revalidate();
        bubbleWindow.repaint();
    }
}
