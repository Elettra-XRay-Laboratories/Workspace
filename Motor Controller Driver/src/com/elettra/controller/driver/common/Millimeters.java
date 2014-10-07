package com.elettra.controller.driver.common;

public final class Millimeters extends MeasureUnit
{
	public static final String MM = "mm";

	public Millimeters()
	{
		super(MM);
	}
	
	public boolean equals(Object o)
	{
		return (o instanceof Millimeters);
	}

}
