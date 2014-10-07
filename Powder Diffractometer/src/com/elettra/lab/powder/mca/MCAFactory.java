package com.elettra.lab.powder.mca;

public final class MCAFactory
{
	public static synchronized IMCA getMCA(int comport, int baudrate, int numberOfChannels) throws MCAException
	{
		return new MCA(comport, baudrate, numberOfChannels);
	}
}
