package com.elettra.controller.driver.common;

public class StepConverter
{
	public static int toStep(int axis, double signedPosition)
	{
		AxisConfiguration configuration = DriverUtilities.getAxisConfigurationMap().getAxisConfiguration(axis);

		return (int) Math.ceil((signedPosition / configuration.getStepDistance()));

	}

	public static double toDistance(int axis, int step)
	{
		AxisConfiguration configuration = DriverUtilities.getAxisConfigurationMap().getAxisConfiguration(axis);

		return step * configuration.getStepDistance();
	}

}
