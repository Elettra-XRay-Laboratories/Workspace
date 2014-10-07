package com.elettra.controller.driver.commands.galil;

import com.elettra.controller.driver.commands.CommandParameters;
import com.elettra.controller.driver.commands.ReadCommand;
import com.elettra.controller.driver.commands.CommandsFacade.Actions;
import com.elettra.controller.driver.common.DriverUtilities;

public final class RequestIsMotorMovingAction extends ReadCommand
{
	public RequestIsMotorMovingAction()
	{
		super(Actions.REQUEST_IS_MOTOR_MOVING);
	}

	protected String getUnformattedCommandString(CommandParameters commandParameters)
	{
		return "MG _BG" + DriverUtilities.getGalilAxis(commandParameters.getAxis());
	}
}
