package com.elettra.controller.driver.commands;

public class UnexistingCommandException extends IllegalArgumentException
{

	/**
   * 
   */
	private static final long serialVersionUID = -973058415682658499L;

	public UnexistingCommandException(String commandName)
	{
		super("Command " + commandName + " not existing");

	}

	public UnexistingCommandException(String commandName, Throwable cause)
	{
		super("Command " + commandName + " not existing", cause);

	}

}
