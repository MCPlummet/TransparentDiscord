package com.transparentdiscord.UI.Message;

import com.transparentdiscord.TransparentDiscord;
import net.dv8tion.jda.core.entities.Message;
import sun.swing.SwingUtilities2;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Created by liam on 7/27/17.
 */
public class UIMessageGroupText extends JTextArea {

    private Message message;    //The message to be displayed

    private UIMessageGroup parent;

    /**
     * Construct a UIMessage from a given message
     * @param message the Message to convert to a UI element
     */
    public UIMessageGroupText(Message message, UIMessageGroup parent) {
        setBorder(new EmptyBorder(5,5,5,5));

        this.message = message;
        this.parent = parent;

        setFont(TransparentDiscord.defaultFont.deriveFont(Font.PLAIN,12));
        setText(message.getContent());

        if (message.getContent().equals("") && message.getAttachments().isEmpty())
            setText(message.getAuthor().getName() + " started a call.");

        setLineWrap(true);
        setWrapStyleWord(true);
        setOpaque(false);
        setEditable(false);
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D graphics2d = (Graphics2D) g;
        graphics2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        super.paintComponent(g);
    }

    public Message getMessage() { return message; }

    public UIMessageGroup getParent() { return parent; }

}
