package com.hiveworkshop.pkb.util;

import com.hiveworkshop.pkb.TestUtils;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test suite for {@link ColorSwap}.
 */
class ColorSwapTest extends TestUtils {

    /**
     * Tests {@link ColorSwap#colorize(File, Color)}.
     */
    @Test
    void testColorize() {
        test("vfx_meteor.particles", true);
        test("vfx_fireball_trail.particles", true);
        test("fx_ice_blizzard_missile.particles", true);
        test("fx_ice_frost_nova.particles", true);
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
