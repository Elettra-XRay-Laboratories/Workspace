package com.elettra.controller.gui.fit;

public class PlottableFitResult
{
	private double firstCoordinate;
	private double secondCoordinate;
	private double peakPosition;
	private double peakIntensity;
	private double peakFWHM;

	public PlottableFitResult(double firstCoordinate, double secondCoordinate, double peakPosition, double peakIntensity, double peakFWHM)
	{
		super();
		this.firstCoordinate = firstCoordinate;
		this.secondCoordinate = secondCoordinate;
		this.peakPosition = peakPosition;
		this.peakIntensity = peakIntensity;
		this.peakFWHM = peakFWHM;
	}

	public double getPeakPosition()
	{
		return this.peakPosition;
	}

	public double getPeakIntensity()
	{
		return this.peakIntensity;
	}

	public double getPeakFWHM()
	{
		return this.peakFWHM;
	}

	public double getFirstCoordinate()
	{
		return this.firstCoordinate;
	}

	public double getSecondCoordinate()
	{
		return this.secondCoordinate;
	}

	public String toString()
	{
		return "First Coordinate = " + this.firstCoordinate + "; Second Coordinate = " + this.secondCoordinate + "; Peak Position = " + this.peakPosition + "; Peak Intensity = " + this.peakIntensity + "; Peak FWHM = " + this.peakFWHM;
	}
}
