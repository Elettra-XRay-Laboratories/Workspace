package com.elettra.controller.driver.programs;

import com.elettra.controller.driver.listeners.IDriverListener;

public class DoubleMoveParameters extends MoveParameters
{
	private int referenceAxis;

	public DoubleMoveParameters(int axis, IDriverListener listener)
	{
		super(axis, listener);

		this.referenceAxis = 2;
	}

	public int getReferenceAxis()
	{
		return this.referenceAxis;
	}

	public void setReferenceAxis(int referenceAxis)
	{
		if (referenceAxis < 1 || referenceAxis > 2)
			throw new IllegalArgumentException("Reference Axis should be only 1 or 2");

		this.referenceAxis = referenceAxis;
	}

}
