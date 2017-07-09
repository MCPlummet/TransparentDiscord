package com.transparentdiscord.UI;

import com.transparentdiscord.Main;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import static java.lang.System.out;

/**
 * Created by liam on 6/23/17.
 * Represents an abstract chat interface
 */
public abstract class UIChat extends JPanel {
    protected JPanel            messageList;    //The list of messages, will contain UIMessage components
    protected JScrollPane       scrollPane;     //Allows the user to scroll through messages
    protected JScrollBar        vertScrollBar;  //The scroll bar of the message list
    protected JTextField        messageField;   //The text field where the user will enter messages to send
    protected MessageChannel    channel;        //The message channel this UI is responsible for displaying
    private AdjustmentListener  downScroll;     //Added to vertScrollBar to scroll the ScrollPane to the bottom
    private int                 tmpScrollValue; //Stores the previous maximum value before update of the vertical scroll bar
    private boolean             fixScroll;      //a boolean to keep track of the state of the scrollbar while loading older messages

    public UIChat() {
        setLayout(new BorderLayout());

        fixScroll = false;

        messageList = new JPanel(new GridBagLayout());

        scrollPane = new JScrollPane(messageList);
        add(scrollPane);

        vertScrollBar = scrollPane.getVerticalScrollBar();

        messageField = new JTextField();
        messageField.addActionListener(actionEvent -> { //Send a message and clear the field's text when the user presses 'enter'
            sendMessage(messageField.getText());
            messageField.setText("");
        });

        messageField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent keyEvent) { //Close the chat window if the user presses escape
                if (keyEvent.getKeyCode() == KeyEvent.VK_ESCAPE)
                    Main.chatWindow.setVisible(false);
            }
        });

        add(messageField, BorderLayout.SOUTH); //Add messageField at the bottom of UIChat, below the message list

        downScroll = new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                Adjustable adjustable = e.getAdjustable();
                adjustable.setValue(adjustable.getMaximum());
                vertScrollBar.removeAdjustmentListener(this);
            }
        };

        vertScrollBar.setUnitIncrement(16);
        vertScrollBar.addAdjustmentListener(new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                Adjustable adjustable = e.getAdjustable();
                if (adjustable.getValue() == 0 && !fixScroll) {
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
    protected abstract void sendMessage(String message);

    /**
     * Updates this UI with a received message
     * @param message the message to update the UI with
     */
    public abstract void receiveMessage(Message message);

    /**
     * Loads past messages and adds them to the list view
     */
    protected abstract void loadMessageHistory();

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
        vertScrollBar.addAdjustmentListener(downScroll);
    }
}
