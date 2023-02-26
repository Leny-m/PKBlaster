package com.hiveworkshop.pkb.util;

import com.hiveworkshop.pkb.*;
import com.hiveworkshop.wc3.gui.ExceptionPopup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Utility class used for color swapping.
 */
public class ColorSwap {
    private ColorSwap() {
        throw new IllegalStateException("Class doesn't support instantiation.");
    }
    private static final Logger LOGGER = LoggerFactory.getLogger(ColorSwap.class);

    public static void colorize(HorriblePkbParser currentPKB, Color currentColorizeColor, Component parentComponent) {
        try {
            if (currentPKB == null) {
                JOptionPane.showMessageDialog(parentComponent, "No file loaded!", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                final List<SwappedColor> swappedColors = new ArrayList<>();
                for (final PKBChunk chunk : currentPKB.getChunks()) {
                    if (chunk instanceof final UnknownChunk c) {
                        final String chunkTypeName = currentPKB.getStrings().get(c.getChunkType());
                        if (chunkTypeName.equals("CParticleNodeSamplerData_Curve")) {
                            final ByteBuffer data = c.getChunkData();
                            data.order(ByteOrder.LITTLE_ENDIAN);
                            data.clear();
                            final short groupCount = data.getShort();
                            LOGGER.debug("Curve with groupCount={}", groupCount);
                            for (int groupIndex = 0; groupIndex < groupCount; groupIndex++) {
                                final short groupType = data.getShort();
                                LOGGER.debug("\tGroup{}", groupType);
                                switch (groupType) {
                                    case 0, 1 -> {
                                        final int unknown1 = data.getInt();
                                        final int unknown2 = data.getInt();
                                        final int nameStringIndex = data.getInt();
                                        LOGGER.debug("\t\tUnknown1:{}", unknown1);
                                        LOGGER.debug("\t\tUnknown2:{}", unknown2);
                                        LOGGER.debug("\t\tName:{}", currentPKB.getStrings().get(nameStringIndex));
                                    }
                                    case 7 -> {
                                        final int unknown = data.getInt();
                                        final int propertyIndex = data.getInt();
                                        LOGGER.debug("\t\tUnknown:{}", unknown);
                                        LOGGER.debug("\t\tPropertyIndex:{}", propertyIndex);
                                    }
                                    case 9 -> {
                                        final int unknown = data.getInt();
                                        LOGGER.debug("\t\tUnknown:{}", unknown);
                                    }
                                    case 10 -> {
                                        final int unknown = data.getInt();
                                        LOGGER.debug("\t\tUnknown:{}", unknown);
                                    }
                                    case 11 -> {
                                        final int unknown = data.getInt();
                                        LOGGER.debug("\t\tUnknown:{}", unknown);
                                    }
                                    case 14 -> {
                                        final int unknown = data.getInt();
                                        LOGGER.debug("\t\tUnknown:{}", unknown);
                                    }
                                    case 16 -> {
                                        final int numberOfFloats = data.getInt();
                                        LOGGER.debug("\t\tnumberOfFloats:{}", numberOfFloats);
                                        final float[] floats = new float[numberOfFloats];
                                        for (int i = 0; i < numberOfFloats; i++) {
                                            floats[i] = data.getFloat();
                                        }
                                        LOGGER.debug("\t\t:{}", Arrays.toString(floats));
                                    }
                                    case 17 -> {
                                        final int numberOfFloats = data.getInt();
                                        LOGGER.debug("\t\tnumberOfFloats:{}", numberOfFloats);
                                        if (numberOfFloats == 12) {
                                            final int floatStartPos = data.position();
                                            for (int i = 0; i < 3; i++) {
                                                final float oldRed = data
                                                        .getFloat(floatStartPos + i * 16);
                                                final float oldGreen = data
                                                        .getFloat(floatStartPos + i * 16 + 4);
                                                final float oldBlue = data
                                                        .getFloat(floatStartPos + i * 16 + 8);
                                                final float avgColor = (oldRed + oldGreen + oldBlue) / 3;
                                                final float newFactor = Math.signum(avgColor) * Math.max(
                                                        Math.max(Math.abs(oldRed), Math.abs(oldGreen)),
                                                        Math.abs(oldBlue));

                                                final float oldAlpha = data
                                                        .getFloat(floatStartPos + i * 16 + 12);
                                                final float newRed = newFactor * currentColorizeColor.getRed()
                                                        / 255f;
                                                data.putFloat(floatStartPos + i * 16, newRed);
                                                final float newGreen = newFactor
                                                        * currentColorizeColor.getGreen() / 255f;
                                                data.putFloat(floatStartPos + i * 16 + 4, newGreen);
                                                final float newBlue = newFactor
                                                        * currentColorizeColor.getBlue() / 255f;
                                                data.putFloat(floatStartPos + i * 16 + 8, newBlue);
                                                swappedColors.add(new SwappedColor(
                                                        createColor(oldRed, oldGreen, oldBlue, oldAlpha),
                                                        createColor(newRed, newGreen, newBlue, oldAlpha)));

                                            }
                                            data.position(floatStartPos + 3 * 16);
                                        } else {
                                            final float[] floats = new float[numberOfFloats];
                                            for (int i = 0; i < numberOfFloats; i++) {
                                                floats[i] = data.getFloat();
                                            }
                                            LOGGER.debug("\t\t:{}", Arrays.toString(floats));
                                        }
                                    }
                                    case 18 -> {
                                        final int numberOfFloats = data.getInt();
                                        LOGGER.debug("\t\tnumberOfFloats:{}", numberOfFloats);
                                        if (numberOfFloats == 24) {
                                            final int floatStartPos = data.position();
                                            for (int i = 0; i < 6; i++) {
                                                final float oldRed = data
                                                        .getFloat(floatStartPos + i * 16);
                                                final float oldGreen = data
                                                        .getFloat(floatStartPos + i * 16 + 4);
                                                final float oldBlue = data
                                                        .getFloat(floatStartPos + i * 16 + 8);
                                                final float avgColor = (oldRed + oldGreen + oldBlue) / 3;
                                                final float newFactor = Math.signum(avgColor) * Math.max(
                                                        Math.max(Math.abs(oldRed), Math.abs(oldGreen)),
                                                        Math.abs(oldBlue));

                                                final float oldAlpha = data
                                                        .getFloat(floatStartPos + i * 16 + 12);
                                                final float newRed = newFactor * currentColorizeColor.getRed()
                                                        / 255f;
                                                data.putFloat(floatStartPos + i * 16, newRed);
                                                final float newGreen = newFactor
                                                        * currentColorizeColor.getGreen() / 255f;
                                                data.putFloat(floatStartPos + i * 16 + 4, newGreen);
                                                final float newBlue = newFactor
                                                        * currentColorizeColor.getBlue() / 255f;
                                                data.putFloat(floatStartPos + i * 16 + 8, newBlue);
                                                swappedColors.add(new SwappedColor(
                                                        createColor(oldRed, oldGreen, oldBlue, oldAlpha),
                                                        createColor(newRed, newGreen, newBlue, oldAlpha)));
                                            }
                                            data.position(floatStartPos + 24 * 4);
                                        } else if (numberOfFloats == 20) {
                                            final int floatStartPos = data.position();
                                            for (int i = 0; i < 5; i++) {
                                                final float oldRed = data
                                                        .getFloat(floatStartPos + i * 16);
                                                final float oldGreen = data
                                                        .getFloat(floatStartPos + i * 16 + 4);
                                                final float oldBlue = data
                                                        .getFloat(floatStartPos + i * 16 + 8);
                                                final float avgColor = (oldRed + oldGreen + oldBlue) / 3;
                                                final float newFactor = Math.signum(avgColor) * Math.max(
                                                        Math.max(Math.abs(oldRed), Math.abs(oldGreen)),
                                                        Math.abs(oldBlue));

                                                final float oldAlpha = data
                                                        .getFloat(floatStartPos + i * 16 + 12);
                                                final float newRed = newFactor * currentColorizeColor.getRed()
                                                        / 255f;
                                                data.putFloat(floatStartPos + i * 16, newRed);
                                                final float newGreen = newFactor
                                                        * currentColorizeColor.getGreen() / 255f;
                                                data.putFloat(floatStartPos + i * 16 + 4, newGreen);
                                                final float newBlue = newFactor
                                                        * currentColorizeColor.getBlue() / 255f;
                                                data.putFloat(floatStartPos + i * 16 + 8, newBlue);
                                                swappedColors.add(new SwappedColor(
                                                        createColor(oldRed, oldGreen, oldBlue, oldAlpha),
                                                        createColor(newRed, newGreen, newBlue, oldAlpha)));
                                            }
                                            data.position(floatStartPos + 20 * 4);
                                        } else {
                                            final float[] floats = new float[numberOfFloats];
                                            for (int i = 0; i < numberOfFloats; i++) {
                                                floats[i] = data.getFloat();
                                            }
                                            LOGGER.debug("\t\t:{}", Arrays.toString(floats));
                                        }
                                    }
                                    case 19 -> {
                                        final int numberOfFloats = data.getInt();
                                        LOGGER.debug("\t\tnumberOfFloats:{}", numberOfFloats);
                                        final float[] floats = new float[numberOfFloats];
                                        for (int i = 0; i < numberOfFloats; i++) {
                                            floats[i] = data.getFloat();
                                        }
                                        LOGGER.debug("\t\t:{}", Arrays.toString(floats));
                                    }
                                    default -> throw new IllegalStateException(
                                            "Unknown group type in 'CParticleNodeSamplerData_Curve': "
                                                    + groupType);
                                }
                            }
                            data.clear();
                        } else if (currentPKB.getVersion().likelyToUseCLayerCompileCache() && chunkTypeName.equals("CLayerCompileCache")) {
                            final ByteBuffer data = c.getChunkData();
                            data.order(ByteOrder.LITTLE_ENDIAN);
                            data.clear();
                            final short groupCount = data.getShort();
                            LOGGER.debug("CLayerCompileCache with groupCount={}", groupCount);
                            for (int groupIndex = 0; groupIndex < groupCount; groupIndex++) {
                                final short groupType = data.getShort();
                                LOGGER.debug("\tGroup{}", groupType);
                                switch (groupType) {
                                    case 0, 1 -> {
                                        final int numberOfGroups = data.getInt();
                                        LOGGER.debug("\t\tnumberOfGroups:{}", numberOfGroups);
                                        final int floatStartPos = data.position();
                                        for (int i = 0; i < numberOfGroups; i++) {
                                            final float oldRed = data.getFloat(floatStartPos + i * 16);
                                            final float oldGreen = data.getFloat(floatStartPos + i * 16 + 4);
                                            final float oldBlue = data.getFloat(floatStartPos + i * 16 + 8);
                                            final float oldAlpha = data.getFloat(floatStartPos + i * 16 + 12);
                                            if (oldRed == oldGreen && oldGreen == oldBlue) {
                                                continue;
                                            } else if (oldRed > 1.0f || oldGreen > 1.0f || oldBlue > 1.0f
                                                    || oldAlpha > 1.0f) {
                                                continue;
                                            } else if (oldRed > 0.0f && oldRed < 0.001f
                                                    || oldGreen > 0.0f && oldGreen < 0.001f
                                                    || oldBlue > 0.0f && oldBlue < 0.001f
                                                    || oldAlpha > 0.0f && oldAlpha < 0.001f) {
                                                continue;
                                            } else if (oldRed < 0.0f || oldGreen < 0.0f
                                                    || oldBlue < 0.0f) {
                                                continue;
                                            }
                                            final float avgColor = (oldRed + oldGreen + oldBlue) / 3;
                                            final float newFactor = Math.signum(avgColor)
                                                    * Math.max(Math.max(Math.abs(oldRed), Math.abs(oldGreen)),
                                                    Math.abs(oldBlue));

                                            final float newRed = newFactor * currentColorizeColor.getRed()
                                                    / 255f;
                                            data.putFloat(floatStartPos + i * 16, newRed);
                                            final float newGreen = newFactor * currentColorizeColor.getGreen()
                                                    / 255f;
                                            data.putFloat(floatStartPos + i * 16 + 4, newGreen);
                                            final float newBlue = newFactor * currentColorizeColor.getBlue()
                                                    / 255f;
                                            data.putFloat(floatStartPos + i * 16 + 8, newBlue);
                                            swappedColors.add(new SwappedColor(
                                                    createColor(oldRed, oldGreen, oldBlue, oldAlpha),
                                                    createColor(newRed, newGreen, newBlue, oldAlpha)));
                                        }
                                        data.position(floatStartPos + numberOfGroups * 16);

                                    }
                                    case 2, 3, 5, 7, 8, 9 -> {
                                        final int numberOfInts = data.getInt();
                                        LOGGER.debug("\t\tnumberOfInts:{}", numberOfInts);
                                        final int[] ints = new int[numberOfInts];
                                        for (int i = 0; i < numberOfInts; i++) {
                                            ints[i] = data.getInt();
                                        }
                                        LOGGER.debug("\t\t:{}", Arrays.toString(ints));
                                    }
                                    case 11, 12, 16, 17, 18, 20, 21, 24, 25, 31 -> {
                                        final int unknown = data.getInt();
                                        LOGGER.debug("\t\tUnknown:{}", unknown);
                                    }
                                    case 19 -> {
                                        final int unknown1 = data.getInt();
                                        final int unknown2 = data.getInt();
                                        final int unknown3 = data.getInt();
                                        final int unknown4 = data.getInt();
                                        LOGGER.debug("\t\tUnknown1:{}", unknown1);
                                        LOGGER.debug("\t\tUnknown2:{}", unknown2);
                                        LOGGER.debug("\t\tUnknown3:{}", unknown3);
                                        LOGGER.debug("\t\tUnknown4:{}", unknown4);
                                    }
                                    case 13, 14, 15 -> {
                                        final int unknown = data.get();
                                        LOGGER.debug("\t\tUnknown:{}", unknown);
                                    }
                                    case 27, 28, 30 -> {
                                        final float unknown = data.getFloat();
                                        LOGGER.debug("\t\tUnknown:{}", unknown);
                                    }
                                    default -> throw new IllegalStateException(
                                            "Unknown group type in 'CLayerCompileCache': " + groupType);
                                }
                            }
                            data.clear();
                        }
                    }
                }
                final ColorSwapPreviewPanel colorSwapPreviewPanel = new ColorSwapPreviewPanel(
                        swappedColors);
                final JScrollPane preview = new JScrollPane(colorSwapPreviewPanel);
                preview.setPreferredSize(new Dimension(800, 600));
                JOptionPane.showMessageDialog(parentComponent, preview);
            }
        } catch (final Exception exc) {
            exc.printStackTrace();
            ExceptionPopup.display(exc);
        }
    }

    private static Color createColor(final float oldRed, final float oldGreen, final float oldBlue, final float oldAlpha) {
        return new Color(Math.max(0f, Math.min(1.0f, oldRed)), Math.max(0f, Math.min(1.0f, oldGreen)),
                Math.max(0f, Math.min(1.0f, oldBlue)), Math.max(0f, Math.min(1.0f, oldAlpha)));
    }
}
