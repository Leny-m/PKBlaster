package com.hiveworkshop.wc3.units.objectdata;

import com.hiveworkshop.wc3.util.CharInt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public record War3ID(int value) implements Comparable<War3ID> {
	private static final Logger LOGGER = LoggerFactory.getLogger(War3ID.class);

	public static War3ID fromString(String idString) {
		if (idString.length() == 3) {
			LOGGER.debug(
					"Loaded custom data for the ability CURSE whose MetaData field, 'Crs', is the only 3 letter War3ID in the game. This might cause unexpected errors, so watch your % chance to miss in custom curse abilities carefully.");
			idString += '\0';
		}
		if (idString.length() != 4) {
			throw new IllegalArgumentException(
					"A War3ID must be 4 ascii characters in length (got " + idString.length() + ") '" + idString + "'");
		}
		return new War3ID(CharInt.toInt(idString));
	}

	public String asStringValue() {
		String string = CharInt.toString(value);
		if (((string.charAt(3) == '\0') || (string.charAt(3) == ' ')) && (string.charAt(2) != '\0')) {
			string = string.substring(0, 3);
		}
		return string;
	}

	public War3ID set(final int index, final char c) {
		final String asStringValue = asStringValue();
		String result = asStringValue.substring(0, index);
		result += c;
		result += asStringValue.substring(index + 1, asStringValue.length());
		return War3ID.fromString(result);
	}

	public char charAt(final int index) {
		return (char) ((value >>> ((3 - index) * 8)) & 0xFF);
	}

	@Override
	public String toString() {
		return asStringValue();
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final War3ID other = (War3ID) obj;
		if (value != other.value) {
			return false;
		}
		return true;
	}

	@Override
	public int compareTo(final War3ID o) {
		return Integer.compare(value, o.value);
	}
}
