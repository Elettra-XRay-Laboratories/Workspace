package com.elettra.controller.driver.commands;

import com.elettra.common.io.CommunicationPortException;
import com.elettra.common.io.ICommunicationPort;

public abstract class ReadCommand extends AbstractCommand
{

	public ReadCommand(String commandName)
	{
		super(commandName);

	}

	protected CommandResult executeSpecificCommand(String commandString, CommandParameters commandParameters, ICommunicationPort port) throws CommunicationPortException
	{
		port.write(commandString);

		return new CommandResult(port.read().trim());
	}

}
