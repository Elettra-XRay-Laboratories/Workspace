package com.elettra.controller.driver.commands.huber;

import com.elettra.controller.driver.commands.CommandsFacade.Commands;

public final class RUNCommand extends MovingAxisCommand
{
	public RUNCommand()
	{
		super(Commands.RUN);
	}
}
