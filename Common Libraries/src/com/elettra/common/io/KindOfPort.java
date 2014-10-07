package com.elettra.common.io;

import com.elettra.common.utilities.StringUtilities;

public abstract class KindOfPort
{
	private String kindOfPort;

	public KindOfPort(String kindOfPort)
	{
		StringUtilities.checkString(kindOfPort, "kindOfPort");
		this.kindOfPort = kindOfPort;
	}

	public final String toString()
	{
		return this.kindOfPort;
	}
}
