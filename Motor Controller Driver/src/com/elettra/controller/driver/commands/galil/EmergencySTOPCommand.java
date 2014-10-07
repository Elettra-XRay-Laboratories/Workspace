package com.elettra.controller.driver.commands.galil;

import com.elettra.controller.driver.commands.CommandParameters;
import com.elettra.controller.driver.commands.WriteCommand;
import com.elettra.controller.driver.commands.CommandsFacade.Commands;
import com.elettra.controller.driver.common.DriverUtilities;

public final class EmergencySTOPCommand extends WriteCommand
{
	public EmergencySTOPCommand()
	{
		super(Commands.EMERGENCY_STOP);
	}

	protected String getUnformattedCommandString(CommandParameters commandParameters)
	{
		String commandString = DriverUtilities.buildGalilCommand("AB 0");
		commandString += "MO";

		return commandString;
	}
}
