package com.hiveworkshop.pkb;

public enum PKBVersion {
	REFORGED_132(0xc9000b11, 0x01040202, 0x0000e14a),
	RESURRECTED(0xca000b11, 0x01050902, 0x00001f40),
	REFORGED_133(0xca000b11, 0x01010502, 0x0000f7d7);

	private int magicKey1;
	private int magicKey2;
	private int magicKey3;

	private PKBVersion(int magicKey1, int magicKey2, int magicKey3) {
		this.magicKey1 = magicKey1;
		this.magicKey2 = magicKey2;
		this.magicKey3 = magicKey3;
	}

	public int getMagicKey1() {
		return magicKey1;
	}

	public int getMagicKey2() {
		return magicKey2;
	}

	public int getMagicKey3() {
		return magicKey3;
	}
	
	public boolean hasExtraBufferData() {
		return this != REFORGED_132;
	}
	
	public boolean likelyToUseCLayerCompileCache() {
		return this == RESURRECTED;
	}
	
	public boolean matchingMagicKeys(int key1, int key2, int key3) {
		return key1 == magicKey1 && key2 == magicKey2 && key3 == magicKey3;
	}

}
