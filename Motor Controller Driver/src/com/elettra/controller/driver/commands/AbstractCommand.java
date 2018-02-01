package com.elettra.controller.driver.commands;

import com.elettra.common.io.ICommunicationPort;
import com.elettra.common.io.CommunicationPortException;
import com.elettra.common.utilities.ObjectUtilities;
import com.elettra.common.utilities.StringUtilities;
import com.elettra.controller.driver.common.DriverUtilities;

public abstract class AbstractCommand implements ICommand
{
	private AbstractCommand next;
	private String          commandName;

	public AbstractCommand(String commandName)
	{
		super();

		StringUtilities.checkString(commandName, "commandName");

		this.commandName = commandName;
	}

	protected final AbstractCommand setNext(AbstractCommand next)
	{
		ObjectUtilities.checkObject(next, "next");

		this.next = next;

		return this.next;
	}

	protected final boolean isRequestedCommand(String commandName)
	{
		StringUtilities.checkString(commandName, "commandName");

		return this.commandName.equals(commandName);
	}

	protected final AbstractCommand getCommand(String commandName)
	{
		if (this.isRequestedCommand(commandName))
			return this;
		else
			return this.next == null ? null : this.next.getCommand(commandName);
	}

	protected final String getCommandName()
	{
		return this.commandName;
	}

	protected final String getCommandString(CommandParameters commandParameters)
	{
		if (DriverUtilities.getKindOfController().equals(DriverUtilities.getGalilController()))
			return DriverUtilities.buildGalilCommand(this.getUnformattedCommandString(commandParameters));
		else if (DriverUtilities.getKindOfController().equals(DriverUtilities.getHuberController()))
			return DriverUtilities.buildHuberCommand(this.getUnformattedCommandString(commandParameters));
		else
			return null;
	}

	protected abstract String getUnformattedCommandString(CommandParameters commandParameters);

	public final CommandResult execute(CommandParameters commandParameters, ICommunicationPort port) throws CommunicationPortException
	{
		ObjectUtilities.checkObject(port, "port");
		
		return this.executeSpecificCommand(this.getCommandString(commandParameters), commandParameters, port);
	}

	protected abstract CommandResult executeSpecificCommand(String commandString, CommandParameters commandParameters, ICommunicationPort port) throws CommunicationPortException;
}
