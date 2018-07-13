package com.elettra.controller.driver.programs.galil;

import com.elettra.common.io.CommunicationPortException;
import com.elettra.common.io.ICommunicationPort;
import com.elettra.controller.driver.commands.CommandParameters;
import com.elettra.controller.driver.commands.CommandsFacade;
import com.elettra.controller.driver.common.AxisConfiguration;
import com.elettra.controller.driver.common.ControllerPosition;
import com.elettra.controller.driver.common.DriverUtilities;
import com.elettra.controller.driver.common.StepConverter;
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

		this.checkLimitations(port, moveParameters, axisConfiguration);
					
		String commandString = DriverUtilities.buildGalilCommand("SH" + DriverUtilities.getGalilAxis(moveParameters.getAxis()));
		commandString += DriverUtilities.buildGalilCommand("AC" + DriverUtilities.getGalilAxis(moveParameters.getAxis()) + "=" + Integer.toString(axisConfiguration.getRamp()));
		commandString += DriverUtilities.buildGalilCommand("DC" + DriverUtilities.getGalilAxis(moveParameters.getAxis()) + "=" + Integer.toString(axisConfiguration.getRamp()));
		commandString += DriverUtilities.buildGalilCommand("SP" + DriverUtilities.getGalilAxis(moveParameters.getAxis()) + "=" + Integer.toString(axisConfiguration.getSts()));

		/* inversione del segno con quello reale */
		moveParameters.setSign(DriverUtilities.getSignProduct(moveParameters.getSign(), axisConfiguration.getSignToPositive()));

		if (moveParameters.getKindOfMovement().equals(DriverUtilities.getRelative()))
		{
			boolean conditionToBacklash = axisConfiguration.getSignToPositive().equals(DriverUtilities.getPlus()) ? moveParameters.getSign().equals(DriverUtilities.getMinus()) : moveParameters.getSign().equals(DriverUtilities.getPlus());

			if (conditionToBacklash)
			{
				int backlashPosition = StepConverter.toStep(moveParameters.getAxis(), moveParameters.getPosition()) + StepConverter.toStep(moveParameters.getAxis(), DriverUtilities.getBacklash(axisConfiguration.getMeasureUnit()));

				commandString += DriverUtilities.buildGalilCommand("PR" + DriverUtilities.getGalilAxis(moveParameters.getAxis()) + "=-" + Integer.toString(backlashPosition));
				commandString += DriverUtilities.buildGalilCommand("BG" + DriverUtilities.getGalilAxis(moveParameters.getAxis()));

				new WritePort(commandString, port).start();

				moveParameters.getListener().signalAxisMovement(moveParameters.getAxis(), port);

				CommandsFacade.waitForTheEndOfMovement(new CommandParameters(moveParameters.getAxis(), moveParameters.getListener()), port);

				commandString = DriverUtilities.buildGalilCommand("PR" + DriverUtilities.getGalilAxis(moveParameters.getAxis()) + "=+" + StepConverter.toStep(moveParameters.getAxis(), DriverUtilities.getBacklash(axisConfiguration.getMeasureUnit())));
			}
			else
				commandString += DriverUtilities.buildGalilCommand("PR" + DriverUtilities.getGalilAxis(moveParameters.getAxis()) + "=" + StepConverter.toStep(moveParameters.getAxis(), moveParameters.getPosition()));
		}
		else if (moveParameters.getKindOfMovement().equals(DriverUtilities.getAbsolute()))
		{
			double currentPosition = DriverUtilities.parseAxisPositionResponse(moveParameters.getAxis(), CommandsFacade.executeAction(CommandsFacade.Actions.REQUEST_AXIS_POSITION, new CommandParameters(moveParameters.getAxis(), moveParameters.getListener()), port)).getSignedPosition();
			double finalPosition = DriverUtilities.controllerToNumber(new ControllerPosition(moveParameters.getSign(), moveParameters.getPosition()));

			boolean conditionToBacklash = axisConfiguration.getSignToPositive().equals(DriverUtilities.getPlus()) ? finalPosition < currentPosition : finalPosition > currentPosition;

			if (conditionToBacklash)
			{
				int backlashPosition = StepConverter.toStep(moveParameters.getAxis(), finalPosition) - StepConverter.toStep(moveParameters.getAxis(), DriverUtilities.getBacklash(axisConfiguration.getMeasureUnit()));

				commandString += DriverUtilities.buildGalilCommand("PA" + DriverUtilities.getGalilAxis(moveParameters.getAxis()) + "=" + Integer.toString(backlashPosition));
				commandString += DriverUtilities.buildGalilCommand("BG" + DriverUtilities.getGalilAxis(moveParameters.getAxis()));

				new WritePort(commandString, port).start();

				moveParameters.getListener().signalAxisMovement(moveParameters.getAxis(), port);

				CommandsFacade.waitForTheEndOfMovement(new CommandParameters(moveParameters.getAxis(), moveParameters.getListener()), port);

				commandString = DriverUtilities.buildGalilCommand("PR" + DriverUtilities.getGalilAxis(moveParameters.getAxis()) + "=+" + StepConverter.toStep(moveParameters.getAxis(), DriverUtilities.getBacklash(axisConfiguration.getMeasureUnit())));
			}
			else
				commandString += DriverUtilities.buildGalilCommand("PA" + DriverUtilities.getGalilAxis(moveParameters.getAxis()) + "=" + moveParameters.getSign() + StepConverter.toStep(moveParameters.getAxis(), moveParameters.getPosition()));
		}

		commandString += DriverUtilities.buildGalilCommand("BG" + DriverUtilities.getGalilAxis(moveParameters.getAxis()));

		new WritePort(commandString, port).start();

		moveParameters.getListener().signalAxisMovement(moveParameters.getAxis(), port);

		return new MoveResult();
	}

	private void checkLimitations(ICommunicationPort port, MoveParameters moveParameters, AxisConfiguration axisConfiguration) throws CommunicationPortException
	{
		if (axisConfiguration.isBlocked())
			throw new IllegalStateException("Move not Possible: Axis " + axisConfiguration.getName() + " is Blocked");

		if (axisConfiguration.isLimited())
		{
			double finalPosition = DriverUtilities.controllerToNumber(new ControllerPosition(moveParameters.getSign(), moveParameters.getPosition()));
			
			if (moveParameters.getKindOfMovement().equals(DriverUtilities.getRelative()))
			{
				double currentPosition = DriverUtilities.parseAxisPositionResponse(moveParameters.getAxis(), CommandsFacade.executeAction(CommandsFacade.Actions.REQUEST_AXIS_POSITION, new CommandParameters(moveParameters.getAxis(), moveParameters.getListener()), port)).getSignedPosition();

				finalPosition += currentPosition;
			}

			if (finalPosition < axisConfiguration.getLimitDown() || finalPosition > axisConfiguration.getLimitUp())
				throw new IllegalArgumentException("Move not Possible: Move Final Position (" + String.valueOf(finalPosition) +  ") lies outside limits for axis " + axisConfiguration.getName());

		}
	}
	class WritePort extends Thread
	{
		private String             commandString;
		private ICommunicationPort port;

		public WritePort(String commandString, ICommunicationPort port)
		{
			super();
			this.commandString = commandString;
			this.port = port;
		}

		public void run()
		{
			try
			{
				this.port.write(this.commandString);
			}
			catch (CommunicationPortException exception)
			{
				exception.printStackTrace();
			}
			finally
			{
			}
		}
	}
}
