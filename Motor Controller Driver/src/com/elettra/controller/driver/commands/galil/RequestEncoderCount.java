package com.elettra.controller.driver.commands.galil;

import com.elettra.controller.driver.commands.CommandParameters;
import com.elettra.controller.driver.commands.CommandsFacade.Actions;
import com.elettra.controller.driver.commands.ReadCommand;
import com.elettra.controller.driver.common.DriverUtilities;

public final class RequestEncoderCount extends ReadCommand
{
	public RequestEncoderCount()
	{
		super(Actions.REQUEST_ENCODER_COUNT);
	}

	protected String getUnformattedCommandString(CommandParameters commandParameters)
	{
		long delay = (long) commandParameters.getPosition();
		String axis = DriverUtilities.getGalilAxis(commandParameters.getAxis());

		return "CE" + axis + "=3;DE" + axis + "=0;WT" + delay + ";TP" + axis;
	}
}
