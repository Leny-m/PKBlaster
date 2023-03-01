package com.hiveworkshop.pkb;

import java.nio.ByteBuffer;

public interface PKBChunk {
	int chunkType();

	int getByteLength();

	void write(ByteBuffer buffer);
}
