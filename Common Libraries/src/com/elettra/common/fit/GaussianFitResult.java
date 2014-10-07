package com.elettra.common.fit;

public final class GaussianFitResult extends GenericFitResult
{
	private double shift;
	private double height;
	private double position;
	private double sigma;

	public GaussianFitResult(double shift, double height, double position, double sigma)
	{
		super(0.0, 0.0);

		this.shift = shift;
		this.height = height;
		this.position = position;
		this.sigma = sigma;
	}

	public double getShift()
	{
		return this.shift;
	}

	public double getHeight()
	{
		return this.height;
	}

	public double getPosition()
	{
		return this.position;
	}

	public double getSigma()
	{
		return this.sigma;
	}

	public double getFwhm()
	{
		return this.sigma / FitUtilities.GAUSSIAN_FWHM_TO_SIGMA;
	}

}
