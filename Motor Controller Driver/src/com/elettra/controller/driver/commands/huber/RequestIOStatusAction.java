package com.elettra.controller.driver.commands.huber;

import com.elettra.controller.driver.commands.CommandParameters;
import com.elettra.controller.driver.commands.ReadCommand;
import com.elettra.controller.driver.commands.CommandsFacade.Actions;

public final class RequestIOStatusAction extends ReadCommand
{
  public RequestIOStatusAction()
	{
		super(Actions.REQUEST_IO_STATUS);

	}

	protected String getUnformattedCommandString(CommandParameters commandParameters)
	{
		return "?IO";
	}
}
