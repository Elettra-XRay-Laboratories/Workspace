package com.elettra.common.utilities;

public final class ObjectUtilities
{
	public static void checkObject(Object object, String objectName)
	{
		if (object == null)
			throw new IllegalArgumentException("Object "
			    + objectName
			    + " cannot be null");
	}
	
	public static void pause(int milliseconds)
	{		
		try
    {
	    Thread.sleep(milliseconds);
    }
    catch (InterruptedException exception)
    {
    }
	}
}
