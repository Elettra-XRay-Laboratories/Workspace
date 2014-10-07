package com.elettra.controller.driver.commands.huber;

import com.elettra.controller.driver.commands.CommandParameters;
import com.elettra.controller.driver.commands.CommandsFacade.Commands;

public final class REFCommand extends MovingAxisCommand
{
	public REFCommand()
	{
		super(Commands.REF);
	}

	protected String getUnformattedSpecificCommandString(CommandParameters commandParameters)
  {
	  return this.getCommandName() + Integer.toString(commandParameters.getAxis());
  }
}
