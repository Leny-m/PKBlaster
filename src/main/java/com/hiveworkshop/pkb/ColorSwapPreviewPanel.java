package com.hiveworkshop.pkb;

import javax.swing.*;
import java.awt.*;
import java.io.Serial;
import java.util.List;

public class ColorSwapPreviewPanel extends JPanel {

    @Serial
    private static final long serialVersionUID = -3345515612389594964L;

    public ColorSwapPreviewPanel(final List<SwappedColor> swappedColors) {
        setLayout(new GridLayout(swappedColors.size(), 1));
        for (final SwappedColor swappedColor : swappedColors) {
            add(new SingleColorSwapPanel(swappedColor));
        }
    }

    private static final class SingleColorSwapPanel extends JPanel {
        @Serial
        private static final long serialVersionUID = 1793054550731198785L;
        private final SwappedColor swappedColor;

        public SingleColorSwapPanel(final SwappedColor swappedColor) {
            this.swappedColor = swappedColor;
            add(Box.createRigidArea(new Dimension(48 * 3, 48)));
        }

        @Override
        protected void paintComponent(final Graphics g) {
            super.paintComponent(g);
            g.setColor(swappedColor.previousValue());
            g.fillRect(0, 0, 48, 48);

            g.setColor(Color.BLACK);
            g.drawLine(86, 34, 96, 24);
            g.drawLine(48, 24, 96, 24);
            g.drawLine(86, 14, 96, 24);

            g.setColor(swappedColor.newValue());
            g.fillRect(48 * 2, 0, 48, 48);
        }
    }
}
