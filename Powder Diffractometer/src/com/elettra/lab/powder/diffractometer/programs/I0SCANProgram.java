package com.elettra.lab.powder.diffractometer.programs;

import java.io.IOException;

import com.elettra.common.io.CommunicationPortException;
import com.elettra.common.io.ICommunicationPort;
import com.elettra.common.keithley.IPicoammeter;
import com.elettra.common.keithley.KeithleyPicoammeter;
import com.elettra.controller.driver.programs.MeasureParameters;
import com.elettra.controller.driver.programs.MeasureResult;
import com.elettra.controller.driver.programs.SCANProgram;

public class I0SCANProgram extends SCANProgram
{

	public static final String PROGRAM_NAME = "I0_SCAN";

	private IPicoammeter       picoammeter;

	public I0SCANProgram() throws IOException
	{
		super(PROGRAM_NAME);

		this.picoammeter = KeithleyPicoammeter.getInstance();
	}

	protected MeasureResult getMeasureFromDetector(ICommunicationPort port, MeasureParameters countParameters) throws CommunicationPortException
	{
		MeasureResult result = null;

		try
		{
			if (countParameters.getScanTime() > 10)
			{

				double average = 0.0;

				for (int index = 0; index < 5; index++)
				{
					average += this.picoammeter.readCurrent();

					try
					{
						Thread.sleep(200);
					}
					catch (InterruptedException exception)
					{
						exception.printStackTrace();
					}
				}

				result = super.getMeasureFromDetector(port, countParameters);


				for (int index = 0; index < 5; index++)
				{
					average += this.picoammeter.readCurrent();

					try
					{
						Thread.sleep(200);
					}
					catch (InterruptedException exception)
					{
						exception.printStackTrace();
					}
				}

				result.setAdditionalInformation1(average / 10);
			}
			else
			{
				result = super.getMeasureFromDetector(port, countParameters);
				result.setAdditionalInformation1(this.picoammeter.readCurrent());
			}
		}
		catch (IOException exception)
		{
			throw new CommunicationPortException(exception);
		}

		result.setAdditionalInformation2(-999.0);

		return result;
	}

}
