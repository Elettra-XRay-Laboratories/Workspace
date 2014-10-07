package com.elettra.controller.driver.programs.huber;

import java.util.Locale;

import com.elettra.common.io.CommunicationPortException;
import com.elettra.common.io.ICommunicationPort;
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

		String commandString = DriverUtilities.buildHuberCommand("CLR");
		commandString += DriverUtilities.buildHuberCommand("CCNT");
		commandString += DriverUtilities.buildHuberCommand("CNT" + String.format(Locale.US, "%4.1f", countParameters.getScanTime()).trim());
		commandString += "START:";

		port.write(DriverUtilities.buildHuberCommand(commandString));

		try
		{
			Thread.sleep((long) (countParameters.getScanTime() * 1000 + 2000));
		}
		catch (InterruptedException exception)
		{
			throw new RuntimeException("Impossibile to wait the proper delay time", exception);
		}

		commandString = DriverUtilities.buildHuberCommand("?C");

		// eseguo tre volte il comando per gestire un problema del controller.
		String response = null;

		for (int trial = 0; trial < 3; trial++)
		{
			port.write(commandString);
			response = port.read();

			try
			{
				Thread.sleep(100);
			}
			catch (InterruptedException exception)
			{
			}
		}

		return new MeasureResult(Integer.parseInt(response));
	}
}
