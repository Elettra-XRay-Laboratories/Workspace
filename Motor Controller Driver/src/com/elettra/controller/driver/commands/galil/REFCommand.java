package com.elettra.controller.driver.commands.galil;

import com.elettra.common.io.CommunicationPortException;
import com.elettra.common.io.ICommunicationPort;
import com.elettra.controller.driver.commands.AbstractCommand;
import com.elettra.controller.driver.commands.CommandParameters;
import com.elettra.controller.driver.commands.CommandResult;
import com.elettra.controller.driver.commands.CommandsFacade.Commands;
import com.elettra.controller.driver.common.AxisConfiguration;
import com.elettra.controller.driver.common.DriverUtilities;
import com.elettra.controller.driver.common.StepConverter;

public final class REFCommand extends AbstractCommand
{
	public REFCommand()
	{
		super(Commands.REF);
	}

	protected String getUnformattedCommandString(CommandParameters commandParameters)
	{
		AxisConfiguration axisConfiguration = DriverUtilities.getAxisConfigurationMap().getAxisConfiguration(commandParameters.getAxis());

		String commandString = DriverUtilities.buildGalilCommand("SH" + DriverUtilities.getGalilAxis(commandParameters.getAxis()));
		commandString += DriverUtilities.buildGalilCommand("AC" + DriverUtilities.getGalilAxis(commandParameters.getAxis()) + "=" + Integer.toString(axisConfiguration.getRamp()));
		commandString += DriverUtilities.buildGalilCommand("DC" + DriverUtilities.getGalilAxis(commandParameters.getAxis()) + "=" + Integer.toString(axisConfiguration.getRamp()));
		commandString += DriverUtilities.buildGalilCommand("SP" + DriverUtilities.getGalilAxis(commandParameters.getAxis()) + "=1000");
		commandString += DriverUtilities.buildGalilCommand("CN ," + axisConfiguration.getRefSign());
		commandString += DriverUtilities.buildGalilCommand("FE" + DriverUtilities.getGalilAxis(commandParameters.getAxis()));
		commandString += DriverUtilities.buildGalilCommand("BG" + DriverUtilities.getGalilAxis(commandParameters.getAxis()));
		commandString += DriverUtilities.buildGalilCommand("AM" + DriverUtilities.getGalilAxis(commandParameters.getAxis()));
		commandString += DriverUtilities.buildGalilCommand("JG" + DriverUtilities.getGalilAxis(commandParameters.getAxis()) + "=500");
		commandString += DriverUtilities.buildGalilCommand("FI" + DriverUtilities.getGalilAxis(commandParameters.getAxis()));
		commandString += DriverUtilities.buildGalilCommand("BG" + DriverUtilities.getGalilAxis(commandParameters.getAxis()));
		commandString += DriverUtilities.buildGalilCommand("AM" + DriverUtilities.getGalilAxis(commandParameters.getAxis()));

		if (axisConfiguration.getReferenceShift() != 0.0)
			commandString += DriverUtilities.buildGalilCommand("DP" + DriverUtilities.getGalilAxis(commandParameters.getAxis()) + "=" + StepConverter.toStep(commandParameters.getAxis(), axisConfiguration.getReferenceShift()));

		commandString += "MO" + DriverUtilities.getGalilAxis(commandParameters.getAxis());

		return commandString;
	}

	protected final CommandResult executeSpecificCommand(String commandString, CommandParameters commandParameters, ICommunicationPort port) throws CommunicationPortException
	{
		port.write(commandString);

		commandParameters.getListener().signalAxisMovement(commandParameters.getAxis(), port);

		return null;
	}
}
