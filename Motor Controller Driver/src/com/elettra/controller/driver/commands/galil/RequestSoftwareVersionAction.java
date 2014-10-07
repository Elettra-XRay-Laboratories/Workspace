package com.elettra.controller.driver.commands.galil;

import com.elettra.common.io.CommunicationPortException;
import com.elettra.common.io.ICommunicationPort;
import com.elettra.controller.driver.commands.CommandParameters;
import com.elettra.controller.driver.commands.CommandResult;
import com.elettra.controller.driver.commands.CommandsFacade.Actions;
import com.elettra.controller.driver.commands.ReadCommand;

public final class RequestSoftwareVersionAction extends ReadCommand
{
	public RequestSoftwareVersionAction()
	{
		super(Actions.REQUEST_SOFTWARE_VERSION);
	}

	protected String getUnformattedCommandString(CommandParameters commandParameters)
	{
		return new Character((char) 18).toString() + new Character((char) 22).toString();
	}

	protected CommandResult executeSpecificCommand(String commandString, CommandParameters commandParameters, ICommunicationPort port) throws CommunicationPortException
	{
		port.write(commandString);

		String response = port.read().trim();

		// ctrl+R, ctrl+V
		return new CommandResult(response.substring(response.lastIndexOf(":") + 1));
	}
}
