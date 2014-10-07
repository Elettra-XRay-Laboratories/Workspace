package com.elettra.controller.driver.commands.huber;

import com.elettra.controller.driver.commands.CommandsFacade.Commands;

public final class FASTCommand extends MovingAxisCommand
{
	public FASTCommand()
	{
		super(Commands.FAST);
	}
}
