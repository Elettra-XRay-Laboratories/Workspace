package com.elettra.lab.metrology.lpt.commands;

import com.elettra.controller.driver.commands.CommandParameters;
import com.elettra.controller.driver.commands.WriteCommand;
import com.elettra.controller.driver.common.DriverUtilities;

public final class ThreeMotorsSTOPCommand extends WriteCommand
{
	public static final String	THREEMSTOP	= "THREEMSTOP";

	public ThreeMotorsSTOPCommand()
	{
		super(THREEMSTOP);
	}

	protected String getUnformattedCommandString(CommandParameters commandParameters)
	{
		ThreeMotorsSTOPParameters stopParameters = (ThreeMotorsSTOPParameters) commandParameters;

		String commandString = DriverUtilities.buildGalilCommand("ST" + DriverUtilities.getGalilAxis(stopParameters.getAxis1())
		    + DriverUtilities.getGalilAxis(stopParameters.getAxis2()) + DriverUtilities.getGalilAxis(stopParameters.getAxis3()));
		commandString += "MO" + DriverUtilities.getGalilAxis(stopParameters.getAxis1()) + DriverUtilities.getGalilAxis(stopParameters.getAxis2())
		    + DriverUtilities.getGalilAxis(stopParameters.getAxis3());

		return commandString;
	}
}
