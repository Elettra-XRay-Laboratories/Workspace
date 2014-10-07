package com.elettra.controller.driver.commands.huber;

import com.elettra.controller.driver.commands.CommandParameters;
import com.elettra.controller.driver.commands.ReadCommand;
import com.elettra.controller.driver.commands.CommandsFacade.Actions;

public final class RequestIsMotorMovingAction extends ReadCommand
{
  public RequestIsMotorMovingAction()
	{
		super(Actions.REQUEST_IS_MOTOR_MOVING);
	}

	protected String getUnformattedCommandString(CommandParameters commandParameters)
	{
		return "?S" + commandParameters.getAxis();
	}
}
