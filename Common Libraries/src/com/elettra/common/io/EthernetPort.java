package com.elettra.common.io;

public class EthernetPort extends KindOfPort
{
	public static final String PORT = "EthernetPort";

	public EthernetPort()
	{
		super(PORT);
	}

	public boolean equals(Object o)
	{
		return (o instanceof EthernetPort);
	}
}
