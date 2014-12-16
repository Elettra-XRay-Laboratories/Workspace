package com.elettra.common.io;

public class SerialPort64 extends KindOfPort
{
	public static final String PORT = "SerialComPort64";

	public SerialPort64()
	{
		super(PORT);
	}

	public boolean equals(Object o)
	{
		return (o instanceof SerialPort64);
	}
}
