package com.elettra.controller.driver.programs.huber;

import com.elettra.common.io.CommunicationPortException;
import com.elettra.common.io.ICommunicationPort;
import com.elettra.controller.driver.commands.CommandParameters;
import com.elettra.controller.driver.commands.CommandsFacade;
import com.elettra.controller.driver.commands.CommandsFacade.Actions;
import com.elettra.controller.driver.common.AxisConfiguration;
import com.elettra.controller.driver.common.ControllerPosition;
import com.elettra.controller.driver.common.DriverUtilities;
import com.elettra.controller.driver.programs.AbstractProgram;
import com.elettra.controller.driver.programs.MoveResult;
import com.elettra.controller.driver.programs.ProgramParameters;
import com.elettra.controller.driver.programs.ProgramResult;
import com.elettra.controller.driver.programs.ProgramsFacade.Programs;
import com.elettra.controller.driver.programs.TwoMotorsMoveParameters;

public class TwoMotorsMOVEProgram extends AbstractProgram
{

	public TwoMotorsMOVEProgram()
	{
		super(Programs.TWOMMOVE);
	}

	public ProgramResult execute(ProgramParameters parameters, ICommunicationPort port) throws CommunicationPortException
	{
		TwoMotorsMoveParameters moveParameters = (TwoMotorsMoveParameters) parameters;

		AxisConfiguration axisConfiguration1 = DriverUtilities.getAxisConfigurationMap().getAxisConfiguration(moveParameters.getAxis1());
		AxisConfiguration axisConfiguration2 = DriverUtilities.getAxisConfigurationMap().getAxisConfiguration(moveParameters.getAxis2());

		this.checkLimitations(port, moveParameters.getAxis1(), moveParameters, axisConfiguration1);
		this.checkLimitations(port, moveParameters.getAxis2(), moveParameters, axisConfiguration2);
		
		if (axisConfiguration1.isBrokenAxis() || axisConfiguration2.isBrokenAxis())
			throw new CommunicationPortException("Broken axis cannot be used in two motors movements");
		
		String commandString = DriverUtilities.buildHuberCommand("CLR");
		commandString += DriverUtilities.buildHuberCommand("CA" + Integer.toString(moveParameters.getAxis1()));

		if (moveParameters.getKindOfMovement().equals(DriverUtilities.getRelative()))
		{
			if (moveParameters.getSign().equals(DriverUtilities.getMinus()))
			{
				double backlashPosition = moveParameters.getPosition() + DriverUtilities.getBacklash(axisConfiguration1.getMeasureUnit());

				commandString += DriverUtilities.buildHuberCommand(Integer.toString(moveParameters.getAxis1()) + ":-" + DriverUtilities.formatControllerPosition(backlashPosition) + axisConfiguration1.getHuberString());
				commandString += DriverUtilities.buildHuberCommand(Integer.toString(moveParameters.getAxis2()) + ":-" + DriverUtilities.formatControllerPosition(backlashPosition) + axisConfiguration2.getHuberString());
				commandString += DriverUtilities.buildHuberCommand("NL");
				commandString += DriverUtilities.buildHuberCommand(Integer.toString(moveParameters.getAxis1()) + ":+" + DriverUtilities.formatControllerPosition(DriverUtilities.getBacklash(axisConfiguration1.getMeasureUnit())) + axisConfiguration1.getHuberString());
				commandString += DriverUtilities.buildHuberCommand(Integer.toString(moveParameters.getAxis2()) + ":+" + DriverUtilities.formatControllerPosition(DriverUtilities.getBacklash(axisConfiguration2.getMeasureUnit())) + axisConfiguration2.getHuberString());
			}
			else if (moveParameters.getSign().equals(DriverUtilities.getPlus()))
			{
				commandString += DriverUtilities.buildHuberCommand(Integer.toString(moveParameters.getAxis1()) + ":+" + DriverUtilities.formatControllerPosition(moveParameters.getPosition()) + axisConfiguration1.getHuberString());
				commandString += DriverUtilities.buildHuberCommand(Integer.toString(moveParameters.getAxis2()) + ":+" + DriverUtilities.formatControllerPosition(moveParameters.getPosition()) + axisConfiguration2.getHuberString());
			}
		}
		else if (moveParameters.getKindOfMovement().equals(DriverUtilities.getAbsolute()))
		{
			String position1 = CommandsFacade.executeAction(Actions.REQUEST_AXIS_POSITION, new CommandParameters(moveParameters.getAxis1(), moveParameters.getListener()), port);
			String position2 = CommandsFacade.executeAction(Actions.REQUEST_AXIS_POSITION, new CommandParameters(moveParameters.getAxis2(), moveParameters.getListener()), port);

			double axis1Position = DriverUtilities.parseAxisPositionResponse(moveParameters.getAxis1(), position1).getSignedPosition();
			double axis2Position = DriverUtilities.parseAxisPositionResponse(moveParameters.getAxis2(), position2).getSignedPosition();

			if (axis1Position != axis2Position)
				throw new CommunicationPortException("Position of the two motors should be identical");
			
			double currentPosition = axis1Position;
			double finalPosition = DriverUtilities.controllerToNumber(new ControllerPosition(moveParameters.getSign(), moveParameters.getPosition()));

			if (finalPosition < currentPosition)
			{
				ControllerPosition backlashPosition = DriverUtilities.numberToController(finalPosition - DriverUtilities.getBacklash(axisConfiguration1.getMeasureUnit()));

				commandString += DriverUtilities.buildHuberCommand(Integer.toString(moveParameters.getAxis1()) + ":A" + backlashPosition.getSign() + DriverUtilities.formatControllerPosition(backlashPosition.getAbsolutePosition()) + axisConfiguration1.getHuberString());
				commandString += DriverUtilities.buildHuberCommand(Integer.toString(moveParameters.getAxis2()) + ":A" + backlashPosition.getSign() + DriverUtilities.formatControllerPosition(backlashPosition.getAbsolutePosition()) + axisConfiguration2.getHuberString());
				commandString += DriverUtilities.buildHuberCommand("NL");
				commandString += DriverUtilities.buildHuberCommand(Integer.toString(moveParameters.getAxis1()) + ":+" + DriverUtilities.formatControllerPosition(DriverUtilities.getBacklash(axisConfiguration1.getMeasureUnit())) + axisConfiguration1.getHuberString());
				commandString += DriverUtilities.buildHuberCommand(Integer.toString(moveParameters.getAxis2()) + ":+" + DriverUtilities.formatControllerPosition(DriverUtilities.getBacklash(axisConfiguration2.getMeasureUnit())) + axisConfiguration2.getHuberString());
			}
			else
			{
				commandString += DriverUtilities.buildHuberCommand(Integer.toString(moveParameters.getAxis1()) + ":A" + moveParameters.getSign() + DriverUtilities.formatControllerPosition(moveParameters.getPosition()) + axisConfiguration1.getHuberString());
				commandString += DriverUtilities.buildHuberCommand(Integer.toString(moveParameters.getAxis2()) + ":A" + moveParameters.getSign() + DriverUtilities.formatControllerPosition(moveParameters.getPosition()) + axisConfiguration2.getHuberString());
			}
		}

		commandString += DriverUtilities.buildHuberCommand("START:");

		commandString = this.correctCommandString(commandString);

		port.write(commandString);

		moveParameters.getListener().signalAxisMovement(moveParameters.getAxis1(), port);
		moveParameters.getListener().signalAxisMovement(moveParameters.getAxis2(), port);

		return new MoveResult();
	}

	private String correctCommandString(String commandString)
	{
		commandString = commandString.replace("+-", "-");
		commandString = commandString.replace("-0.0000", "+0.0000");

		return commandString;
	}

	private void checkLimitations(ICommunicationPort port, int axis, TwoMotorsMoveParameters moveParameters, AxisConfiguration axisConfiguration) throws CommunicationPortException
	{
		if (axisConfiguration.isBlocked())
			throw new IllegalStateException("Move not Possible: Axis " + axisConfiguration.getName() + " is Blocked");

		if (axisConfiguration.isLimited())
		{
			double finalPosition = DriverUtilities.controllerToNumber(new ControllerPosition(moveParameters.getSign(), moveParameters.getPosition()));
			
			if (moveParameters.getKindOfMovement().equals(DriverUtilities.getRelative()))
			{
				double currentPosition = DriverUtilities.parseAxisPositionResponse(axis, CommandsFacade.executeAction(CommandsFacade.Actions.REQUEST_AXIS_POSITION, new CommandParameters(axis, moveParameters.getListener()), port)).getSignedPosition();

				finalPosition += currentPosition;
			}

			if (finalPosition < axisConfiguration.getLimitDown() || finalPosition > axisConfiguration.getLimitUp())
				throw new IllegalArgumentException("Move not Possible: Move Final Position (" + String.valueOf(finalPosition) +  ") lies outside limits for axis " + axisConfiguration.getName());

		}
	}
}
