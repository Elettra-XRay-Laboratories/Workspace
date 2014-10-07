package com.elettra.controller.driver.commands.galil;

import com.elettra.common.io.CommunicationPortException;
import com.elettra.common.io.ICommunicationPort;
import com.elettra.controller.driver.commands.CommandParameters;
import com.elettra.controller.driver.commands.CommandResult;
import com.elettra.controller.driver.commands.CommandsFacade.Commands;
import com.elettra.controller.driver.commands.WriteCommand;
import com.elettra.controller.driver.common.AxisConfiguration;
import com.elettra.controller.driver.common.DriverUtilities;

public final class MotorConfigurationCommand extends WriteCommand
{

	public MotorConfigurationCommand()
	{
		super(Commands.MOTOR_CONFIGURATION);
	}

	protected String getUnformattedCommandString(CommandParameters commandParameters)
	{
		AxisConfiguration configuration = DriverUtilities.getAxisConfigurationMap().getAxisConfiguration(commandParameters.getAxis());

		return "MT" + DriverUtilities.getGalilAxis(commandParameters.getAxis()) + "=" + DriverUtilities.formatDoubleNumber(configuration.getMtValue());
	}

	protected final CommandResult executeSpecificCommand(String commandString, CommandParameters commandParameters, ICommunicationPort port) throws CommunicationPortException
	{
		super.executeSpecificCommand(commandString, commandParameters, port);

		return null;
	}

}
