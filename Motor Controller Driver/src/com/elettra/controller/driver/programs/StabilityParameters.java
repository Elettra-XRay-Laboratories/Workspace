package com.elettra.controller.driver.programs;

import com.elettra.controller.driver.listeners.IDriverListener;

public class StabilityParameters extends ProgramParameters
{
	private String scanDuration;

	public StabilityParameters(int axis, IDriverListener listener)
	{
		super(axis, listener);

	}

	public String getScanDuration()
	{
		return this.scanDuration;
	}

	public void setScanDuration(String scanDuration)
	{
		this.scanDuration = scanDuration;
	}

}
