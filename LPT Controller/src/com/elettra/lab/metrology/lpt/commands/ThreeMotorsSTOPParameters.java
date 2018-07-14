package com.elettra.lab.metrology.lpt.commands;

import com.elettra.controller.driver.commands.CommandParameters;
import com.elettra.controller.driver.listeners.IDriverListener;

public class ThreeMotorsSTOPParameters extends CommandParameters
{
	private int	axis2;
	private int	axis3;

	public ThreeMotorsSTOPParameters(int axis1, int axis2, int axis3, IDriverListener listener)
	{
		super(axis1, listener);

		this.axis2 = axis2;
		this.axis3 = axis3;
	}

	public final int getAxis1()
	{
		return this.getAxis();
	}

	public final int getAxis2()
	{
		return this.axis2;
	}

	public final int getAxis3()
	{
		return this.axis3;
	}

}
