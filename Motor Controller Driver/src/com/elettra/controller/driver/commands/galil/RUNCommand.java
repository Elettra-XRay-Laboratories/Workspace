package com.elettra.controller.driver.commands.galil;

import com.elettra.controller.driver.commands.CommandParameters;
import com.elettra.controller.driver.commands.CommandsFacade.Commands;
import com.elettra.controller.driver.common.AxisConfiguration;
import com.elettra.controller.driver.common.DriverUtilities;

public final class RUNCommand extends MovingAxisCommand
{
	public RUNCommand()
	{
		super(Commands.RUN);
	}

	protected String getUnformattedSpecificCommandString(CommandParameters commandParameters)
	{
		AxisConfiguration configuration = DriverUtilities.getAxisConfigurationMap().getAxisConfiguration(commandParameters.getAxis());

		String command = DriverUtilities.buildGalilCommand("AC" + DriverUtilities.getGalilAxis(commandParameters.getAxis()) + "=" + Integer.toString(configuration.getRamp()));
		command += DriverUtilities.buildGalilCommand("DC" + DriverUtilities.getGalilAxis(commandParameters.getAxis()) + "=" + Integer.toString(configuration.getRamp()));
		command += "JG" + DriverUtilities.getGalilAxis(commandParameters.getAxis()) + "=" + DriverUtilities.getSignProduct(commandParameters.getSign(), configuration.getSignToPositive()) + Integer.toString(configuration.getSts());

		return command;
	}

}
