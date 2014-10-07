package com.elettra.controller.driver.commands.huber;

import com.elettra.controller.driver.commands.SimpleCommand;
import com.elettra.controller.driver.commands.CommandsFacade.Commands;

public final class LOCALCommand extends SimpleCommand
{
	public LOCALCommand()
	{
		super(Commands.LOCAL);
	}
}
