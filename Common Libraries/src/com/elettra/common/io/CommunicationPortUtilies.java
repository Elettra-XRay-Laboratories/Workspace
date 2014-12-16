package com.elettra.common.io;

import com.elettra.common.utilities.StringUtilities;

public class CommunicationPortUtilies
{
	public static KindOfPort getEthernetPort()
	{
		return new EthernetPort();
	}

	public static KindOfPort getSerialPort()
	{
		return new SerialPort();
	}

	public static KindOfPort getSerialPort64()
	{
		return new SerialPort64();
	}

	public static KindOfPort parseKindOfPort(String kindOfPortString)
	{
		StringUtilities.checkString(kindOfPortString, "kindOfPortString");

		if (kindOfPortString.equalsIgnoreCase(SerialPort.PORT))
			return new SerialPort();
		else if (kindOfPortString.equalsIgnoreCase(SerialPort64.PORT))
			return new SerialPort64();
		else if (kindOfPortString.equalsIgnoreCase(EthernetPort.PORT))
			return new EthernetPort();
		else
			throw new IllegalArgumentException("kindOfPort not recognized: " + kindOfPortString);
	}

}
