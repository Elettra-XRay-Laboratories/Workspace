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
import com.elettra.controller.driver.programs.MoveParameters;
import com.elettra.controller.driver.programs.MoveResult;
import com.elettra.controller.driver.programs.ProgramParameters;
import com.elettra.controller.driver.programs.ProgramResult;
import com.elettra.lab.metrology.lpt.encoder.EncoderReaderFactory;

public class LPTMOVEProgram extends AbstractProgram
{
	public static final String	PROGRAM_NAME	= "LPT_MOVE";

	public LPTMOVEProgram()
	{
		super(PROGRAM_NAME);
	}

	public ProgramResult execute(ProgramParameters parameters, ICommunicationPort port) throws CommunicationPortException
	{
		double requestedAbsolutePosition = 0.0;
		double currentPosition = EncoderReaderFactory.getEncoderReader().readPosition();

		MoveParameters moveParameters = (MoveParameters) parameters;

		Object precisionObj = moveParameters.getCustomParameter("PRECISION");

		AxisConfiguration axisConfiguration = DriverUtilities.getAxisConfigurationMap().getAxisConfiguration(moveParameters.getAxis());

		if (axisConfiguration.isBlocked())
			throw new CommunicationPortException("Motor " + axisConfiguration.getName() + " is Blocked");

		String commandString = DriverUtilities.buildGalilCommand("SH" + DriverUtilities.getGalilAxis(moveParameters.getAxis()));
		commandString += DriverUtilities.buildGalilCommand("AC" + DriverUtilities.getGalilAxis(moveParameters.getAxis()) + "="
		    + Integer.toString(axisConfiguration.getRamp()));
		commandString += DriverUtilities.buildGalilCommand("DC" + DriverUtilities.getGalilAxis(moveParameters.getAxis()) + "="
		    + Integer.toString(axisConfiguration.getRamp()));
		commandString += DriverUtilities.buildGalilCommand("SP" + DriverUtilities.getGalilAxis(moveParameters.getAxis()) + "="
		    + Integer.toString(axisConfiguration.getSts()));

		if (moveParameters.getKindOfMovement().equals(DriverUtilities.getRelative()))
		{
			requestedAbsolutePosition = currentPosition
			    + DriverUtilities.controllerToNumber(new ControllerPosition(moveParameters.getSign(), moveParameters.getPosition()));

			commandString += DriverUtilities.buildGalilCommand("PR" + DriverUtilities.getGalilAxis(moveParameters.getAxis()) + "="
			    + StepConverter.toStep(moveParameters.getAxis(), moveParameters.getPosition()));
		}
		else if (moveParameters.getKindOfMovement().equals(DriverUtilities.getAbsolute()))
		{
			double finalPosition = DriverUtilities.controllerToNumber(new ControllerPosition(moveParameters.getSign(), moveParameters.getPosition()));

			requestedAbsolutePosition = finalPosition;

			commandString += DriverUtilities.buildGalilCommand("PR" + DriverUtilities.getGalilAxis(moveParameters.getAxis()) + "="
			    + StepConverter.toStep(moveParameters.getAxis(), finalPosition - currentPosition));
		}

		commandString += DriverUtilities.buildGalilCommand("BG" + DriverUtilities.getGalilAxis(moveParameters.getAxis()));

		new WritePort(commandString, port).start();

		moveParameters.getListener().signalAxisMovement(moveParameters.getAxis(), port);

		if (precisionObj != null)
		{
			CommandsFacade.waitForTheEndOfMovement(new CommandParameters(moveParameters.getAxis(), moveParameters.getListener()), port);

			this.correctPosition(moveParameters, port, requestedAbsolutePosition, Math.abs(((Double) precisionObj).doubleValue()));
		}
		
		return new MoveResult();
	}

	private void correctPosition(MoveParameters moveParameters, ICommunicationPort port, double requestedAbsolutePosition, double precision)
	    throws CommunicationPortException
	{
		double currentPosition = EncoderReaderFactory.getEncoderReader().readPosition();
		int trial = 0;

		while (Math.abs(requestedAbsolutePosition - currentPosition) >= precision && trial < 2)
		{
			double delta = requestedAbsolutePosition - currentPosition;

			String commandString = DriverUtilities.buildGalilCommand("PR" + DriverUtilities.getGalilAxis(moveParameters.getAxis()) + "="
			    + StepConverter.toStep(moveParameters.getAxis(), delta));
			commandString += DriverUtilities.buildGalilCommand("BG" + DriverUtilities.getGalilAxis(moveParameters.getAxis()));

			new WritePort(commandString, port).start();

			CommandsFacade.waitForTheEndOfMovement(new CommandParameters(moveParameters.getAxis(), moveParameters.getListener()), port);

			currentPosition = EncoderReaderFactory.getEncoderReader().readPosition();
			trial++;
		}

		moveParameters.getListener().signalAxisMovement(moveParameters.getAxis(), port);	
	}

	class WritePort extends Thread
	{
		private String		         commandString;
		private ICommunicationPort	port;

		public WritePort(String commandString, ICommunicationPort port)
		{
			super();
			this.commandString = commandString;
			this.port = port;

			this.setPriority(MAX_PRIORITY);
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
