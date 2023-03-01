package com.hiveworkshop.pkb;

import com.hiveworkshop.pkb.model.Constants;
import com.hiveworkshop.pkb.util.ColorSwap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * Main runnable class.
 */
public class PKBMain {
    private static final Logger LOGGER = LoggerFactory.getLogger(PKBMain.class);

    public static void main(final String[] args) {
        if (args.length == 0) {
            runGUI();
        } else if (args.length == 2) {
            colorize(args[0], args[1]);
        } else {
            showUsage();
        }
    }

    private static void colorize(String absoluteFilePath, String colorHex) {
        File file = new File(absoluteFilePath);
        if (file.exists() && file.isFile()) {
            if (colorHex.startsWith("#") && colorHex.length() == 7) {
                Color color = Color.decode(colorHex);
                ColorSwap.colorize(file, color);
            } else {
                LOGGER.error("Expected color hex format: #RRGGBB.");
            }
        } else {
            LOGGER.error("File {} not found.", absoluteFilePath);
        }
    }

    private static void showUsage() {
        LOGGER.info("Usage: java -jar PKBlaster.jar <absolute_file_path> <color_hex>" +
                "\nExample: java -jar PKBlaster.jar \"C:\\Program Files (x86)\\Diablo II Resurrected\\Work\\data\\data\\hd\\vfx\\particles\\missiles\\fireball\\vfx_fireball_trail.particles\" #00FF00");
    }

    private static void runGUI() {
        SwingUtilities.invokeLater(() -> {
            JFrame mainFrame = new JFrame(String.format("PKBlaster Editor (%s) modified by Leny", Constants.VERSION));
            mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            PKBPanel panel = new PKBPanel();
            mainFrame.setContentPane(panel);
            mainFrame.setJMenuBar(panel.createJMenuBar());
            mainFrame.setBounds(new Rectangle(800, 600));
            mainFrame.setLocationRelativeTo(null);
            mainFrame.setVisible(true);
        });
    }
}
