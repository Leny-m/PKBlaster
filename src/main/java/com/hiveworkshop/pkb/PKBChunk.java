package com.hiveworkshop.pkb;

import java.nio.ByteBuffer;

public interface PKBChunk {
	int getChunkType();

	int getByteLength();

	void write(ByteBuffer buffer);
}
