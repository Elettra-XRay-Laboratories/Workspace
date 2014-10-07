package com.elettra.idsccd.driver;


import com.sun.jna.Memory;

public final class DataBufferPointer extends Memory {

	private int dataBlockDim;

	public DataBufferPointer(int dataBlockDim) {
		super(SizeOf.sizeof((short) 0) * dataBlockDim);
		this.dataBlockDim = dataBlockDim;
	}

	public int[] getArray() {
		return JNAUtility.fromUnsignedShortArray(this.getShortArray(0, this.dataBlockDim));
	}
}
