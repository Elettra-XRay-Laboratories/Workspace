package com.elettra.controller.driver.common;

public final class MultipleAxis
{
	private int  axis1;
	private int  axis2;
	private Sign relativeSign;
	private int  defaultReferenceAxis;

	public MultipleAxis(int axis1, int axis2, Sign relativeSign, int defaultReferenceAxis)
	{
		super();
		this.axis1 = axis1;
		this.axis2 = axis2;
		this.relativeSign = relativeSign;

		if (defaultReferenceAxis < 1 || defaultReferenceAxis > 2)
			throw new IllegalArgumentException("Reference Axis should be only 1 or 2");

		this.defaultReferenceAxis = defaultReferenceAxis;
	}

	public int getAxis1()
	{
		return this.axis1;
	}

	public int getAxis2()
	{
		return this.axis2;
	}

	public Sign getRelativeSign()
	{
		return this.relativeSign;
	}

	public int getDefaultReferenceAxis()
	{
		return this.defaultReferenceAxis;
	}
}
