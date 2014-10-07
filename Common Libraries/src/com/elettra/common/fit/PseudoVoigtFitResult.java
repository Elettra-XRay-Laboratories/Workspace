package com.elettra.common.fit;

public final class PseudoVoigtFitResult extends GenericFitResult
{
	private double amplitude; // a0
	private double center;   // a1
	private double sigma;    // a2
	private double shape;    // a3
	private double offset;   // a4

	public PseudoVoigtFitResult(double amplitude, double center, double sigma, double shape, double offset)
	{
		super(0.0, 0.0);

		this.amplitude = amplitude;
		this.center = center;
		this.sigma = sigma;
		this.shape = shape;
		this.offset = offset;
	}

	public PseudoVoigtFitResult(double[] parameters)
	{
		this(parameters[0], parameters[1], parameters[2], parameters[3], parameters[4]);
	}

	public double getAmplitude()
	{
		return this.amplitude;
	}

	public double getCenter()
	{
		return this.center;
	}

	public double getSigma()
	{
		return this.sigma;
	}

	public double getShape()
	{
		return this.shape;
	}

	public double getOffset()
	{
		return this.offset;
	}

	public double getFwhm()
	{
		return this.getSigma() * 2;
	}

	public double[] getParameters()
	{
		double[] parameters = new double[5];

		parameters[0] = this.amplitude;
		parameters[1] = this.center;
		parameters[2] = this.sigma;
		parameters[3] = this.shape;
		parameters[4] = this.offset;

		return parameters;
	}
}
