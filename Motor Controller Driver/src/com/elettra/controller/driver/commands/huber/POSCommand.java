package com.elettra.controller.driver.commands.huber;

import com.elettra.common.io.ICommunicationPort;
import com.elettra.common.io.CommunicationPortException;
import com.elettra.controller.driver.commands.CommandParameters;
import com.elettra.controller.driver.commands.CommandResult;
import com.elettra.controller.driver.commands.WriteCommand;
import com.elettra.controller.driver.commands.CommandsFacade.Commands;
import com.elettra.controller.driver.common.DriverUtilities;

public final class POSCommand extends WriteCommand
{

	public POSCommand()
	{
		super(Commands.POS);

	}

	protected String getUnformattedCommandString(CommandParameters commandParameters)
	{
		return Commands.POS
		    + commandParameters.getAxis()
		    + ":"
		    + commandParameters.getSign().toString()
		    + DriverUtilities.formatControllerPosition(commandParameters.getPosition());
	}

	protected final CommandResult executeSpecificCommand(String commandString, CommandParameters commandParameters, ICommunicationPort port) throws CommunicationPortException
	{
		super.executeSpecificCommand(commandString, commandParameters, port);

		commandParameters.getListener().signalAxisMovement(commandParameters.getAxis(), port);

		return null;
	}

}
