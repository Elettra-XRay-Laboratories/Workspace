package com.elettra.common.io;

public class SerialPort extends KindOfPort
{
	public static final String PORT = "SerialComPort";

	public SerialPort()
	{
		super(PORT);
	}

	public boolean equals(Object o)
	{
		return (o instanceof SerialPort);
	}
}
