package com.elettra.controller.driver.commands.huber;

import com.elettra.controller.driver.commands.SimpleCommand;
import com.elettra.controller.driver.commands.CommandsFacade.Commands;

public final class REMOTECommand extends SimpleCommand
{
	public REMOTECommand()
	{
		super(Commands.REMOTE);
	}
}
