package com.elettra.idsccd.driver;

public final class IDSCCDFactory
{
	public static synchronized IIDSCCD getIDSCCD()
	{
		return new IDSCCD();
	}
}
