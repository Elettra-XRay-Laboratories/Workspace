package com.elettra.controller.driver.commands.galil;

import com.elettra.controller.driver.commands.CommandParameters;
import com.elettra.controller.driver.commands.WriteCommand;
import com.elettra.controller.driver.commands.CommandsFacade.Commands;
import com.elettra.controller.driver.common.DriverUtilities;

public final class STOPCommand extends WriteCommand
{
	public STOPCommand()
	{
		super(Commands.STOP);
	}

	protected String getUnformattedCommandString(CommandParameters commandParameters)
	{
		String commandString = DriverUtilities.buildGalilCommand("ST" + DriverUtilities.getGalilAxis(commandParameters.getAxis()));
		commandString += "MO" + DriverUtilities.getGalilAxis(commandParameters.getAxis());

		return commandString;
	}
}
