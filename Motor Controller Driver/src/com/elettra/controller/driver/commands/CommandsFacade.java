package com.elettra.controller.driver.commands;

import com.elettra.common.io.CommunicationPortException;
import com.elettra.common.io.ICommunicationPort;
import com.elettra.controller.driver.common.DriverUtilities;
import com.elettra.controller.driver.common.KindOfController;

public final class CommandsFacade
{
	private static AbstractCommand  huberCommandChainOfReponsibility;
	private static AbstractCommand  galilCommandChainOfReponsibility;
	private static AbstractCommand  huberActionChainOfReponsibility;
	private static AbstractCommand  galilActionChainOfReponsibility;

	private static AbstractCommand  lastOfHuberCommandChainOfReponsibility;
	private static AbstractCommand  lastOfGalilCommandChainOfReponsibility;
	private static AbstractCommand  lastOfHuberActionChainOfReponsibility;
	private static AbstractCommand  lastOfGalilActionChainOfReponsibility;

	private static KindOfController kindOfController = DriverUtilities.getKindOfController();

	public final class Commands
	{
		public static final String POS                 = "POS";
		public static final String STOP                = "STOP";
		public static final String REMOTE              = "REMOTE";
		public static final String REF                 = "REF";
		public static final String RUN                 = "RUN";
		public static final String STEP                = "STEP";
		public static final String FAST                = "FAST";
		public static final String LOCAL               = "LOCAL";
		public static final String MOTOR_CONFIGURATION = "MOTOR_CONFIGURATION";
		public static final String EMERGENCY_STOP      = "EMERGENCY_STOP";
	}

	public final class Actions
	{
		public static final String REQUEST_AXIS_POSITION    = "RequestAxisPosition";
		public static final String REQUEST_IO_STATUS        = "RequestIOStatus";
		public static final String REQUEST_SOFTWARE_VERSION = "RequestSoftwareVersion";
		public static final String REQUEST_IS_MOTOR_MOVING  = "RequestIsMotorMoving";
		public static final String REQUEST_ENCODER_COUNT    = "RequestEncoderCount";
	}

	static
	{
		huberCommandChainOfReponsibility = new com.elettra.controller.driver.commands.huber.POSCommand();
		lastOfHuberCommandChainOfReponsibility = huberCommandChainOfReponsibility.setNext(new com.elettra.controller.driver.commands.huber.REFCommand())
		    .setNext(new com.elettra.controller.driver.commands.huber.STOPCommand())
		    .setNext(new com.elettra.controller.driver.commands.huber.RUNCommand())
		    .setNext(new com.elettra.controller.driver.commands.huber.FASTCommand())
		    .setNext(new com.elettra.controller.driver.commands.huber.STEPCommand())
		    .setNext(new com.elettra.controller.driver.commands.huber.REMOTECommand())
		    .setNext(new com.elettra.controller.driver.commands.huber.LOCALCommand());

		huberActionChainOfReponsibility = new com.elettra.controller.driver.commands.huber.RequestAxisPositionAction();
		lastOfHuberActionChainOfReponsibility = huberActionChainOfReponsibility.setNext(new com.elettra.controller.driver.commands.huber.RequestIsMotorMovingAction())
		    .setNext(new com.elettra.controller.driver.commands.huber.RequestIOStatusAction())
		    .setNext(new com.elettra.controller.driver.commands.huber.RequestSoftwareVersionAction());

		galilCommandChainOfReponsibility = new com.elettra.controller.driver.commands.galil.POSCommand();
		lastOfGalilCommandChainOfReponsibility = galilCommandChainOfReponsibility.setNext(new com.elettra.controller.driver.commands.galil.REFCommand())
		    .setNext(new com.elettra.controller.driver.commands.galil.STOPCommand())
		    .setNext(new com.elettra.controller.driver.commands.galil.RUNCommand())
		    .setNext(new com.elettra.controller.driver.commands.galil.FASTCommand())
		    .setNext(new com.elettra.controller.driver.commands.galil.STEPCommand())
		    .setNext(new com.elettra.controller.driver.commands.galil.REMOTECommand())
		    .setNext(new com.elettra.controller.driver.commands.galil.LOCALCommand())
		    .setNext(new com.elettra.controller.driver.commands.galil.MotorConfigurationCommand())
		    .setNext(new com.elettra.controller.driver.commands.galil.EmergencySTOPCommand());

		galilActionChainOfReponsibility = new com.elettra.controller.driver.commands.galil.RequestAxisPositionAction();
		lastOfGalilActionChainOfReponsibility = galilActionChainOfReponsibility.setNext(new com.elettra.controller.driver.commands.galil.RequestIsMotorMovingAction())
		    .setNext(new com.elettra.controller.driver.commands.galil.RequestEncoderCount())
		    .setNext(new com.elettra.controller.driver.commands.galil.RequestIOStatusAction())
		    .setNext(new com.elettra.controller.driver.commands.galil.RequestSoftwareVersionAction());
	}

	public static synchronized void addCustomCommand(AbstractCommand command)
	{
		if (kindOfController.equals(DriverUtilities.getHuberController()))
			lastOfHuberCommandChainOfReponsibility = lastOfHuberCommandChainOfReponsibility.setNext(command);
		else if (kindOfController.equals(DriverUtilities.getGalilController()))
			lastOfGalilCommandChainOfReponsibility = lastOfGalilCommandChainOfReponsibility.setNext(command);

	}

	public static synchronized void addCustomAction(AbstractCommand action)
	{
		if (kindOfController.equals(DriverUtilities.getHuberController()))
			lastOfHuberActionChainOfReponsibility = lastOfHuberActionChainOfReponsibility.setNext(action);
		else if (kindOfController.equals(DriverUtilities.getGalilController()))
			lastOfGalilActionChainOfReponsibility = lastOfGalilActionChainOfReponsibility.setNext(action);
	}

	protected static synchronized final ICommand getCommand(String commandName)
	{
		ICommand command = null;

		if (kindOfController.equals(DriverUtilities.getHuberController()))
			command = CommandsFacade.huberCommandChainOfReponsibility.getCommand(commandName);
		else if (kindOfController.equals(DriverUtilities.getGalilController()))
			command = CommandsFacade.galilCommandChainOfReponsibility.getCommand(commandName);

		if (command == null)
			throw new UnexistingCommandException(commandName);

		return command;
	}

	protected static synchronized final ICommand getAction(String actionName)
	{
		ICommand action = null;

		if (kindOfController.equals(DriverUtilities.getHuberController()))
			action = CommandsFacade.huberActionChainOfReponsibility.getCommand(actionName);
		else if (kindOfController.equals(DriverUtilities.getGalilController()))
			action = CommandsFacade.galilActionChainOfReponsibility.getCommand(actionName);

		if (action == null)
			throw new UnexistingCommandException(actionName);

		return action;
	}

	public static synchronized final void executeCommand(String commandName, CommandParameters commandParameters, ICommunicationPort port) throws CommunicationPortException
	{
		CommandsFacade.getCommand(commandName).execute(commandParameters, port);
	}

	public static synchronized final String executeAction(String actionName, CommandParameters actionParameters, ICommunicationPort port) throws CommunicationPortException
	{
		return CommandsFacade.getAction(actionName).execute(actionParameters, port).getResponse();
	}

	public static void waitForTheEndOfMovement(CommandParameters actionParameters, ICommunicationPort port) throws CommunicationPortException
	{
		boolean isMotorStopped = false;

		try
		{
			while (!isMotorStopped)
			{
				actionParameters.getListener().signalAxisMovement(actionParameters.getAxis(), port);
				
				String actionResponse = CommandsFacade.executeAction(Actions.REQUEST_IS_MOTOR_MOVING, actionParameters, port);

				Thread.sleep(10);

				isMotorStopped = CommandUtilities.isMotorStopped(actionResponse);
			}
		}
		catch (InterruptedException exception)
		{
			exception.printStackTrace();

			isMotorStopped = true;
		}

	}
}
