package com.hiveworkshop.pkb;

import java.awt.*;
import java.io.Serial;
import java.io.Serializable;

/**
 * A record for 2 colors that are being swapped and the info about the chunk they are in.
 *
 * @param previousValue to be swapped for the new value
 * @param newValue      to replace the previous value
 * @param info          about the chunk the swap is happening in
 */
public record SwappedColor(Color previousValue, Color newValue, String info) implements Serializable {
    @Serial
    private static final long serialVersionUID = -4126242909227896131L;

    public boolean isChanged() {
        return !previousValue.equals(newValue);
    }
}