package com.elettra.idsccd.driver;


import com.sun.jna.Memory;

class CharPointer extends Memory {

	private long size;

	CharPointer(int dimension) {
		super(dimension * SizeOf.sizeof((char) 0));

		this.size = dimension * SizeOf.sizeof((char) 0);
		this.setMemory(0, this.size, (byte) 0);
	}

	public String getString() {
		
		return this.getString(0);
	}

}
