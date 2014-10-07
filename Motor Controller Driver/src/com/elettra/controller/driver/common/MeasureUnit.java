package com.elettra.controller.driver.common;

import com.elettra.common.utilities.StringUtilities;

public abstract class MeasureUnit
{
	private String measureUnit;

	public MeasureUnit(String measureUnit)
	{
		StringUtilities.checkString(measureUnit, "measureUnit");

		this.measureUnit = measureUnit;
	}

	public final String toString()
	{
		return this.measureUnit;
	}
}
