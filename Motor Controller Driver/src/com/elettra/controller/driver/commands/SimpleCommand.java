package com.elettra.controller.driver.commands;

public abstract class SimpleCommand extends WriteCommand
{

	public SimpleCommand(String commandName)
	{
		super(commandName);
	}

	protected final String getUnformattedCommandString(CommandParameters commandParameters)
	{
		return this.getCommandName();
	}

}