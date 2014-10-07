package com.elettra.controller.driver.programs.huber;

import com.elettra.common.io.CommunicationPortException;
import com.elettra.common.io.ICommunicationPort;
import com.elettra.controller.driver.commands.CommandParameters;
import com.elettra.controller.driver.commands.CommandsFacade;
import com.elettra.controller.driver.commands.CommandsFacade.Actions;
import com.elettra.controller.driver.common.AxisConfiguration;
import com.elettra.controller.driver.common.DriverUtilities;
import com.elettra.controller.driver.common.ControllerPosition;
import com.elettra.controller.driver.common.MultipleAxis;
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

			if (moveParameters.getKindOfMovement().equals(DriverUtilities.getAbsolute()))
			{
				String position = CommandsFacade.executeAction(Actions.REQUEST_AXIS_POSITION, new CommandParameters(multipleAxis.getAxis2(), moveParameters.getListener()), port);

				double currentPosition = DriverUtilities.parseAxisPositionResponse(multipleAxis.getAxis2(), position).getSignedPosition();
				double newPosition = DriverUtilities.controllerToNumber(new ControllerPosition(moveParameters.getSign(), moveParameters.getPosition()));

				if (currentPosition < newPosition)
				{
					ProgramsFacade.executeProgram(ProgramsFacade.Programs.MOVE, axis1MoveParameters, port);
					CommandsFacade.waitForTheEndOfMovement(new CommandParameters(multipleAxis.getAxis1(), moveParameters.getListener()), port);

					ProgramsFacade.executeProgram(ProgramsFacade.Programs.MOVE, axis2MoveParameters, port);
					CommandsFacade.waitForTheEndOfMovement(new CommandParameters(multipleAxis.getAxis2(), moveParameters.getListener()), port);
				}
				else
				{
					ProgramsFacade.executeProgram(ProgramsFacade.Programs.MOVE, axis2MoveParameters, port);
					CommandsFacade.waitForTheEndOfMovement(new CommandParameters(multipleAxis.getAxis2(), moveParameters.getListener()), port);

					ProgramsFacade.executeProgram(ProgramsFacade.Programs.MOVE, axis1MoveParameters, port);
					CommandsFacade.waitForTheEndOfMovement(new CommandParameters(multipleAxis.getAxis1(), moveParameters.getListener()), port);
				}
			}
			else if (moveParameters.getKindOfMovement().equals(DriverUtilities.getRelative()))
			{
				if (moveParameters.getSign().equals(DriverUtilities.getMinus()))
				{
					ProgramsFacade.executeProgram(ProgramsFacade.Programs.MOVE, axis1MoveParameters, port);
					CommandsFacade.waitForTheEndOfMovement(new CommandParameters(multipleAxis.getAxis1(), moveParameters.getListener()), port);

					ProgramsFacade.executeProgram(ProgramsFacade.Programs.MOVE, axis2MoveParameters, port);
					CommandsFacade.waitForTheEndOfMovement(new CommandParameters(multipleAxis.getAxis2(), moveParameters.getListener()), port);
				}
				else if (moveParameters.getSign().equals(DriverUtilities.getPlus()))
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
}
