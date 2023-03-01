package com.hiveworkshop.pkb;

import java.io.File;
import java.util.Objects;

/**
 * Common test class.
 */
public class TestUtils {

    public File loadFile(String path) {
        ClassLoader classLoader = this.getClass().getClassLoader();
        return new File(Objects.requireNonNull(classLoader.getResource(path)).getFile());
    }
}
