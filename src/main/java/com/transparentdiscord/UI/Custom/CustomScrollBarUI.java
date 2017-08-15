package com.transparentdiscord.UI.Custom;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;

/**
 * Created by liam on 8/14/17.
 */
public class CustomScrollBarUI extends BasicScrollBarUI {

    public CustomScrollBarUI() {
        scrollBarWidth = 8;
        thumbColor = Color.black;
        trackColor = new Color(0,0,0,0);

    }

    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
        super.paintTrack(g,c,trackBounds);
    }

    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
        super.paintThumb(g,c,thumbBounds);
    }

    @Override
    protected JButton createDecreaseButton(int orientation) {
        return createNoButton();
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        return createNoButton();
    }

    private JButton createNoButton() {
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(0, 0));
        button.setMinimumSize(new Dimension(0, 0));
        button.setMaximumSize(new Dimension(0, 0));
        return button;
    }

}
