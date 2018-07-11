package com.elettra.idsccd.driver;

public enum IDSCCDColorModes implements JNAEnum<IDSCCDColorModes>
{
	/*! \brief Raw sensor data, occupies 8 bits */
	IS_CM_SENSOR_RAW8          , // 11
	/*! \brief Raw sensor data, occupies 16 bits */
	IS_CM_SENSOR_RAW10         , // 33
	/*! \brief Raw sensor data, occupies 16 bits */
	IS_CM_SENSOR_RAW12         , // 27
	/*! \brief Raw sensor data, occupies 16 bits */
	IS_CM_SENSOR_RAW16         , // 29
	/*! \brief Mono, occupies 8 bits */
	IS_CM_MONO8                , // 6
	/*! \brief Mono, occupies 16 bits */
	IS_CM_MONO10              , //  34
	/*! \brief Mono, occupies 16 bits */
	IS_CM_MONO12              , //  26
	/*! \brief Mono, occupies 16 bits */
	IS_CM_MONO16               , // 28
	IS_CM_JPEG                ; //  32
	
	public static String[] get_values()
	{
		return new String[] {"Raw8", "Mono8", "JPEG"};
	}
	
	public static IDSCCDColorModes get_from_index(int index)
	{
		switch (index)
		{
			case 0:
				return IS_CM_SENSOR_RAW8;
			case 1:
				return IS_CM_MONO8;
			case 2:
				return IS_CM_JPEG;
			default:
				return IS_CM_MONO8;
		}
	}
	
	public int getIntValue()
	{
		switch (this.ordinal())
		{
			case 0:
				return 11;
			case 1:
				return 33;
			case 2:
				return 27;
			case 3:
				return 29;
			case 4:
				return 6;
			case 5:
				return 34;
			case 6:
				return 26;
			case 7:
				return 32;
			default:
				return 0;
		}
	}

	public IDSCCDColorModes getForValue(int i)
	{
		for (IDSCCDColorModes o : IDSCCDColorModes.values())
			if (o.getIntValue() == i)
				return o;

		return null;
	}
}
