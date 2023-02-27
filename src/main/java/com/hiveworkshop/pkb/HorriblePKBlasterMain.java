package com.hiveworkshop.pkb;

import javax.swing.*;
import java.awt.*;

/**
 * Main runnable class.
 */
public class HorriblePKBlasterMain {
    public static void main(final String[] args) {
        SwingUtilities.invokeLater(() -> {
            final JFrame mainFrame = new JFrame("PKBlaster Editor (v0.05_RC1) modified by Leny");
            mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            final HorriblePKBlasterPanel panel = new HorriblePKBlasterPanel();
            mainFrame.setContentPane(panel);
            mainFrame.setJMenuBar(panel.createJMenuBar());
            mainFrame.setBounds(new Rectangle(800, 600));
            mainFrame.setLocationRelativeTo(null);
            mainFrame.setVisible(true);
        });
    }
}
