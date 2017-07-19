package com.transparentdiscord.UI;

import com.transparentdiscord.Main;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static java.lang.System.out;

/**
 * Created by liam on 6/23/17.
 * Represents an abstract chat interface
 */
public abstract class UIChat extends JPanel {
    protected JPanel            messageList;    //The list of messages, will contain UIMessage components
    protected JPanel            messageCompose; //The panel containing message composition elements (text entry, image preview, etc)
    protected JScrollPane       scrollPane;     //Allows the user to scroll through messages
    protected JScrollBar        vertScrollBar;  //The scroll bar of the message list
    protected JTextField        messageField;   //The text field where the user will enter messages to send
    protected MessageChannel    channel;        //The message channel this UI is responsible for displaying

    protected Clipboard         clipboard;      //The system clipboard
    protected InputStream       attachment;     //A potential attachment, used for images located in the clipboard
    protected File              fileAttachment; //A potential attachment, used for pasted files
    protected Image             attachmentPreview;//Stores a preview of the current attachment

    private int                 tmpScrollValue; //Stores the previous maximum value before update of the vertical scroll bar
    private boolean             fixScroll;      //a boolean to keep track of the state of the scrollbar while loading older messages
    protected boolean           doneLoad;       //prevents the UI from loading older messages before the initial messages have loaded

    public UIChat() {
        setLayout(new BorderLayout());

        clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

        fixScroll = false;
        doneLoad = false;

        attachment = null;
        fileAttachment = null;

        messageList = new JPanel(new GridBagLayout());

        scrollPane = new JScrollPane(messageList);
        add(scrollPane);

        vertScrollBar = scrollPane.getVerticalScrollBar();

        messageCompose = new JPanel(new BorderLayout());

        messageField = new JTextField();
        messageField.setFont(Main.defaultFont.deriveFont(Font.PLAIN, 12));
        messageField.addActionListener(actionEvent -> { //Send a message and clear the field's text when the user presses 'enter'
            sendMessage(messageField.getText());
            messageField.setText("");
        });

        messageField.setTransferHandler(null);

        messageField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent keyEvent) { //Close the chat window if the user presses escape
                channel.sendTyping().queue();
                if (keyEvent.getKeyCode() == KeyEvent.VK_ESCAPE)
                    Main.chatWindow.setVisible(false);
                if (keyEvent.isControlDown() && keyEvent.getKeyCode() == KeyEvent.VK_V) {
                    String mimetype = clipboard.getAvailableDataFlavors()[0].getMimeType();
                    try {
                        if (mimetype.contains("text/plain")) {
                            String pastedText = (String) clipboard.getContents(null).getTransferData(DataFlavor.stringFlavor);
                            messageField.setText(messageField.getText() + pastedText);
                        }
                        else if (mimetype.contains("text/uri-list")) {
                            List<File> files = (List) clipboard.getContents(null).getTransferData(DataFlavor.javaFileListFlavor);
                            fileAttachment = files.get(0);
                            try {
                                addAttachmentPreview(ImageIO.read(fileAttachment));
                            } catch (Exception e) {
                                //TODO set preview to default file icon
                            }
                            attachment = null;
                        }
                        else {
                            DataFlavor pngFlavor = new DataFlavor("image/png; class=java.io.InputStream");
                            attachment = (InputStream) clipboard.getContents(null).getTransferData(pngFlavor);
                            addAttachmentPreview((Image) clipboard.getContents(null).getTransferData(DataFlavor.imageFlavor));

                            fileAttachment = null;
                        }
                    } catch (UnsupportedFlavorException e) {
                        out.println("Pasted data not supported...");
                    } catch (IOException e) {
                        out.println("IO Failed...");
                    } catch (ClassNotFoundException e) {
                        out.println("Failed to create DataFlavor...");
                    }
                }
            }
        });

        messageCompose.add(messageField, BorderLayout.SOUTH);
        add(messageCompose, BorderLayout.SOUTH); //Add messageField at the bottom of UIChat, below the message list

        vertScrollBar.setUnitIncrement(16);
        vertScrollBar.addAdjustmentListener(new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                Adjustable adjustable = e.getAdjustable();
                if (adjustable.getValue() == 0 && !fixScroll && doneLoad) {
                    tmpScrollValue = adjustable.getMaximum();
                    fixScroll = true;
                    loadMessageHistory();
                } else if (fixScroll) {
                    adjustable.setValue(adjustable.getMaximum()-tmpScrollValue);
                    fixScroll = false;
                }
            }
        });
    }

    /**
     * Sends a message in the channel
     * @param message a string containing the message to send
     */
    protected void sendMessage(String message) {
        MessageBuilder builder = new MessageBuilder();
        builder.append(message);
        if (attachment != null) {
            String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
            channel.sendFile(attachment, "clipboard-"+timeStamp+".png", builder.build()).queue();

            BorderLayout layout = (BorderLayout) messageCompose.getLayout();
            messageCompose.remove(layout.getLayoutComponent(BorderLayout.CENTER));
        }
        else if (fileAttachment != null) {
            try {
                channel.sendFile(fileAttachment, builder.build()).queue();
            } catch (IOException e) {
                out.println("Failed to send file...");
            }

            BorderLayout layout = (BorderLayout) messageCompose.getLayout();
            messageCompose.remove(layout.getLayoutComponent(BorderLayout.CENTER));
        }
        else
            channel.sendMessage(builder.build()).queue();

        messageField.setText("");

        attachment = null;
        fileAttachment = null;

        refresh();
    }

    private void addAttachmentPreview(Image image) {
        ImageIcon preview = Main.resizeToWidth(new ImageIcon(image),Main.getChatWidth()-10);
        JLabel previewLabel = new JLabel(preview);
        previewLabel.setBorder(new EmptyBorder(5,5,5,5));
        previewLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                BorderLayout layout = (BorderLayout) messageCompose.getLayout();
                messageCompose.remove(layout.getLayoutComponent(BorderLayout.CENTER));

                attachment = null;
                fileAttachment = null;

                refresh();
            }
        });
        previewLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        messageCompose.add(previewLabel,BorderLayout.CENTER);
        refresh();
    }

    /**
     * Updates this UI with a received message
     * @param message the message to update the UI with
     */
    public abstract void receiveMessage(Message message);

    /**
     * Loads past messages and adds them to the list view
     */
    protected abstract void loadMessageHistory();

    /**
     * Refreshes the window
     */
    protected void refresh() {
        messageList.repaint();
        messageList.revalidate();
        scrollPane.repaint();
        scrollPane.revalidate();
        repaint();
        revalidate();
    }

    /**
     * @return the MessageChannel this UI is responsible for displaying to the user
     */
    public MessageChannel getChannel() { return this.channel; }

    /**
     * Scrolls the ScrollPane to the bottom
     */
    protected void scrollToBottom() {
        scrollPane.getViewport().setViewPosition(new Point(0,Integer.MAX_VALUE));
    }
}
