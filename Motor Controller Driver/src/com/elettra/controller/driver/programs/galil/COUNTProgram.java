package com.elettra.controller.driver.programs.galil;

import com.elettra.common.io.CommunicationPortException;
import com.elettra.common.io.ICommunicationPort;
import com.elettra.controller.driver.commands.CommandParameters;
import com.elettra.controller.driver.commands.CommandsFacade;
import com.elettra.controller.driver.commands.CommandsFacade.Actions;
import com.elettra.controller.driver.common.DriverUtilities;
import com.elettra.controller.driver.programs.AbstractProgram;
import com.elettra.controller.driver.programs.MeasureParameters;
import com.elettra.controller.driver.programs.MeasureResult;
import com.elettra.controller.driver.programs.ProgramParameters;
import com.elettra.controller.driver.programs.ProgramResult;
import com.elettra.controller.driver.programs.ProgramsFacade.Programs;

public class COUNTProgram extends AbstractProgram
{

	public COUNTProgram()
	{
		super(Programs.COUNT);
	}

	public ProgramResult execute(ProgramParameters parameters, ICommunicationPort port) throws CommunicationPortException
	{
		MeasureParameters countParameters = (MeasureParameters) parameters;

		String countingAxis = DriverUtilities.getCountingAxis();

		String response = CommandsFacade.executeAction(Actions.REQUEST_ENCODER_COUNT, new CommandParameters(DriverUtilities.getAxis(countingAxis), null, countParameters.getScanTime() * 1000, countParameters.getListener()), port);

		return new MeasureResult(Math.abs(Integer.parseInt(response.substring(response.lastIndexOf(" ")).trim())));
	}
}
