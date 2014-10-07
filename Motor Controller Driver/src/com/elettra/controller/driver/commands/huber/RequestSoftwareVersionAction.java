package com.elettra.controller.driver.commands.huber;

import com.elettra.controller.driver.commands.CommandParameters;
import com.elettra.controller.driver.commands.ReadCommand;
import com.elettra.controller.driver.commands.CommandsFacade.Actions;

public final class RequestSoftwareVersionAction extends ReadCommand
{

	public RequestSoftwareVersionAction()
	{
		super(Actions.REQUEST_SOFTWARE_VERSION);
	}

	protected String getUnformattedCommandString(CommandParameters commandParameters)
	{
		return "?V";
	}
}
