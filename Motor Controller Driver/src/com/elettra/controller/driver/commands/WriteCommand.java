package com.elettra.controller.driver.commands;

import com.elettra.common.io.ICommunicationPort;
import com.elettra.common.io.CommunicationPortException;

public abstract class WriteCommand extends AbstractCommand
{

	public WriteCommand(String commandName)
	{
		super(commandName);
	}

	protected CommandResult executeSpecificCommand(String commandString, CommandParameters commandParameters, ICommunicationPort port) throws CommunicationPortException
	{
		port.write(commandString);

		return null;
	}
}
