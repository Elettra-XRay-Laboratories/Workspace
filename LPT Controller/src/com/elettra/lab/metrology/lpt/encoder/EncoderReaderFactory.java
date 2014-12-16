package com.elettra.lab.metrology.lpt.encoder;

import com.elettra.common.io.CommunicationPortException;

public class EncoderReaderFactory
{
	private static IEncoderReader	uniqueInstance	= null;

	public static synchronized IEncoderReader getEncoderReader() throws CommunicationPortException
	{
		if (uniqueInstance == null)
			uniqueInstance = new HeidenheinEncoderReader();

		return uniqueInstance;
	}

	public static synchronized void releaseEncoderReader() throws CommunicationPortException
	{
		if (uniqueInstance != null)
			((IEncoderReaderInternal) uniqueInstance).release();

		uniqueInstance = null;
	}

}
