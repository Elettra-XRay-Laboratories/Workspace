package com.elettra.controller.driver.common;

import com.elettra.common.utilities.StringUtilities;

public abstract class Sign
{
	private String sign;

	public Sign(String sign)
	{
		StringUtilities.checkString(sign, "sign");
		this.sign = sign;
	}

	public final String toString()
	{
		return this.sign;
	}

	public abstract double sign();
}
