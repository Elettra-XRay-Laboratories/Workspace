package com.elettra.common.fit;

public class MiddlePointResult extends LinearFitResult
{
	private double middlePointSignalHeight;
	private double middlePointPosition;

	public MiddlePointResult(double intercept, double interceptStdError, double slope, double slopeStdError, double rSquare)
	{
		super(intercept, interceptStdError, slope, slopeStdError, rSquare);
	}

	public double getMiddlePointPosition()
	{
		return middlePointPosition;
	}

	public void setMiddlePointPosition(double middlePointPosition)
	{
		this.middlePointPosition = middlePointPosition;
	}

	public double getMiddlePointSignalHeight()
	{
		return middlePointSignalHeight;
	}

	public void setMiddlePointSignalHeight(double middlePointSignalHeight)
	{
		this.middlePointSignalHeight = middlePointSignalHeight;
	}

}
