package com.elettra.controller.driver.commands.galil;

import com.elettra.common.io.ICommunicationPort;
import com.elettra.common.io.CommunicationPortException;
import com.elettra.controller.driver.commands.CommandParameters;
import com.elettra.controller.driver.commands.CommandResult;
import com.elettra.controller.driver.commands.WriteCommand;
import com.elettra.controller.driver.common.DriverUtilities;

abstract class MovingAxisCommand extends WriteCommand
{

	public MovingAxisCommand(String commandName)
	{
		super(commandName);
	}

	protected final String getUnformattedCommandString(CommandParameters commandParameters)
	{
		String commandString = DriverUtilities.buildGalilCommand("SH" + DriverUtilities.getGalilAxis(commandParameters.getAxis()));
		commandString += DriverUtilities.buildGalilCommand(this.getUnformattedSpecificCommandString(commandParameters));
		commandString += DriverUtilities.buildGalilCommand("BG" + DriverUtilities.getGalilAxis(commandParameters.getAxis()));

		return commandString;
	}

	protected abstract String getUnformattedSpecificCommandString(CommandParameters commandParameters);

	protected final CommandResult executeSpecificCommand(String commandString, CommandParameters commandParameters, ICommunicationPort port) throws CommunicationPortException
	{
		port.write(commandString);

		commandParameters.getListener().signalAxisMovement(commandParameters.getAxis(), port);

		return null;
	}
}
