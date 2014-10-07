package com.elettra.controller.driver.common;

public final class DecimalGrades extends MeasureUnit
{
	public static final String DEG = "deg";

	public DecimalGrades()
	{
		super(DEG);
	}

	public boolean equals(Object o)
	{
		return (o instanceof DecimalGrades);
	}

}
