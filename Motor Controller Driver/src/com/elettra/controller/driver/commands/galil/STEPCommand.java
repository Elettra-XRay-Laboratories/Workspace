package com.elettra.controller.driver.commands.galil;

import com.elettra.controller.driver.commands.CommandParameters;
import com.elettra.controller.driver.commands.CommandsFacade.Commands;
import com.elettra.controller.driver.common.AxisConfiguration;
import com.elettra.controller.driver.common.DriverUtilities;

public final class STEPCommand extends MovingAxisCommand
{
	public STEPCommand()
	{
		super(Commands.STEP);
	}

	protected String getUnformattedSpecificCommandString(CommandParameters commandParameters)
	{
		AxisConfiguration configuration = DriverUtilities.getAxisConfigurationMap().getAxisConfiguration(commandParameters.getAxis());

		String command = DriverUtilities.buildGalilCommand("AC" + DriverUtilities.getGalilAxis(commandParameters.getAxis()) + "=" + Integer.toString(configuration.getRamp()));
		command += DriverUtilities.buildGalilCommand("DC" + DriverUtilities.getGalilAxis(commandParameters.getAxis()) + "=" + Integer.toString(configuration.getRamp()));
		command += "PR" + DriverUtilities.getGalilAxis(commandParameters.getAxis()) + "=" + DriverUtilities.getSignProduct(commandParameters.getSign(), configuration.getSignToPositive()) + "1";

		return command;
	}

}
