package com.elettra.idsccd.driver;

public enum IDSCCDImageFileCommands implements JNAEnum<IDSCCDImageFileCommands>
{
	IS_IMAGE_FILE_CMD_LOAD, IS_IMAGE_FILE_CMD_SAVE;

	private static int start = 1;

	public int getIntValue()
	{
		return start + this.ordinal();
	}

	public IDSCCDImageFileCommands getForValue(int i)
	{
		for (IDSCCDImageFileCommands o : IDSCCDImageFileCommands.values())
			if (o.getIntValue() == i)
				return o;

		return null;
	}
}
