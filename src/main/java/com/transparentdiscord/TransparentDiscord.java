package com.transparentdiscord;
import com.transparentdiscord.UI.*;
import net.dv8tion.jda.client.entities.Friend;
import net.dv8tion.jda.client.entities.Group;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.exceptions.RateLimitedException;

import javax.imageio.ImageIO;
import javax.security.auth.login.LoginException;
import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;

import static java.lang.System.out;

/**
 * Created by liam on 6/20/17.
 * Controls the initialization of GUI elements and the underlying Discord API
 */

public class TransparentDiscord {

    public static List<Guild> guilds;                       //A list of what Discord calls "servers" (henceforth these will be referred to as guilds
    public static List<PrivateChannel> privateChannels;     //A list of Private/Direct Messages
    public static List<Group> groups;                       //A list of Group messages
    public static List<Friend> friends;                      //A list of the user's friends

    public static HashMap<String, UIChat> chatWindows;      //Maps the ID of a MessageChannel (String) to the UI associated with that channel (UIChat)
    private static HashMap<String, ImageIcon> chatIcons;    //Maps the ID of a chat, guild, or user icon to the icon
    private static HashMap<String, UIFloatingButton> bubbles;//Maps the ID of a chat to a bubble, so that it can be removed from the list later

    public static JFrame channelWindow;                     //The JFrame responsible for displaying a UIChannelList
    public static JFrame chatWindow;                        //The JFrame responsible for displaying the active UIChat
    public static JFrame bubbleWindow;                      //The JFrame responsible for displaying the chat bubbles
    public static JFrame friendWindow;                      //A window for displaying a friend list

    public static UIChannelList channelList;                //The list of channels in channelWindow

    public static JPanel bubblePane;                        //The JPanel that contains the chat bubbles
    private static GridBagConstraints gbc;                  //Constraints for adding bubbles to bubblePane

    private static ImageIcon defaultUserIcon;               //The default user icon (Discord logo with yellow background)
    private static ImageIcon defaultGroupIcon;              //The default icon for groups
    private static ImageIcon downloadIcon;                  //The default attachment icon

    private static final String GROUP_ICON_PATH = "/images/group.png"; //The path to the default group icon
    private static final String DOWNLOAD_ICON_PATH = "/images/download.png"; //The path to the download icon

    private static JDA jda;                                 //The object used to interface with Discord

    public static Font defaultFont;                         //The font to use for TransparentDiscord
    public static Font boldFont;                            //The font to use for bolded text
    private static final String FONT_PATH = "/fonts/roboto/Roboto-Medium.ttf"; //The path to the font
    private static final String BOLD_FONT_PATH = "/fonts/roboto/Roboto-Bold.ttf";//The path to the bold font

    private static Clip notificationSound;                  //Stores the sound file used for notifications
    private static final String SOUND_PATH = "/sounds/notification.wav"; //Notification obtained from http://freesound.org/people/TheGertz/sounds/235911/


    public static void main(String[] args) {
        System.setProperty("http.agent", "Mozilla/5.0 AppleWebKit/537.31 Chrome/26.0.1410.65 Safari/537.31");
        File tokenFile = new File("token");
        if (tokenFile.exists()) {           //Check if the token has been stored
            try {
                String token = Files.readAllLines(Paths.get(tokenFile.getPath())).get(0); //Try to read the token
                init(token); //And init with the token
            } catch (Exception e) { //If that doesn't work, display the Token input UI
                displayLoginPrompt();
                e.printStackTrace();
            }
        } else {
            displayLoginPrompt();   //Otherwise, display the login prompt
        }
    }

    /**
     * Displays the Token input UI
     */
    private static void displayLoginPrompt() {
        JFrame loginWindow = new JFrame("Login");       //The frame to hold the token UI
        JPanel loginPanel = new JPanel();               //The panel to hold the token UI
        loginPanel.setLayout(new BorderLayout());       //BorderLayout for now
        loginWindow.add(loginPanel);
        JTextField tokenField = new JTextField();       //Token input
        JButton submit = new JButton("Log in");         //Login button
        JCheckBox saveToken = new JCheckBox("Remember");//Whether or not to save the token to a file
        loginPanel.add(tokenField,BorderLayout.CENTER);
        loginPanel.add(submit,BorderLayout.EAST);
        loginPanel.add(saveToken, BorderLayout.SOUTH);
        loginWindow.setSize(250,70);
        loginWindow.setLocationRelativeTo(null);
        loginWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        tokenField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    if (saveToken.isSelected()) {
                        PrintWriter tokenWriter = new PrintWriter("token");
                        tokenWriter.write(tokenField.getText());
                        tokenWriter.close();
                    }
                    init(tokenField.getText());
                    loginWindow.setVisible(false);
                    loginWindow.dispose();
                } catch (Exception e) {
                    e.printStackTrace();
                    System.exit(0);
                }
            }
        });

        submit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    if (saveToken.isSelected()) {
                        PrintWriter tokenWriter = new PrintWriter("token");
                        tokenWriter.write(tokenField.getText());
                        tokenWriter.close();
                    }
                    init(tokenField.getText());
                    loginWindow.setVisible(false);
                    loginWindow.dispose();
                } catch (Exception e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            }
        });

        loginWindow.setVisible(true);
    }

    private static void init(String token) throws LoginException, InterruptedException, RateLimitedException, IOException {
        chatWindows     = new HashMap<>();
        chatIcons       = new HashMap<>();
        bubbles         = new HashMap<>();

        jda = new JDABuilder(AccountType.CLIENT)    //Initialize the API
                .setToken(token)                        //Log in with the token specified on stdin
                .addEventListener(new MessageListener()) //Add a message listener to handle Discord events (messages, calls, etc.)
                .buildBlocking();                       //Makes sure the API finishes initializing before continuing (as opposed to buildAsync()
        guilds = jda.getGuilds();                       //Get a list of all the guilds the user is a member of
        privateChannels = jda.getPrivateChannels();     //Get a list of all the user's private chats (note: these are chats the user has already created, not one for every contact/friend)
        groups = jda.asClient().getGroups();            //Get a list of all the user's group chats
        friends = jda.asClient().getFriends();          //Get a list of all the user's friends

        try {
            InputStream stream = TransparentDiscord.class.getResourceAsStream(FONT_PATH);
            defaultFont = Font.createFont(Font.TRUETYPE_FONT, stream);
            stream.close();
            stream = TransparentDiscord.class.getResourceAsStream(BOLD_FONT_PATH);
            boldFont = Font.createFont(Font.TRUETYPE_FONT, stream);
            stream.close();

            notificationSound = AudioSystem.getClip();
            AudioInputStream inputStream = AudioSystem.getAudioInputStream(TransparentDiscord.class.getResource(SOUND_PATH));
            notificationSound.open(inputStream);

            defaultGroupIcon = getImageFromFile(TransparentDiscord.class.getResource(GROUP_ICON_PATH));
            downloadIcon = getImageFromFile(TransparentDiscord.class.getResource(DOWNLOAD_ICON_PATH));

        } catch (LineUnavailableException e) {
            out.println("failed to load font, using builtin");
            defaultFont = new Font("Helvetica", Font.PLAIN, 12);
        } catch (FontFormatException e) {
            out.println("failed to load font, using builtin");
            defaultFont = new Font("Helvetica", Font.PLAIN, 12);
        } catch (UnsupportedAudioFileException e) {
            out.println("failed to load notification sound");
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        defaultUserIcon = getCircularImageFromURL(new URL(jda.getSelfUser().getDefaultAvatarUrl()));

        channelWindow = new JFrame();
        channelWindow.setLayout(new BorderLayout());
        channelWindow.add(new UITitleBar(), BorderLayout.NORTH);
        channelWindow.setUndecorated(true);             //Remove the window border
        channelWindow.setSize(400,700);

        chatWindow = new JFrame();
        chatWindow.setUndecorated(true);
        chatWindow.setSize(400,700);


        bubbleWindow = new JFrame();
        bubbleWindow.setLocationRelativeTo(null);
        bubbleWindow.setUndecorated(true);
        bubbleWindow.setBackground(new Color(0,0,0,0));//Make the window transparent
        bubbleWindow.setAlwaysOnTop(true);
        bubbleWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //Kill the program if the bubble window is closed

        bubblePane = new JPanel();
        bubblePane.setLayout(new GridBagLayout());
        bubblePane.setBackground(new Color(0,0,0,0));

        gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        bubblePane.add(new UIFloatingButton(bubbleWindow, channelWindow), gbc, 0); //Add the channel list button
        bubbleWindow.add(bubblePane);
        bubbleWindow.setSize(50,50);
        bubbleWindow.pack();
        bubbleWindow.setVisible(true);

        channelList = new UIChannelList(true);
        channelList.addGuilds(guilds);
        channelList.addGroupsAndPrivateChannels(groups, privateChannels);
        channelWindow.add(channelList, BorderLayout.CENTER);  //Add a channel list, currently only containing private channels, the the channel window

        channelWindow.add(new UIStatusBar(getImage(jda.getSelfUser())), BorderLayout.SOUTH);

        channelWindow.repaint();
        channelWindow.revalidate();

        friendWindow = new JFrame("Friends");
        UIUserList friendList = new UIUserList();
        friendList.addFriends(jda.asClient().getFriends());
        friendWindow.add(friendList);
        friendWindow.setSize(400,700);
        friendWindow.setLocationRelativeTo(channelWindow);
        friendWindow.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

        repositionWindows();
    }

    /**
     * Repositions chatWindow and channelWindow relative to the bubbles
     */
    public static void repositionWindows() {
        int bubbleX = bubbleWindow.getX();
        int bubbleY = bubbleWindow.getY();


        int height = (bubbles.keySet().size()+1)*UIFloatingButton.BUBBLE_SIZE;

        chatWindow.setLocation(bubbleX-405,bubbleY-(700-height));
        channelWindow.setLocation(bubbleX-405,bubbleY-(700-height));
    }

    /**
     * Given a channel, opens a UIChat inside chatWindow with that channel, either retrieving
     * it from chatWindows or initializing a new one
     * @param channel the channel to open
     */
    public static void openChat(MessageChannel channel) {
        //If the chat is already open...
        if (chatWindows.containsKey(channel.getId()) &&
                chatWindow.getContentPane().equals(chatWindows.get(channel.getId())) &&
                chatWindow.isVisible())
        {
            chatWindow.setVisible(false);   //Minimize the chat window
            return;                         //No need to continue
        }

        UIChat chat = getChat(channel);             //Get the UIChat element from the MessageChannel
        if (!bubbles.containsKey(channel.getId())) addBubble(channel);
        chatWindow.setContentPane(chat);            //Update the current chat UI
        chatWindow.setVisible(true);                //Make sure chatWindow is displayed
        chatWindow.revalidate();                    //Refresh the chat window
        chatWindow.repaint();                       //..
        channelWindow.setVisible(false);            //Hide the channel list
    }

    private static UIChat getChat(MessageChannel channel) {
        if (channel instanceof PrivateChannel) {                        //If the channel is a private channel...
            UIPrivateChat pc;
            if (chatWindows.containsKey(channel.getId()))               //If the channel has already been opened...
                pc = (UIPrivateChat) chatWindows.get(channel.getId());  //Retrieve the UIChat object from chatWindows
            else {
                pc = new UIPrivateChat((PrivateChannel) channel);       //Otherwise, create a new UIChat
                chatWindows.put(channel.getId(), pc);                   //Put it in chatWindows
            }
            return pc;
        } else if (channel instanceof TextChannel) {
            UITextChat tc;
            TextChannel textChannel = (TextChannel) channel;
            if (chatWindows.containsKey(channel.getId()))
                tc = (UITextChat) chatWindows.get(channel.getId());
            else {
                tc = new UITextChat(textChannel);
                chatWindows.put(channel.getId(), tc);
            }

            return tc;
        } else if (channel instanceof Group) {
            UIGroupChat gc;
            Group group = (Group) channel;
            if (chatWindows.containsKey(channel.getId()))
                gc = (UIGroupChat) chatWindows.get(channel.getId());
            else {
                gc = new UIGroupChat(group);
                chatWindows.put(channel.getId(), gc);
            }

            return gc;
        } else {
            return null;
        }
    }

    public static void closeChat(MessageChannel channel) {
        if (chatWindows.containsKey(channel.getId()) &&
                chatWindow.getContentPane().equals(chatWindows.get(channel.getId())) &&
                chatWindow.isVisible()) chatWindow.setVisible(false);   //Minimize the chat window if it's open
        if (chatWindows.containsKey(channel.getId())) {
            chatWindow.remove(chatWindows.get(channel.getId()));
            chatWindows.remove(channel.getId());
            removeBubble(channel);
        }
    }

    public static void receiveMessage(Message message, MessageChannel channel) {
        channelList.update(message);
        if (chatWindows.containsKey(channel.getId())) {                //If the chat is currently open in the UI
            chatWindows.get(channel.getId()).receiveMessage(message);  //Update the chat with the received message
            if (message.getAuthor().getName() != jda.getSelfUser().getName() &&
                    !(chatWindow.isVisible() && chatWindow.getContentPane().equals(chatWindows.get(channel.getId())))) {
                notificationSound.stop();
                notificationSound.setFramePosition(0);
                notificationSound.start();
            }
            if (bubbles.containsKey(channel.getId()) && message.getAuthor().getName() != jda.getSelfUser().getName() &&
                    !(chatWindow.getContentPane().equals(chatWindows.get(channel.getId())) && chatWindow.isVisible())) {
                bubbles.get(channel.getId()).updateUnread();
            }
        } else if (!(channel instanceof TextChannel)) {
            UIChat chat = getChat(channel);
            addBubble(channel);
            chatWindows.put(channel.getId(), chat);
            bubbles.get(channel.getId()).updateUnread();
            notificationSound.stop();
            notificationSound.setFramePosition(0);
            notificationSound.start();
        }
        channelList.revalidate();
        channelList.repaint();
        channelWindow.revalidate();
        channelWindow.repaint();
    }

    /**
     * Adds a bubble to the bubble list for a given channel
     * Clicking the button will open the given channel inside a UIChat inside chatWindow
     * @param channel the channel to add a bubble for
     */
    public static void addBubble(MessageChannel channel) {
        UIFloatingButton button = new UIFloatingButton(channel);
        bubbles.put(channel.getId(), button);
        bubblePane.add(button, gbc, 0);

        resizeBubbles();
    }

    public static void removeBubble(MessageChannel channel) {
        if (bubbles.containsKey(channel.getId())) {
            bubblePane.remove(bubbles.get(channel.getId()));
            bubbles.remove(channel.getId());
        }

        resizeBubbles();
    }

    /**
     * Resizes the bubble window to appropriately fit the current number of bubbles
     */
    private static void resizeBubbles() {
        int tmp = bubbleWindow.getHeight();
        bubbleWindow.pack();

        bubbleWindow.setLocation(bubbleWindow.getX(), bubbleWindow.getY() + (tmp-bubbleWindow.getHeight()));    //Move the bubbles down by however much the height increased

        bubbleWindow.revalidate();
        bubbleWindow.repaint();

        repositionWindows();
    }

    /**
     * Returns an image for a generic message channel
     * @param channel The channel to retrieve an image for
     * @return either a profile image, Guild icon, or group icon depending on the channel type
     */
    public static ImageIcon getImage(MessageChannel channel) {
        if (channel instanceof PrivateChannel)
            return getImage((PrivateChannel) channel);
        else if (channel instanceof TextChannel) {
            TextChannel tc = (TextChannel) channel;
            return getImage(tc.getGuild());
        }
        else if (channel instanceof Group)
            return getImage((Group) channel);
        else
            return null;
    }

    /**
     * @param guild The guild to get an icon for
     * @return The guild's icon, or if no icon is found, the default user icon
     */
    public static ImageIcon getImage(Guild guild) {
        if (chatIcons.containsKey(guild.getIconId()))
            return chatIcons.get(guild.getIconId());
        else {
            try {
                ImageIcon image = getCircularImageFromURL(new URL(guild.getIconUrl()));
                chatIcons.put(guild.getIconId(), image);
                return image;
            } catch (MalformedURLException e) {
                return defaultUserIcon;
            } catch (IOException e) {
                return defaultUserIcon;
            }
        }
    }

    /**
     * @param group The group to get an icon for
     * @return The group's icon, or if no icon is found, the default user icon
     */
    public static ImageIcon getImage(Group group) {
        //TODO construct icon from users in group?
        if (chatIcons.containsKey(group.getIconId()))
            return chatIcons.get(group.getIconId());
        else {
            try {
                ImageIcon image = getImageFromURL(new URL(group.getIconUrl()));
                chatIcons.put(group.getIconId(), image);
                return image;
            } catch (MalformedURLException e) {
                return defaultGroupIcon;
            } catch (IOException e) {
                return defaultGroupIcon;
            }
        }
    }

    /**
     * @param privateChannel the private channel to get an icon for
     * @return the icon of the user the private channel communicates with
     */
    public static ImageIcon getImage(PrivateChannel privateChannel) {
        return getImage(privateChannel.getUser());
    }

    /**
     * @param user the user to get an icon for
     * @return the icon of the user or the default user icon
     */
    public static ImageIcon getImage(User user) {
        if (chatIcons.containsKey(user.getAvatarId()))
            return chatIcons.get(user.getAvatarId());
        else {
            try {
                ImageIcon image = getCircularImageFromURL(new URL(user.getAvatarUrl()));
                chatIcons.put(user.getAvatarId(), image);
                return image;
            } catch (MalformedURLException e) {
                return defaultUserIcon;
            } catch (IOException e) {
                return defaultUserIcon;
            }
        }
    }

    /**
     * Returns a user icon with a given size
     * @param user the user to get an icon for
     * @param width the width of the desired icon
     * @param height the height of the desired icon
     * @return a resized user icon
     */
    public static ImageIcon getImage(User user, int width, int height) {
        Image image = getImage(user).getImage();
        image = image.getScaledInstance(width,height,Image.SCALE_SMOOTH);
        return new ImageIcon(image);
    }

    /**
     * Returns an image from a URL
     * @param url the url of the image
     * @return the image located at the given URL
     * @throws IOException
     */
    public static ImageIcon getImageFromURL(URL url) {
        return new ImageIcon(url);
    }

    /**
     * Returns an image from a URL clipped to a circle
     * @param url the url of the image
     * @return a circular cropped image located at the given URL
     * @throws IOException
     */
    public static ImageIcon getCircularImageFromURL(URL url) throws IOException {
        final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty(
                "User-Agent",
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_5) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31");
        final BufferedImage image = ImageIO.read(connection.getInputStream());
        return new ImageIcon(clipToCircle(image));
    }

    /**
     * Retrieve an image from the filesystem
     * @param path the path to the image
     * @return the image at the given path
     */
    public static ImageIcon getImageFromFile(URL path) {
        return new ImageIcon(path);
    }

    /**
     * Retrieve a scaled image from the filesystem
     * @param path the path to the image
     * @param width the desired width of the image
     * @param height the desired height of the image
     * @return the image at the given path with the given width and height
     */
    public static ImageIcon getScaledImageFromFile(URL path, int width, int height) {
        Image image = getImageFromFile(path).getImage();
        image = image.getScaledInstance(width,height,Image.SCALE_SMOOTH);
        return new ImageIcon(image);
    }

    public static ImageIcon resizeToWidth(ImageIcon image, int width) {
        float ratio = (float) image.getIconHeight()/image.getIconWidth();
        int height = (int) (ratio * width);
        return new ImageIcon(image.getImage().getScaledInstance(width,height,Image.SCALE_SMOOTH));
    }

    public static ImageIcon resizeToWidthAnimated(ImageIcon image, int width) {
        float ratio = (float) image.getIconHeight()/image.getIconWidth();
        int height = (int) (ratio * width);
        return new ImageIcon(image.getImage().getScaledInstance(width,height,Image.SCALE_DEFAULT));
    }

    public static ImageIcon getDownloadIcon() { return downloadIcon; }

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

    public static ImageIcon getDefaultUserIcon() { return defaultUserIcon; }

    /**
     * Sets the logged in user's status
     * @param status the status to set the user to
     */
    public static void setStatus(OnlineStatus status) {
        jda.getPresence().setStatus(status);
    }
}
