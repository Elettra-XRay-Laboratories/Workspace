package com.elettra.idsccd.driver;

public enum IDSCCDImageFileTypes implements JNAEnum<IDSCCDImageFileTypes>
{
	IS_IMG_BMP, //0
	IS_IMG_JPG, //1
	IS_IMG_PNG, //2
	IS_IMG_RAW, //4
	IS_IMG_TIF; //8

	public int getIntValue()
	{
		switch (this.ordinal())
		{
			case 0:
				return 0;
			case 1:
				return 1;
			case 2:
				return 2;
			case 3:
				return 4;
			case 4:
				return 8;
			default:
				return 0;
		}
	}

	public IDSCCDImageFileTypes getForValue(int i)
	{
		for (IDSCCDImageFileTypes o : IDSCCDImageFileTypes.values())
			if (o.getIntValue() == i)
				return o;

		return null;
	}
}
