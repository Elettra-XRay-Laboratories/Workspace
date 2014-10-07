package com.elettra.idsccd.driver;

public class JNAUtility
{
  
	public static long fromUnsignedInt(int original)
	{
		long result = 0;

		if (original < 0)
			result = Short.MAX_VALUE + original + Math.abs(Short.MIN_VALUE) + 1;
		else
			result = original;

		return result;
	}

	public static int fromUnsignedShort(short original)
	{
		int result = 0;

		if (original < 0)
			result = Short.MAX_VALUE + original + Math.abs(Short.MIN_VALUE) + 1;
		else
			result = original;

		return result;
	}

	public static int fromUnsignedByte(byte original)
	{
		int result = 0;

		if (original < 0)
			result = Byte.MAX_VALUE + original + Math.abs(Byte.MIN_VALUE) + 1;
		else
			result = original;

		return result;
	}

	public static int[] fromUnsignedShortArray(short[] original)
	{
		int[] result = new int[original.length];

		for (int i = 0; i < original.length; i++)
			result[i] = JNAUtility.fromUnsignedShort(original[i]);

		return result;
	}

	public static int[] fromUnsignedByteArray(byte[] original)
	{
		int[] result = new int[original.length];

		for (int i = 0; i < original.length; i++)
			result[i] = JNAUtility.fromUnsignedByte(original[i]);

		return result;
	}

	public static long[] fromUnsignedIntArray(int[] original)
	{
		long[] result = new long[original.length];

		for (int i = 0; i < original.length; i++)
			result[i] = JNAUtility.fromUnsignedInt(original[i]);

		return result;
	}
}
