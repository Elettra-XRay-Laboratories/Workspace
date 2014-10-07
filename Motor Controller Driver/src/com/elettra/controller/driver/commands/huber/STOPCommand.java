package com.elettra.controller.driver.commands.huber;

import com.elettra.controller.driver.commands.CommandParameters;
import com.elettra.controller.driver.commands.CommandsFacade.Commands;

public final class STOPCommand extends ChangeAxisCommand
{
	public STOPCommand()
	{
		super(Commands.STOP);
	}

	protected String getUnformattedSpecificCommandString(CommandParameters commandParameters)
  {
	  return "Q" + Integer.toString(commandParameters.getAxis());
  }
	
	
}
