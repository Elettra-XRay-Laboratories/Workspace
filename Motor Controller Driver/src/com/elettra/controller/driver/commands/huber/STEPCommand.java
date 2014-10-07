package com.elettra.controller.driver.commands.huber;

import com.elettra.controller.driver.commands.CommandsFacade.Commands;

public final class STEPCommand extends MovingAxisCommand
{
	public STEPCommand()
	{
		super(Commands.STEP);
	}
}
