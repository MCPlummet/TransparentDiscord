package com.transparentdiscord;
import com.transparentdiscord.UI.*;
import net.dv8tion.jda.client.entities.Group;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.exceptions.RateLimitedException;

import javax.imageio.ImageIO;
import javax.security.auth.login.LoginException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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

    private static HashMap<String, ImageIcon> chatIcons;    //Maps the ID of a chat, guild, or user icon to the icon

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        String  token   = scanner.nextLine();               //Read the user token (obtainable from the Discord web client). Used to log in.
        chatWindows     = new HashMap<>();
        chatIcons       = new HashMap<>();

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
            chatWindow.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent focusEvent) {
                    super.focusLost(focusEvent);
                    chatWindow.setVisible(false);
                }
            });


            bubbleWindow = new JFrame();
            bubbleWindow.setLocationRelativeTo(null);
            bubbleWindow.setUndecorated(true);
            bubbleWindow.setBackground(new Color(0,0,0,0));//Make the window transparent
            bubbleWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //Kill the program if the bubble window is closed

            bubblePane = new JPanel();
            bubblePane.setLayout(new GridBagLayout());
            bubblePane.setBackground(new Color(0,0,0,0));

            gbc = new GridBagConstraints();
            gbc.gridwidth = GridBagConstraints.REMAINDER;
            gbc.weightx = 1;
//            gbc.ipady = 45;
            gbc.fill = GridBagConstraints.HORIZONTAL;

            bubblePane.add(new UIFloatingButton(bubbleWindow, channelWindow), gbc, 0); //Add the channel list button
            bubbleWindow.add(bubblePane);
            bubbleWindow.setSize(50,50);
            bubbleWindow.setVisible(true);

            UIChannelList channelList = new UIChannelList();
            channelList.addGuilds(guilds);
            channelList.addPrivateChannels(privateChannels);
            channelWindow.add(channelList);  //Add a channel list, currently only containing private channels, the the channel window
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
                addBubble(pc.getChannel(), getImage((PrivateChannel) channel));//And add a bubble for it
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
    public static void addBubble(MessageChannel channel, ImageIcon imageIcon) {
        bubblePane.add(new UIFloatingButton(channel, imageIcon), gbc, 0);
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

    public static ImageIcon getImage(Guild guild) {
        if (chatIcons.containsKey(guild.getIconId()))
            return chatIcons.get(guild.getIconId());
        else {
            try {
                ImageIcon image = getImageFromURL(new URL(guild.getIconUrl()));
                chatIcons.put(guild.getIconId(), image);
                return image;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static ImageIcon getImage(Group group) {
        if (chatIcons.containsKey(group.getIconId()))
            return chatIcons.get(group.getIconId());
        else {
            try {
                ImageIcon image = getImageFromURL(new URL(group.getIconUrl()));
                chatIcons.put(group.getIconId(), image);
                return image;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static ImageIcon getImage(PrivateChannel privateChannel) {
        return getImage(privateChannel.getUser());
    }

    public static ImageIcon getImage(User user) {
        if (chatIcons.containsKey(user.getAvatarId()))
            return chatIcons.get(user.getAvatarId());
        else {
            try {
                ImageIcon image = getCircularImageFromURL(new URL(user.getAvatarUrl()));
                chatIcons.put(user.getAvatarId(), image);
                return image;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static ImageIcon getImage(User user, int width, int height) {
        Image image = getImage(user).getImage();
        image = image.getScaledInstance(width,height,Image.SCALE_SMOOTH);
        return new ImageIcon(image);
    }

    public static ImageIcon getImageFromURL(URL url) throws IOException {
        final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty(
                "User-Agent",
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_5) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31");
        final BufferedImage image = ImageIO.read(connection.getInputStream());
        return new ImageIcon(image);
    }

    public static ImageIcon getCircularImageFromURL(URL url) throws IOException {
        final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty(
                "User-Agent",
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_5) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31");
        final BufferedImage image = ImageIO.read(connection.getInputStream());
        return new ImageIcon(clipToCircle(image));
    }

    public static ImageIcon resizeToWidth(ImageIcon image, int width) {
        float ratio = (float) image.getIconHeight()/image.getIconWidth();
        int height = (int) (ratio * width);
        return new ImageIcon(image.getImage().getScaledInstance(width,height,Image.SCALE_SMOOTH));
    }

    /**
     * Clips a buffered image to a circle as per https://stackoverflow.com/a/31424601
     * @param image the buffered image to clip
     * @return a masked buffered image clipped to a circle
     */
    private static BufferedImage clipToCircle(BufferedImage image) {
        int diameter = Math.min(image.getWidth(), image.getHeight());
        BufferedImage mask = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = mask.createGraphics();
        applyRenderingHints(g2d);
        g2d.fillOval(0, 0, diameter - 1, diameter - 1);
        g2d.dispose();

        BufferedImage masked = new BufferedImage(diameter, diameter, BufferedImage.TYPE_INT_ARGB);
        g2d = masked.createGraphics();
        applyRenderingHints(g2d);
        int x = (diameter - image.getWidth()) / 2;
        int y = (diameter - image.getHeight()) / 2;
        g2d.drawImage(image, x, y, null);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.DST_IN));
        g2d.drawImage(mask, 0, 0, null);
        g2d.dispose();

        return masked;
    }

    /**
     * Applies rendering hints to a Graphics2D object as per https://stackoverflow.com/a/31424601
     * @param g2d the Graphics2D object to modify
     */
    private static void applyRenderingHints(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
    }

    public static int getChatWidth() {
        return chatWindow.getWidth();
    }
}
