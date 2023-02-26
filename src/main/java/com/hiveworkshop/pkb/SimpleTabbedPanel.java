package com.hiveworkshop.pkb;

import com.hiveworkshop.pkb.util.ColorSwap;
import com.hiveworkshop.wc3.gui.ExceptionPopup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.Serial;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Simplified version of PKB UI.
 */
public class SimpleTabbedPanel extends JPanel {
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleTabbedPanel.class);
    @Serial
    private static final long serialVersionUID = 4392299392114136745L;

    public SimpleTabbedPanel() {
        init();
    }

    private void init() {
        // components
        JLabel fileLabel = new JLabel();
        add(fileLabel);

        JButton loadFileButton = new JButton("Load file");
        add(loadFileButton);

        JColorChooser colorChooser = new JColorChooser();
        colorChooser.setVisible(false);
        add(colorChooser);

        JLabel colorChangedLabel = new JLabel();
        add(colorChangedLabel);

        JLabel fileSavedLabel = new JLabel();
        add(fileSavedLabel);

        // file chooser
        AtomicReference<File> selectedFile = new AtomicReference<>();
        AtomicReference<HorriblePkbParser> pkb = new AtomicReference<>();
        JFileChooser fileChooser = new JFileChooser();
        loadFileButton.addActionListener(e -> {
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                selectedFile.set(fileChooser.getSelectedFile());
                if (selectedFile.get() != null) {
                    fileLabel.setText(selectedFile.get().getName());
                    colorChooser.setVisible(true);
                    LOGGER.debug("Selected file: {}", selectedFile.get().getAbsolutePath());

                    ByteBuffer stupidBuffer2 = ByteBuffer.allocate((int) selectedFile.get().length());
                    try (FileChannel channel = FileChannel.open(selectedFile.get().toPath(), StandardOpenOption.READ)) {
                        channel.read(stupidBuffer2);
                        stupidBuffer2.clear();
                        pkb.set(new HorriblePkbParser(stupidBuffer2));
                    } catch (IOException e1) {
                        e1.printStackTrace();
                        ExceptionPopup.display(e1);
                    }
                }
            }
        });

        // color chooser
        colorChooser.getSelectionModel().addChangeListener(e -> {
            Color newColor = colorChooser.getColor();
            colorChangedLabel.setText(String.format("Color changed to: %s", colorToRGB(newColor)));
            LOGGER.debug("Color changed to: {}", newColor);

            ColorSwap.colorize(pkb.get(), newColor, getParent());

            ByteBuffer stupidBuffer2 = pkb.get().toBuffer();
            try (FileChannel channel = FileChannel.open(selectedFile.get().toPath(), StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
                channel.truncate(0);
                stupidBuffer2.clear();
                channel.write(stupidBuffer2);
                LOGGER.debug("File {} updated", selectedFile.get().getName());
                fileSavedLabel.setText(String.format("File %s updated", selectedFile.get().getName()));
            } catch (IOException e1) {
                e1.printStackTrace();
                ExceptionPopup.display(e1);
            }
        });
    }

    private String colorToRGB(Color color) {
        return String.format("RGB: %s, %s, %s", color.getRed(), color.getGreen(), color.getBlue());
    }
}
