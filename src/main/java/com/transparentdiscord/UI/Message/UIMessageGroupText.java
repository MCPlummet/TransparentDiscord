package com.transparentdiscord.UI.Message;

import com.transparentdiscord.TransparentDiscord;
import net.dv8tion.jda.core.entities.Message;

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
        setLineWrap(true);
        setWrapStyleWord(true);
        setOpaque(false);
        setEditable(false);
    }

    public Message getMessage() { return message; }

    public UIMessageGroup getParent() { return parent; }

}
