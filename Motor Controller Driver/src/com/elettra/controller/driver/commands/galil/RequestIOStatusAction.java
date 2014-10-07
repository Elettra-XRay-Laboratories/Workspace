package com.elettra.controller.driver.commands.galil;

import com.elettra.common.io.CommunicationPortException;
import com.elettra.common.io.ICommunicationPort;
import com.elettra.controller.driver.commands.AbstractCommand;
import com.elettra.controller.driver.commands.CommandParameters;
import com.elettra.controller.driver.commands.CommandResult;
import com.elettra.controller.driver.commands.CommandsFacade.Actions;

public final class RequestIOStatusAction extends AbstractCommand
{
	public RequestIOStatusAction()
	{
		super(Actions.REQUEST_IO_STATUS);

	}

	protected String getUnformattedCommandString(CommandParameters commandParameters)
	{
		return null;
	}

	protected CommandResult executeSpecificCommand(String commandString, CommandParameters commandParameters, ICommunicationPort port) throws CommunicationPortException
	{
		return new CommandResult("OK");
	}
}
