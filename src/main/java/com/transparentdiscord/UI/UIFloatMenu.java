package com.transparentdiscord.UI;

import javax.swing.*;
import java.awt.*;

/**
 * Created by liam on 6/22/17.
 */
public class UIFloatMenu extends JFrame {

    public UIFloatMenu() {
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));

        JPanel panel = new JPanel();

        panel.setLayout(new GridBagLayout());
        panel.setBackground(new Color(0,0,0,0));
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.weighty = 1;
        c.gridx = 0;
        c.gridy = 0;
        c.ipady = 45;
        c.anchor = GridBagConstraints.PAGE_END;

//        panel.add(new UIFloatingButton(this), c);

        c.weightx = 0;
        c.weighty = 0;
        c.gridy = 1;
//        panel.add(new UIFloatingButton(this), c);

        add(panel);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(50,500);
    }

    public static void main(String[] args) {
        JFrame frame = new UIFloatMenu();
        frame.setVisible(true);
    }

}
