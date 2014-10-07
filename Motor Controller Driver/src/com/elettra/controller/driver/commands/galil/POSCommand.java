package com.elettra.controller.driver.commands.galil;

import com.elettra.common.io.ICommunicationPort;
import com.elettra.common.io.CommunicationPortException;
import com.elettra.controller.driver.commands.CommandParameters;
import com.elettra.controller.driver.commands.CommandResult;
import com.elettra.controller.driver.commands.WriteCommand;
import com.elettra.controller.driver.commands.CommandsFacade.Commands;
import com.elettra.controller.driver.common.AxisConfiguration;
import com.elettra.controller.driver.common.ControllerPosition;
import com.elettra.controller.driver.common.DriverUtilities;
import com.elettra.controller.driver.common.StepConverter;

public final class POSCommand extends WriteCommand
{
	public POSCommand()
	{
		super(Commands.POS);

	}

	protected String getUnformattedCommandString(CommandParameters commandParameters)
	{
		AxisConfiguration configuration = DriverUtilities.getAxisConfigurationMap().getAxisConfiguration(commandParameters.getAxis());

		ControllerPosition position = new ControllerPosition(DriverUtilities.getSignProduct(commandParameters.getSign(), configuration.getSignToPositive()), commandParameters.getPosition());

		return "DP" + DriverUtilities.getGalilAxis(commandParameters.getAxis()) + "=" + StepConverter.toStep(commandParameters.getAxis(), position.getSignedPosition());
	}

	protected final CommandResult executeSpecificCommand(String commandString, CommandParameters commandParameters, ICommunicationPort port) throws CommunicationPortException
	{
		super.executeSpecificCommand(commandString, commandParameters, port);

		commandParameters.getListener().signalAxisMovement(commandParameters.getAxis(), port);

		return null;
	}

}
