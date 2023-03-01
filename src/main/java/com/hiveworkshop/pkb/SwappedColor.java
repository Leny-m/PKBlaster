package com.hiveworkshop.pkb;

import java.awt.*;
import java.io.Serial;
import java.io.Serializable;

public record SwappedColor(Color previousValue, Color newValue) implements Serializable {
    @Serial
    private static final long serialVersionUID = -4126242909227896131L;

    public boolean isChanged() {
        return !previousValue.equals(newValue);
    }
}