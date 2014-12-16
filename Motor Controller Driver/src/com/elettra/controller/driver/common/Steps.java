package com.elettra.controller.driver.common;

public final class Steps extends MeasureUnit
{
	public static final String STEPS = "stp";

	public Steps()
	{
		super(STEPS);
	}

	public boolean equals(Object o)
	{
		return (o instanceof Steps);
	}

}
