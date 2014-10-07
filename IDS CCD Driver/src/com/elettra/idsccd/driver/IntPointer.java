package com.elettra.idsccd.driver;


import com.sun.jna.Memory;

class IntPointer extends Memory {

	private long size;

	IntPointer(int dimension) {
		super(dimension * SizeOf.sizeof((int) 0));

		this.size = dimension * SizeOf.sizeof((int) 0);
		this.setMemory(0, this.size, (byte) 0);
	}

	public String getString() {
		return this.getString(0);
	}
}
