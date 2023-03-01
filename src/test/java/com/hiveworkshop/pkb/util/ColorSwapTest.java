package com.hiveworkshop.pkb.util;

import com.hiveworkshop.pkb.PKBParser;
import com.hiveworkshop.pkb.TestUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test suite for {@link ColorSwap}.
 */
class ColorSwapTest extends TestUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(ColorSwapTest.class);

    public static final String METEOR = "vfx_meteor.particles";
    public static final String FIREBALL = "vfx_fireball_trail.particles";
    public static final String BLIZZARD = "fx_ice_blizzard_missile.particles";
    public static final String FROST_NOVA = "fx_ice_frost_nova.particles";

    /**
     * Tests {@link ColorSwap#colorize(File, Color)}.
     */
    @Test
    void testColorize() {
        test(METEOR, true);
        test(FIREBALL, true);
        test(BLIZZARD, true);
        test(FROST_NOVA, true);
    }

    /**
     * Tests {@link ColorSwap#colorize(File, Color)}.
     */
    @Test
    void testColorizeSingle() {
        test(FROST_NOVA, true);
    }

    @Test
    void decodeFile() throws IOException {
        File file = loadFile(FROST_NOVA);
        PKBParser pkbParser;
        ByteBuffer byteBuffer = ByteBuffer.allocate((int) file.length());
        try (FileChannel channel = FileChannel.open(file.toPath(), StandardOpenOption.READ)) {
            channel.read(byteBuffer);
            byteBuffer.clear();
            pkbParser = new PKBParser(byteBuffer);
            List<String> strings = pkbParser.getStrings();
            strings.forEach(LOGGER::debug);
        }
    }

    private void test(String filePath, boolean expectedResult) {
        // data
        File file = loadFile(filePath);

        // api call
        boolean success = ColorSwap.colorize(file, Color.RED);

        // validation
        assertThat(success).isEqualTo(expectedResult);
    }
}
