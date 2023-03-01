package com.hiveworkshop.pkb;

import com.hiveworkshop.wc3.units.objectdata.War3ID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.*;

/**
 * Parses the game files.
 */
public class PKBParser implements Serializable {
    private static final Logger LOGGER = LoggerFactory.getLogger(PKBParser.class);
    @Serial
    private static final long serialVersionUID = -4126242909227896130L;
    static final Map<Integer, Set<Integer>> MESSAGE_TYPES_TO_LENS = new TreeMap<>();
    private final PKBVersion pkbVersion;
    private final int someResurrectedIdentifier;
    private final List<String> strings = new ArrayList<>();
    private final List<PKBChunk> chunks = new ArrayList<>();
    private final int[] resurrectBuffer;
    private final int firstMagicIdentifier;
    private final long secondMagicIdentifier;

    public PKBParser(final ByteBuffer buffer) {
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        final int key1 = buffer.getInt();
        final int key2 = buffer.getInt();
        final int key3 = buffer.getInt();
        PKBVersion pkbVersionTmp = null;
        for (PKBVersion possibleVersion : PKBVersion.values()) {
            if (possibleVersion.matchingMagicKeys(key1, key2, key3)) {
                pkbVersionTmp = possibleVersion;
            }
        }
        if (pkbVersionTmp == null) {
            throw new IllegalStateException("Illegal magic keys={" + key1 + ", " + key2 + ", " + key3 + "}");
        }
        this.pkbVersion = pkbVersionTmp;
        firstMagicIdentifier = buffer.getInt();
        if (this.pkbVersion.hasExtraBufferData()) {
            someResurrectedIdentifier = buffer.getInt();
            LOGGER.trace("Some resurrected identifier:{}", someResurrectedIdentifier);
        } else {
            someResurrectedIdentifier = 0;
        }
        final int stringDataOffset = buffer.getInt();
        final int initialPosition = buffer.position();
        buffer.position(stringDataOffset);
        final int stringCount = buffer.getInt();
        for (int n = 0; n < stringCount; n++) {
            final int maybeTextureLength = buffer.get();
            final StringBuilder sb = new StringBuilder();
            for (int i = 0; i < maybeTextureLength; i++) {
                sb.append((char) buffer.get());
            }
            strings.add(sb.toString());
        }
        LOGGER.trace("Finished reading strings at:{}", buffer.position());
        LOGGER.trace("Finished with remaining:{}", buffer.remaining());
        buffer.position(initialPosition);
        if (this.pkbVersion.hasExtraBufferData()) {
            secondMagicIdentifier = buffer.getInt();
        } else {
            secondMagicIdentifier = buffer.getLong();
        }
        LOGGER.trace("firstmagic:{}", firstMagicIdentifier);
        LOGGER.trace("strings offset:{}", stringDataOffset);
        LOGGER.trace("secondmagic:{}", secondMagicIdentifier);
        if (this.pkbVersion.hasExtraBufferData()) {
            resurrectBuffer = new int[someResurrectedIdentifier * 2];
            for (int i = 0; i < resurrectBuffer.length; i++) {
                resurrectBuffer[i] = buffer.getInt(); // Read some 39 DWORDs, whatever it is
            }
        } else {
            resurrectBuffer = null;
        }
        while (buffer.position() < stringDataOffset) {
            final int length = buffer.getInt();
            final byte magic32ValueByte = buffer.get(buffer.position());
            if (magic32ValueByte != 0x20) {
                throw new IllegalStateException("Not 32 bit (0x20): " + magic32ValueByte);
            }
            final int messageType = buffer.getInt(buffer.position() + 1);
            final ByteBuffer chunkContentsBuffer = ByteBuffer.allocate(length - 5);
            chunkContentsBuffer.clear();
            for (int i = 5; i < length; i++) {
                chunkContentsBuffer.put(buffer.get(buffer.position() + i));
            }
            chunks.add(new UnknownChunk(messageType, chunkContentsBuffer));
            if (messageType > 400) {
                LOGGER.trace("length:{}", length);
                String msgType = messageType + "\t" + Integer.toHexString(messageType) + "\t" + Integer.toBinaryString(messageType) + "\t" + new War3ID(messageType) + "\t";
                LOGGER.trace("messageType:{}", msgType);
            }
            Set<Integer> lens = MESSAGE_TYPES_TO_LENS.computeIfAbsent(messageType, k -> new TreeSet<>());
            lens.add(length);
            buffer.position(buffer.position() + length);
        }

    }

    public void setString(final int selectedIndex, final String newString) {
        strings.set(selectedIndex, newString);
    }

    public List<String> getStrings() {
        return strings;
    }

    public List<PKBChunk> getChunks() {
        return chunks;
    }

    public PKBVersion getVersion() {
        return pkbVersion;
    }

    public ByteBuffer toBuffer() {
        int byteLength = 12;
        byteLength += 4; // first magic
        if (this.pkbVersion.hasExtraBufferData()) {
            byteLength += 4; // some resurrect magic
        }
        byteLength += 4; // string offset location info
        if (this.pkbVersion.hasExtraBufferData()) {
            byteLength += 4;
            byteLength += resurrectBuffer.length * 4;
        } else {
            byteLength += 8; // second magic
        }
        for (final PKBChunk chunk : chunks) {
            byteLength += chunk.getByteLength() + 9;
        }
        final int stringsOffset = byteLength;
        byteLength += 4;
        for (final String string : strings) {
            byteLength += string.length() + 1;
        }
        final ByteBuffer buffer = ByteBuffer.allocate(byteLength).order(ByteOrder.LITTLE_ENDIAN);
        buffer.putInt(this.pkbVersion.getMagicKey1());
        buffer.putInt(this.pkbVersion.getMagicKey2());
        buffer.putInt(this.pkbVersion.getMagicKey3());

        buffer.putInt(firstMagicIdentifier);
        if (this.pkbVersion.hasExtraBufferData()) {
            buffer.putInt(someResurrectedIdentifier);
        }
        buffer.putInt(stringsOffset);
        if (this.pkbVersion.hasExtraBufferData()) {
            buffer.putInt((int) secondMagicIdentifier);
            for (int j : resurrectBuffer) {
                buffer.putInt(j);
            }
        } else {
            buffer.putLong(secondMagicIdentifier);
        }
        for (final PKBChunk chunk : chunks) {
            buffer.putInt(chunk.getByteLength() + 5);
            buffer.put((byte) 0x20); // the magic 32
            buffer.putInt(chunk.chunkType());
            chunk.write(buffer);
        }
        buffer.putInt(strings.size());
        for (final String string : strings) {
            buffer.put((byte) string.length());
            buffer.put(string.getBytes());
        }
        if (buffer.hasRemaining()) {
            throw new IllegalStateException("buffer create failed: " + buffer.remaining());
        }
        return buffer;
    }
}
