package com.elettra.common.fit;

public class LinearFitResult
{
	private double intercept;
	private double interceptStdError;
	private double slope;
	private double slopeStdError;
	private double rSquare;

	public LinearFitResult(double intercept, double interceptStdError, double slope, double slopeStdError, double rSquare)
	{
		super();
		this.intercept = intercept;
		this.interceptStdError = interceptStdError;
		this.slope = slope;
		this.slopeStdError = slopeStdError;
		this.rSquare = rSquare;
	}

	public double getIntercept()
	{
		return this.intercept;
	}

	public double getSlope()
	{
		return this.slope;
	}

	public double getSlopeStdError()
	{
		return this.slopeStdError;
	}

	public double getInterceptStdError()
	{
		return interceptStdError;
	}

	public double getrSquare()
	{
		return this.rSquare;
	}
}
