package com.hiveworkshop.pkb;

import java.nio.ByteBuffer;

public class UnknownChunk implements PKBChunk {
	private int chunkType;
	private ByteBuffer chunkData;

	public UnknownChunk(final int chunkType, final ByteBuffer chunkData) {
		this.chunkType = chunkType;
		this.chunkData = chunkData;
	}

	@Override
	public int getChunkType() {
		return chunkType;
	}

	public void setChunkType(final int chunkType) {
		this.chunkType = chunkType;
	}

	public ByteBuffer getChunkData() {
		return chunkData;
	}

	public void setChunkData(final ByteBuffer chunkData) {
		this.chunkData = chunkData;
	}

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
