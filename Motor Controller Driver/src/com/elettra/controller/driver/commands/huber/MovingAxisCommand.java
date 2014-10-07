package com.elettra.controller.driver.commands.huber;

import com.elettra.common.io.ICommunicationPort;
import com.elettra.common.io.CommunicationPortException;
import com.elettra.controller.driver.commands.CommandParameters;
import com.elettra.controller.driver.commands.CommandResult;

abstract class MovingAxisCommand extends ChangeAxisCommand
{

	public MovingAxisCommand(String commandName)
	{
		super(commandName);
	}

	protected String getUnformattedSpecificCommandString(CommandParameters commandParameters)
	{
		return this.getCommandName() + commandParameters.getSign();
	}

	protected final CommandResult executeSpecificCommand(String commandString, CommandParameters commandParameters, ICommunicationPort port) throws CommunicationPortException
	{
		port.write(commandString);

		commandParameters.getListener().signalAxisMovement(commandParameters.getAxis(), port);

		return null;
	}
}
