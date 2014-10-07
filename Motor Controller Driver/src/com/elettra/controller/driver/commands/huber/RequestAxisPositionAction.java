package com.elettra.controller.driver.commands.huber;

import java.io.IOException;

import com.elettra.common.io.ICommunicationPort;
import com.elettra.common.io.CommunicationPortException;
import com.elettra.controller.driver.commands.CommandParameters;
import com.elettra.controller.driver.commands.CommandResult;
import com.elettra.controller.driver.commands.ReadCommand;
import com.elettra.controller.driver.commands.CommandsFacade.Actions;
import com.elettra.controller.driver.common.DriverUtilities;

public final class RequestAxisPositionAction extends ReadCommand
{
	public RequestAxisPositionAction()
	{
		super(Actions.REQUEST_AXIS_POSITION);
	}

	protected String getUnformattedCommandString(CommandParameters commandParameters)
	{
		return "?P"
		    + commandParameters.getAxis();
	}

	protected CommandResult executeSpecificCommand(String commandString, CommandParameters commandParameters, ICommunicationPort port) throws CommunicationPortException
	{
		CommandResult result = super.executeSpecificCommand(commandString, commandParameters, port);

		try
		{
			DriverUtilities.saveAxisPosition(commandParameters.getAxis(), result.getResponse());
		}
		catch (IOException exception)
		{
			throw new RuntimeException("Save Axis Position impossibile", exception);
		}

		return result;
	}

}
