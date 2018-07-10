package com.elettra.common.io;

public class SerialPortRemote extends KindOfPort
{
	public static final String PORT = "SerialComPortRemote";

	public SerialPortRemote()
	{
		super(PORT);
	}

	public boolean equals(Object o)
	{
		return (o instanceof SerialPortRemote);
	}
}
