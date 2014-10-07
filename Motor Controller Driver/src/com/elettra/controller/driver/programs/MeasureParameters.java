package com.elettra.controller.driver.programs;

import com.elettra.controller.driver.listeners.IDriverListener;

public class MeasureParameters extends ProgramParameters
{

	private double scanTime;

	public MeasureParameters(int axis, IDriverListener listener)
	{
		super(axis, listener);

	}

	public double getScanTime()
	{
		return this.scanTime;
	}

	public synchronized void setScanTime(double scanTime)
	{
		this.scanTime = scanTime;
	}

}
