package com.elettra.controller.driver.commands.huber;

import com.elettra.controller.driver.commands.CommandParameters;
import com.elettra.controller.driver.commands.WriteCommand;
import com.elettra.controller.driver.common.DriverUtilities;

abstract class ChangeAxisCommand extends WriteCommand
{

	public ChangeAxisCommand(String commandName)
	{
		super(commandName);

	}

	protected final String getUnformattedCommandString(CommandParameters commandParameters)
	{
		String commandString = DriverUtilities.buildHuberCommand("CLR");
		commandString += DriverUtilities.buildHuberCommand("CA" + Integer.toString(commandParameters.getAxis()));
		commandString += DriverUtilities.buildHuberCommand(this.getUnformattedSpecificCommandString(commandParameters));
		commandString += "START:";

		return commandString;
	}
	
	protected abstract String getUnformattedSpecificCommandString(CommandParameters commandParameters);
}
