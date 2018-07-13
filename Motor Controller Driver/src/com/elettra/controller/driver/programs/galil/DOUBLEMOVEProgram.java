package com.elettra.controller.driver.programs.galil;

import com.elettra.common.io.CommunicationPortException;
import com.elettra.common.io.ICommunicationPort;
import com.elettra.controller.driver.commands.CommandParameters;
import com.elettra.controller.driver.commands.CommandsFacade;
import com.elettra.controller.driver.commands.CommandsFacade.Actions;
import com.elettra.controller.driver.common.AxisConfiguration;
import com.elettra.controller.driver.common.ControllerPosition;
import com.elettra.controller.driver.common.DriverUtilities;
import com.elettra.controller.driver.common.MultipleAxis;
import com.elettra.controller.driver.common.Sign;
import com.elettra.controller.driver.programs.AbstractProgram;
import com.elettra.controller.driver.programs.DoubleMoveParameters;
import com.elettra.controller.driver.programs.MoveParameters;
import com.elettra.controller.driver.programs.MoveResult;
import com.elettra.controller.driver.programs.ProgramParameters;
import com.elettra.controller.driver.programs.ProgramResult;
import com.elettra.controller.driver.programs.ProgramsFacade;
import com.elettra.controller.driver.programs.ProgramsFacade.Programs;

public class DOUBLEMOVEProgram extends AbstractProgram
{

	public DOUBLEMOVEProgram()
	{
		super(Programs.DOUBLEMOVE);
	}

	public ProgramResult execute(ProgramParameters parameters, ICommunicationPort port) throws CommunicationPortException
	{
		DoubleMoveParameters moveParameters = (DoubleMoveParameters) parameters;

		AxisConfiguration axisConfiguration = DriverUtilities.getAxisConfigurationMap().getAxisConfiguration(moveParameters.getAxis());

		if (axisConfiguration.isMultiple())
		{
			this.checkLimitations(port, moveParameters, axisConfiguration);

			MultipleAxis multipleAxis = axisConfiguration.getMultipleAxis();

			MoveParameters axis1MoveParameters = new MoveParameters(multipleAxis.getAxis1(), moveParameters.getListener());
			axis1MoveParameters.setKindOfMovement(moveParameters.getKindOfMovement());

			MoveParameters axis2MoveParameters = new MoveParameters(multipleAxis.getAxis2(), moveParameters.getListener());
			axis2MoveParameters.setKindOfMovement(moveParameters.getKindOfMovement());

			if (moveParameters.getReferenceAxis() == 2)
			{
				axis1MoveParameters.setPosition(moveParameters.getPosition() / 2.0);
				axis1MoveParameters.setSign(DriverUtilities.getSignProduct(moveParameters.getSign(), multipleAxis.getRelativeSign()));
				axis2MoveParameters.setPosition(moveParameters.getPosition());
				axis2MoveParameters.setSign(moveParameters.getSign());
			}
			else if (moveParameters.getReferenceAxis() == 1)
			{
				axis1MoveParameters.setPosition(moveParameters.getPosition());
				axis1MoveParameters.setSign(moveParameters.getSign());
				axis2MoveParameters.setPosition(moveParameters.getPosition() * 2.0);
				axis2MoveParameters.setSign(DriverUtilities.getSignProduct(moveParameters.getSign(), multipleAxis.getRelativeSign()));
			}

			Sign signToPositive1 = DriverUtilities.getAxisConfigurationMap().getAxisConfiguration(multipleAxis.getAxis1()).getSignToPositive();
			Sign signToPositive2 = DriverUtilities.getAxisConfigurationMap().getAxisConfiguration(multipleAxis.getAxis2()).getSignToPositive();

			if (moveParameters.getKindOfMovement().equals(DriverUtilities.getAbsolute()))
			{
				String position = CommandsFacade.executeAction(Actions.REQUEST_AXIS_POSITION, new CommandParameters(multipleAxis.getAxis2(), moveParameters.getListener()), port);

				double currentPosition = 0;
				Sign movementRealSign = DriverUtilities.getPlus();

				if (moveParameters.getReferenceAxis() == 2)
				{
					currentPosition = DriverUtilities.parseAxisPositionResponse(multipleAxis.getAxis2(), position).getSignedPosition();
					movementRealSign = signToPositive2;
				}
				else if (moveParameters.getReferenceAxis() == 1)
				{
					currentPosition = DriverUtilities.parseAxisPositionResponse(multipleAxis.getAxis1(), position).getSignedPosition();
					movementRealSign = signToPositive1;
				}

				double newPosition = DriverUtilities.controllerToNumber(new ControllerPosition(moveParameters.getSign(), moveParameters.getPosition()));

				if ((movementRealSign.sign() * currentPosition) < (movementRealSign.sign() * newPosition))
				{
					ProgramsFacade.executeProgram(ProgramsFacade.Programs.MOVE, axis2MoveParameters, port);
					CommandsFacade.waitForTheEndOfMovement(new CommandParameters(multipleAxis.getAxis2(), moveParameters.getListener()), port);

					ProgramsFacade.executeProgram(ProgramsFacade.Programs.MOVE, axis1MoveParameters, port);
					CommandsFacade.waitForTheEndOfMovement(new CommandParameters(multipleAxis.getAxis1(), moveParameters.getListener()), port);
				}
				else
				{
					ProgramsFacade.executeProgram(ProgramsFacade.Programs.MOVE, axis1MoveParameters, port);
					CommandsFacade.waitForTheEndOfMovement(new CommandParameters(multipleAxis.getAxis1(), moveParameters.getListener()), port);

					ProgramsFacade.executeProgram(ProgramsFacade.Programs.MOVE, axis2MoveParameters, port);
					CommandsFacade.waitForTheEndOfMovement(new CommandParameters(multipleAxis.getAxis2(), moveParameters.getListener()), port);
				}
			}
			else if (moveParameters.getKindOfMovement().equals(DriverUtilities.getRelative()))
			{
				Sign movementRealSign = DriverUtilities.getPlus();

				if (moveParameters.getReferenceAxis() == 2)
					movementRealSign = DriverUtilities.getSignProduct(moveParameters.getSign(), signToPositive2);
				else if (moveParameters.getReferenceAxis() == 1)
					movementRealSign = DriverUtilities.getSignProduct(moveParameters.getSign(), signToPositive1);

				if (movementRealSign.equals(DriverUtilities.getMinus()))
				{
					ProgramsFacade.executeProgram(ProgramsFacade.Programs.MOVE, axis1MoveParameters, port);
					CommandsFacade.waitForTheEndOfMovement(new CommandParameters(multipleAxis.getAxis1(), moveParameters.getListener()), port);

					ProgramsFacade.executeProgram(ProgramsFacade.Programs.MOVE, axis2MoveParameters, port);
					CommandsFacade.waitForTheEndOfMovement(new CommandParameters(multipleAxis.getAxis2(), moveParameters.getListener()), port);
				}
				else if (movementRealSign.equals(DriverUtilities.getPlus()))
				{
					ProgramsFacade.executeProgram(ProgramsFacade.Programs.MOVE, axis2MoveParameters, port);
					CommandsFacade.waitForTheEndOfMovement(new CommandParameters(multipleAxis.getAxis2(), moveParameters.getListener()), port);

					ProgramsFacade.executeProgram(ProgramsFacade.Programs.MOVE, axis1MoveParameters, port);
					CommandsFacade.waitForTheEndOfMovement(new CommandParameters(multipleAxis.getAxis1(), moveParameters.getListener()), port);
				}
				else
					throw new IllegalArgumentException("Sign not recognized:" + moveParameters.getSign());
			}
			else
				throw new IllegalArgumentException("Kind Of Movement not recognized:" + moveParameters.getKindOfMovement());
		}
		else
			throw new IllegalArgumentException("Asked double move for a non multiple axis");

		return new MoveResult();
	}

	private void checkLimitations(ICommunicationPort port, DoubleMoveParameters moveParameters, AxisConfiguration axisConfiguration) throws CommunicationPortException
	{
		double signedFinalPosition =  DriverUtilities.controllerToNumber(new ControllerPosition(moveParameters.getSign(), moveParameters.getPosition()));
		
		MultipleAxis axisNumbers = axisConfiguration.getMultipleAxis();

		AxisConfiguration axisConfiguration1 = DriverUtilities.getAxisConfigurationMap().getAxisConfiguration(axisNumbers.getAxis1());
		AxisConfiguration axisConfiguration2 = DriverUtilities.getAxisConfigurationMap().getAxisConfiguration(axisNumbers.getAxis2());

		double signedFinalPosition1 = 0.0;
		double signedFinalPosition2 = 0.0;

		if (axisNumbers.getDefaultReferenceAxis() == 2)
		{
			signedFinalPosition1 = axisNumbers.getRelativeSign().sign() * signedFinalPosition / 2.0;
			signedFinalPosition2 = signedFinalPosition;
		}
		else if (axisNumbers.getDefaultReferenceAxis() == 1)
		{
			signedFinalPosition1 = signedFinalPosition;
			signedFinalPosition2 = axisNumbers.getRelativeSign().sign() * signedFinalPosition * 2.0;
		}

		this.checkSingleAxisLimitations(port, axisNumbers.getAxis1(), moveParameters, axisConfiguration1, signedFinalPosition1);
		this.checkSingleAxisLimitations(port, axisNumbers.getAxis2(), moveParameters, axisConfiguration2, signedFinalPosition2);
	}

	private void checkSingleAxisLimitations(ICommunicationPort port, int axis, DoubleMoveParameters moveParameters, AxisConfiguration axisConfiguration, double signedFinalPosition) throws CommunicationPortException
	{
		if (axisConfiguration.isBlocked())
			throw new IllegalStateException("Scan not Possible: Axis " + axisConfiguration.getName() + " is Blocked");

		if (axisConfiguration.isLimited())
		{
			if (moveParameters.getKindOfMovement().equals(DriverUtilities.getRelative()))
			{
				double actualPosition = DriverUtilities.parseAxisPositionResponse(axis, CommandsFacade.executeAction(CommandsFacade.Actions.REQUEST_AXIS_POSITION, new CommandParameters(axis, moveParameters.getListener()), port)).getSignedPosition();

				signedFinalPosition += actualPosition;
			}

			if (signedFinalPosition < axisConfiguration.getLimitDown() || signedFinalPosition > axisConfiguration.getLimitUp())
				throw new IllegalArgumentException("Move not Possible: Move Final Position (" + String.valueOf(signedFinalPosition) +  ") lies outside limits for axis " + axisConfiguration.getName());
		}
	}
}
