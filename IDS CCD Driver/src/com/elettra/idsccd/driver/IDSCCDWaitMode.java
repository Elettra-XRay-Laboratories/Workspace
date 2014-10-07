package com.elettra.idsccd.driver;

public enum IDSCCDWaitMode implements JNAEnum<IDSCCDWaitMode>
{
	IS_DONT_WAIT,
	IS_WAIT;

	private static int start = 0;

	public int getIntValue()
	{
		return start + this.ordinal();
	}

	public IDSCCDWaitMode getForValue(int i)
	{
		for (IDSCCDWaitMode o : IDSCCDWaitMode.values())
			if (o.getIntValue() == i)
				return o;

		return null;
	}
}
