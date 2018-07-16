package com.elettra.controller.driver.programs;

public class StabilityScan
{
	public static final int STEP_DURATION = 50;

	private int steps;
	private int stepDuration;
	
	
	public StabilityScan(int steps)
	{
		this(steps, STEP_DURATION);
	}

	public StabilityScan(int steps, int stepDuration)
	{
		super();
		this.steps = steps;
		this.stepDuration = stepDuration;
	}

	public int getStepDuration()
	{
		return this.stepDuration;
	}

	public int getSteps()
	{
		return this.steps;
	}

	public int getTotalDuration()
	{
		return this.getSteps() * this.stepDuration;
	}
}