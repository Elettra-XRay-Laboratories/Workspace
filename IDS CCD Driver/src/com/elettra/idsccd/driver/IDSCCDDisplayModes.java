package com.elettra.idsccd.driver;

public enum IDSCCDDisplayModes implements JNAEnum<IDSCCDDisplayModes>
{
	IS_SET_DM_DIB, // 1
	IS_SET_DM_DIRECT3D, // 4
	IS_SET_DM_OPENGL; // 8

	public int getIntValue()
	{
		switch (this.ordinal())
		{
			case 0:
				return 1;
			case 1:
				return 4;
			case 2:
				return 8;
			default:
				return 0;
		}
	}

	public IDSCCDDisplayModes getForValue(int i)
	{
		for (IDSCCDDisplayModes o : IDSCCDDisplayModes.values())
			if (o.getIntValue() == i)
				return o;

		return null;
	}
}
