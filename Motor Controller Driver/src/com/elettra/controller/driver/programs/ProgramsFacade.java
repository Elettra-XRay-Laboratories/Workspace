package com.elettra.controller.driver.programs;

import com.elettra.common.io.CommunicationPortException;
import com.elettra.common.io.ICommunicationPort;
import com.elettra.controller.driver.common.DriverUtilities;
import com.elettra.controller.driver.common.KindOfController;

public final class ProgramsFacade
{
	private static AbstractProgram  huberProgramChainOfReponsibility;
	private static AbstractProgram  galilProgramChainOfReponsibility;

	private static AbstractProgram  lastOfHuberProgramChainOfReponsibility;
	private static AbstractProgram  lastOfGalilProgramChainOfReponsibility;

	private static KindOfController kindOfController = DriverUtilities.getKindOfController();

	public final class Programs
	{
		public static final String COUNT      = "COUNT";
		public static final String MOVE       = "MOVE";
		public static final String DOUBLEMOVE = "DOUBLEMOVE";
		public static final String SCAN       = "SCAN";
		public static final String STABILITY  = "STABILITY";
		public static final String TMMOVE       = "TMMOVE";
	}

	static
	{
		huberProgramChainOfReponsibility = new com.elettra.controller.driver.programs.huber.MOVEProgram();
		lastOfHuberProgramChainOfReponsibility = huberProgramChainOfReponsibility.setNext(new com.elettra.controller.driver.programs.huber.COUNTProgram()).setNext(new SCANProgram()).setNext(new com.elettra.controller.driver.programs.huber.DOUBLEMOVEProgram()).setNext(new STABILITYProgram()).setNext(new com.elettra.controller.driver.programs.huber.TwoMotorsMOVEProgram());

		galilProgramChainOfReponsibility = new com.elettra.controller.driver.programs.galil.MOVEProgram();
		lastOfGalilProgramChainOfReponsibility = galilProgramChainOfReponsibility.setNext(new com.elettra.controller.driver.programs.galil.COUNTProgram()).setNext(new SCANProgram()).setNext(new com.elettra.controller.driver.programs.galil.DOUBLEMOVEProgram()).setNext(new STABILITYProgram());
	}

	public static synchronized void addCustomCommand(AbstractProgram program)
	{
		if (kindOfController.equals(DriverUtilities.getHuberController()))
			lastOfHuberProgramChainOfReponsibility = lastOfHuberProgramChainOfReponsibility.setNext(program);
		else if (kindOfController.equals(DriverUtilities.getGalilController()))
			lastOfGalilProgramChainOfReponsibility = lastOfGalilProgramChainOfReponsibility.setNext(program);

	}

	public static synchronized final IProgram getProgram(String programName)
	{
		IProgram program = null;

		if (kindOfController.equals(DriverUtilities.getHuberController()))
			program = ProgramsFacade.huberProgramChainOfReponsibility.getProgram(programName);
		else if (kindOfController.equals(DriverUtilities.getGalilController()))
			program = ProgramsFacade.galilProgramChainOfReponsibility.getProgram(programName);

		if (program == null)
			throw new UnexistingProgramException(programName);

		return program;
	}

	public static final synchronized ProgramResult executeProgram(String programName, ProgramParameters programParameters, ICommunicationPort port) throws CommunicationPortException
	{
		return ProgramsFacade.getProgram(programName).execute(programParameters, port);
	}
}
