package com.elettra.controller.driver.programs;

class StabilityScan
{
	public static final int STEP_DURATION = 50;

	private int             steps;

	public StabilityScan(int steps)
	{
		super();
		this.steps = steps;
	}

	public int getSteps()
	{
		return this.steps;
	}

	public int getTotalDuration()
	{
		return this.getSteps() * STEP_DURATION;
	}
}