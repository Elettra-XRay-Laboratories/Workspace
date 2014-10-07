package com.elettra.common.fit;

abstract class GenericFitResult
{
	private double chi2;
	private double rms;

	public GenericFitResult(double chi2, double rms)
	{
		super();
		this.chi2 = chi2;
		this.rms = rms;
	}

	public void setChi2(double chi2)
	{
		this.chi2 = chi2;
	}

	public double getChi2()
	{
		return this.chi2;
	}

	public double getRms()
	{
		return this.rms;
	}

	public void setRms(double rms)
	{
		this.rms = rms;
	}

}
