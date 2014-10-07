package com.elettra.controller.driver.programs.huber;

import com.elettra.common.io.ICommunicationPort;
import com.elettra.common.io.CommunicationPortException;
import com.elettra.controller.driver.commands.CommandParameters;
import com.elettra.controller.driver.commands.CommandsFacade;
import com.elettra.controller.driver.commands.CommandsFacade.Actions;
import com.elettra.controller.driver.common.AxisConfiguration;
import com.elettra.controller.driver.common.DriverUtilities;
import com.elettra.controller.driver.common.ControllerPosition;
import com.elettra.controller.driver.programs.AbstractProgram;
import com.elettra.controller.driver.programs.MoveParameters;
import com.elettra.controller.driver.programs.MoveResult;
import com.elettra.controller.driver.programs.ProgramParameters;
import com.elettra.controller.driver.programs.ProgramResult;
import com.elettra.controller.driver.programs.ProgramsFacade.Programs;

public class MOVEProgram extends AbstractProgram
{

	public MOVEProgram()
	{
		super(Programs.MOVE);
	}

	public ProgramResult execute(ProgramParameters parameters, ICommunicationPort port) throws CommunicationPortException
	{
		MoveParameters moveParameters = (MoveParameters) parameters;

		AxisConfiguration axisConfiguration = DriverUtilities.getAxisConfigurationMap().getAxisConfiguration(moveParameters.getAxis());

		if (axisConfiguration.isBrokenAxis() && moveParameters.getKindOfMovement().equals(DriverUtilities.getRelative()))
		{
			String position = CommandsFacade.executeAction(Actions.REQUEST_AXIS_POSITION, new CommandParameters(moveParameters.getAxis(), moveParameters.getListener()), port);

			double currentPosition = DriverUtilities.parseAxisPositionResponse(moveParameters.getAxis(), position).getSignedPosition();
			double relativePosition = DriverUtilities.controllerToNumber(new ControllerPosition(moveParameters.getSign(), moveParameters.getPosition())); /* qui il segno e' quello video */

			double newAbsolutePosition = currentPosition + relativePosition;

			ControllerPosition newPosition = DriverUtilities.numberToController(newAbsolutePosition);

			moveParameters.setKindOfMovement(DriverUtilities.getAbsolute());
			moveParameters.setPosition(newPosition.getAbsolutePosition());
			moveParameters.setSign(DriverUtilities.getSignProduct(newPosition.getSign(), axisConfiguration.getSignToPositive())); /* ripristino quello reale */
		}

		String commandString = DriverUtilities.buildHuberCommand("CLR");
		commandString += DriverUtilities.buildHuberCommand("CA" + Integer.toString(moveParameters.getAxis()));

		if (moveParameters.getKindOfMovement().equals(DriverUtilities.getRelative()))
		{
			if (moveParameters.getSign().equals(DriverUtilities.getMinus()))
			{
				double backlashPosition = moveParameters.getPosition() + DriverUtilities.getBacklash(axisConfiguration.getMeasureUnit());

				commandString += DriverUtilities.buildHuberCommand(Integer.toString(moveParameters.getAxis()) + ":-" + DriverUtilities.formatControllerPosition(backlashPosition) + axisConfiguration.getHuberString());
				commandString += DriverUtilities.buildHuberCommand("NL");
				commandString += DriverUtilities.buildHuberCommand(Integer.toString(moveParameters.getAxis()) + ":+" + DriverUtilities.formatControllerPosition(DriverUtilities.getBacklash(axisConfiguration.getMeasureUnit())) + axisConfiguration.getHuberString());
			}
			else if (moveParameters.getSign().equals(DriverUtilities.getPlus()))
				commandString += DriverUtilities.buildHuberCommand(Integer.toString(moveParameters.getAxis()) + ":+" + DriverUtilities.formatControllerPosition(moveParameters.getPosition()) + axisConfiguration.getHuberString());
		}
		else if (moveParameters.getKindOfMovement().equals(DriverUtilities.getAbsolute()))
		{
			String position = CommandsFacade.executeAction(Actions.REQUEST_AXIS_POSITION, new CommandParameters(moveParameters.getAxis(), moveParameters.getListener()), port);

			double currentPosition = DriverUtilities.parseAxisPositionResponse(moveParameters.getAxis(), position).getSignedPosition();
			double finalPosition = DriverUtilities.controllerToNumber(new ControllerPosition(moveParameters.getSign(), moveParameters.getPosition()));

			if (finalPosition < currentPosition)
			{
				ControllerPosition backlashPosition = DriverUtilities.numberToController(finalPosition - DriverUtilities.getBacklash(axisConfiguration.getMeasureUnit()));

				commandString += DriverUtilities.buildHuberCommand(Integer.toString(moveParameters.getAxis()) + ":A" + backlashPosition.getSign() + DriverUtilities.formatControllerPosition(backlashPosition.getAbsolutePosition()) + axisConfiguration.getHuberString());
				commandString += DriverUtilities.buildHuberCommand("NL");
				commandString += DriverUtilities.buildHuberCommand(Integer.toString(moveParameters.getAxis()) + ":+" + DriverUtilities.formatControllerPosition(DriverUtilities.getBacklash(axisConfiguration.getMeasureUnit())) + axisConfiguration.getHuberString());
			}
			else
				commandString += DriverUtilities.buildHuberCommand(Integer.toString(moveParameters.getAxis()) + ":A" + moveParameters.getSign() + DriverUtilities.formatControllerPosition(moveParameters.getPosition()) + axisConfiguration.getHuberString());
		}

		commandString += DriverUtilities.buildHuberCommand("START:");

		commandString = this.correctCommandString(commandString);

		port.write(commandString);

		moveParameters.getListener().signalAxisMovement(moveParameters.getAxis(), port);

		if (axisConfiguration.isBrokenAxis())
		{
			CommandsFacade.waitForTheEndOfMovement(new CommandParameters(moveParameters.getAxis(), moveParameters.getListener()), port);

			port.write(commandString);

			moveParameters.getListener().signalAxisMovement(moveParameters.getAxis(), port);

			try
			{
				Thread.sleep(500);
			}
			catch (InterruptedException exception)
			{
			}
		}

		return new MoveResult();
	}

	private String correctCommandString(String commandString)
	{
		commandString = commandString.replace("+-", "-");
		commandString = commandString.replace("-0.0000", "+0.0000");

		return commandString;
	}
}
