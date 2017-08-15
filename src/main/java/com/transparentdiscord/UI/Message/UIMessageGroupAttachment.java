package com.transparentdiscord.UI.Message;

import com.transparentdiscord.TransparentDiscord;
import net.dv8tion.jda.core.entities.Message;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Created by liam on 7/29/17.
 */
public class UIMessageGroupAttachment extends JPanel {

    UIMessageGroupAttachment(Message message) {
        for (Message.Attachment a : message.getAttachments()) {
            if (a.isImage()) {
                try {
                    //Get the image from the URL and resize it to the width of the chat window
                    ImageIcon image = TransparentDiscord.getImageFromURL(new URL(a.getUrl()));

                    //If the image is animated, we can't use smooth scaling
                    if (a.getUrl().contains(".gif")) image = TransparentDiscord.resizeToWidthAnimated(image, TransparentDiscord.getChatWidth()-80);
                    else image = TransparentDiscord.resizeToWidth(image, TransparentDiscord.getChatWidth()-80);

                    JLabel label = new JLabel(image);
                    image.setImageObserver(this);
                    add(label);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else {
                JLabel label = new JLabel(a.getFileName(),
                        TransparentDiscord.resizeToWidth(TransparentDiscord.getDownloadIcon(),25), JLabel.LEFT);
                label.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        JFileChooser chooser = new JFileChooser();
                        chooser.setSelectedFile(new File(System.getProperty("user.home") +"/" + a.getFileName()));
                        int response = chooser.showSaveDialog(null);
                        if (response == JFileChooser.APPROVE_OPTION)
                            a.download(chooser.getSelectedFile());
                    }
                });
                label.setCursor(new Cursor(Cursor.HAND_CURSOR));
                add(label);
            }
        }
    }
}
