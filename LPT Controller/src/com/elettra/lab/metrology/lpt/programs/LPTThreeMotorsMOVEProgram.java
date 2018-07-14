package com.elettra.lab.metrology.lpt.programs;

import com.elettra.common.io.CommunicationPortException;
import com.elettra.common.io.ICommunicationPort;
import com.elettra.controller.driver.commands.CommandParameters;
import com.elettra.controller.driver.commands.CommandsFacade;
import com.elettra.controller.driver.common.AxisConfiguration;
import com.elettra.controller.driver.common.ControllerPosition;
import com.elettra.controller.driver.common.DriverUtilities;
import com.elettra.controller.driver.common.StepConverter;
import com.elettra.controller.driver.programs.AbstractProgram;
import com.elettra.controller.driver.programs.MoveResult;
import com.elettra.controller.driver.programs.ProgramParameters;
import com.elettra.controller.driver.programs.ProgramResult;

public class LPTThreeMotorsMOVEProgram extends AbstractProgram
{
	public static final String THREEMMOVE = "THREEMMOVE";

	public LPTThreeMotorsMOVEProgram()
	{
		super(THREEMMOVE);
	}

	public ProgramResult execute(ProgramParameters parameters, ICommunicationPort port) throws CommunicationPortException
	{
		ThreeMotorsMoveParameters moveParameters = (ThreeMotorsMoveParameters) parameters;

		AxisConfiguration axisConfiguration1 = DriverUtilities.getAxisConfigurationMap().getAxisConfiguration(moveParameters.getAxis());
		AxisConfiguration axisConfiguration2 = DriverUtilities.getAxisConfigurationMap().getAxisConfiguration(moveParameters.getAxis2());
		AxisConfiguration axisConfiguration3 = DriverUtilities.getAxisConfigurationMap().getAxisConfiguration(moveParameters.getAxis3());

		this.checkLimitations(port, moveParameters, axisConfiguration1);
		this.checkLimitations(port, moveParameters, axisConfiguration2);
		this.checkLimitations(port, moveParameters, axisConfiguration3);
					
		String commandString = DriverUtilities.buildGalilCommand("SH" + DriverUtilities.getGalilAxis(moveParameters.getAxis()) + DriverUtilities.getGalilAxis(moveParameters.getAxis2()) + DriverUtilities.getGalilAxis(moveParameters.getAxis3())  );
		
		commandString += DriverUtilities.buildGalilCommand("AC" + DriverUtilities.getGalilAxis(moveParameters.getAxis()) + "=" + Integer.toString(axisConfiguration1.getRamp()));
		commandString += DriverUtilities.buildGalilCommand("DC" + DriverUtilities.getGalilAxis(moveParameters.getAxis()) + "=" + Integer.toString(axisConfiguration1.getRamp()));
		commandString += DriverUtilities.buildGalilCommand("SP" + DriverUtilities.getGalilAxis(moveParameters.getAxis()) + "=" + Integer.toString(axisConfiguration1.getSts()));
		commandString += DriverUtilities.buildGalilCommand("AC" + DriverUtilities.getGalilAxis(moveParameters.getAxis2()) + "=" + Integer.toString(axisConfiguration2.getRamp()));
		commandString += DriverUtilities.buildGalilCommand("DC" + DriverUtilities.getGalilAxis(moveParameters.getAxis2()) + "=" + Integer.toString(axisConfiguration2.getRamp()));
		commandString += DriverUtilities.buildGalilCommand("SP" + DriverUtilities.getGalilAxis(moveParameters.getAxis2()) + "=" + Integer.toString(axisConfiguration2.getSts()));
		commandString += DriverUtilities.buildGalilCommand("AC" + DriverUtilities.getGalilAxis(moveParameters.getAxis3()) + "=" + Integer.toString(axisConfiguration3.getRamp()));
		commandString += DriverUtilities.buildGalilCommand("DC" + DriverUtilities.getGalilAxis(moveParameters.getAxis3()) + "=" + Integer.toString(axisConfiguration3.getRamp()));
		commandString += DriverUtilities.buildGalilCommand("SP" + DriverUtilities.getGalilAxis(moveParameters.getAxis3()) + "=" + Integer.toString(axisConfiguration3.getSts()));

		/* inversione del segno con quello reale */
		moveParameters.setSign(DriverUtilities.getSignProduct(moveParameters.getSign(), axisConfiguration1.getSignToPositive()));

		if (moveParameters.getKindOfMovement().equals(DriverUtilities.getRelative()))
			commandString += DriverUtilities.buildGalilCommand("PR" + DriverUtilities.getGalilAxis(moveParameters.getAxis()) + "=" + StepConverter.toStep(moveParameters.getAxis(), moveParameters.getPosition()));
		else if (moveParameters.getKindOfMovement().equals(DriverUtilities.getAbsolute()))
			commandString += DriverUtilities.buildGalilCommand("PA" + DriverUtilities.getGalilAxis(moveParameters.getAxis()) + "=" + moveParameters.getSign() + StepConverter.toStep(moveParameters.getAxis(), moveParameters.getPosition()));

		moveParameters.setSign(DriverUtilities.getSignProduct(moveParameters.getSign(), axisConfiguration2.getSignToPositive()));

		if (moveParameters.getKindOfMovement().equals(DriverUtilities.getRelative()))
			commandString += DriverUtilities.buildGalilCommand("PR" + DriverUtilities.getGalilAxis(moveParameters.getAxis2()) + "=" + StepConverter.toStep(moveParameters.getAxis2(), moveParameters.getPosition()));
		else if (moveParameters.getKindOfMovement().equals(DriverUtilities.getAbsolute()))
			commandString += DriverUtilities.buildGalilCommand("PA" + DriverUtilities.getGalilAxis(moveParameters.getAxis2()) + "=" + moveParameters.getSign() + StepConverter.toStep(moveParameters.getAxis2(), moveParameters.getPosition()));

		moveParameters.setSign(DriverUtilities.getSignProduct(moveParameters.getSign(), axisConfiguration3.getSignToPositive()));

		if (moveParameters.getKindOfMovement().equals(DriverUtilities.getRelative()))
			commandString += DriverUtilities.buildGalilCommand("PR" + DriverUtilities.getGalilAxis(moveParameters.getAxis3()) + "=" + StepConverter.toStep(moveParameters.getAxis3(), moveParameters.getPosition()));
		else if (moveParameters.getKindOfMovement().equals(DriverUtilities.getAbsolute()))
			commandString += DriverUtilities.buildGalilCommand("PA" + DriverUtilities.getGalilAxis(moveParameters.getAxis3()) + "=" + moveParameters.getSign() + StepConverter.toStep(moveParameters.getAxis3(), moveParameters.getPosition()));

		commandString += DriverUtilities.buildGalilCommand("BG" + DriverUtilities.getGalilAxis(moveParameters.getAxis()) + DriverUtilities.getGalilAxis(moveParameters.getAxis2()) + DriverUtilities.getGalilAxis(moveParameters.getAxis3()));
		
		System.out.println(commandString);
		
		new WritePort(commandString, port).start();

		moveParameters.getListener().signalAxisMovement(moveParameters.getAxis1(), port);
		moveParameters.getListener().signalAxisMovement(moveParameters.getAxis2(), port);
		moveParameters.getListener().signalAxisMovement(moveParameters.getAxis3(), port);

		return new MoveResult();
	}

	private void checkLimitations(ICommunicationPort port, ThreeMotorsMoveParameters moveParameters, AxisConfiguration axisConfiguration) throws CommunicationPortException
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
