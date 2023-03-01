package com.hiveworkshop.pkb;

import java.nio.ByteBuffer;

public record UnknownChunk(int chunkType, ByteBuffer chunkData) implements PKBChunk {

	@Override
	public String toString() {
		return "Unknown '" + chunkType + "'";
	}

	@Override
	public int getByteLength() {
		return chunkData.capacity();
	}

	@Override
	public void write(final ByteBuffer buffer) {
		chunkData.clear();
		buffer.put(chunkData);
	}
}
