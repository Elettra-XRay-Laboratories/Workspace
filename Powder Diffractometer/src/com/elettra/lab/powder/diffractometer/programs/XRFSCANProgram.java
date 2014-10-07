package com.elettra.lab.powder.diffractometer.programs;

import java.io.IOException;

import org.apache.commons.math.optimization.fitting.GaussianFitter;
import org.apache.commons.math.optimization.fitting.GaussianFunction;
import org.apache.commons.math.optimization.general.LevenbergMarquardtOptimizer;

import com.elettra.common.io.CommunicationPortException;
import com.elettra.common.io.ICommunicationPort;
import com.elettra.common.utilities.FileIni;
import com.elettra.controller.driver.programs.MeasureParameters;
import com.elettra.controller.driver.programs.MeasureResult;
import com.elettra.controller.driver.programs.ProgramParameters;
import com.elettra.controller.driver.programs.ProgramResult;
import com.elettra.controller.driver.programs.SCANProgram;
import com.elettra.controller.gui.common.GuiUtilities;
import com.elettra.lab.powder.mca.IMCA;
import com.elettra.lab.powder.mca.MCAException;
import com.elettra.lab.powder.mca.MCAFactory;

public class XRFSCANProgram extends SCANProgram
{
	public static final String PROGRAM_NAME        = "XRF_SCAN";

	private IMCA               mca;

	private static int         portNumber          = 0;
	private static int         baudrate            = 0;
	private static int         numberOfChannels    = 0;
	private static int         xrfPeakFirstChannel = 0;
	private static int         xrfPeakLastChannel  = 0;

	static
	{

		try
		{
			portNumber = Integer.parseInt(FileIni.getInstance().getProperty("MCAPortNumber"));
			baudrate = Integer.parseInt(FileIni.getInstance().getProperty("MCABaudrate"));
			numberOfChannels = Integer.parseInt(FileIni.getInstance().getProperty("MCANumberOfChannels"));
			xrfPeakFirstChannel = Integer.parseInt(FileIni.getInstance().getProperty("MCAXrfPeakFirstChannel"));
			xrfPeakLastChannel = Integer.parseInt(FileIni.getInstance().getProperty("MCAXrfPeakLastChannel"));
		}
		catch (Throwable t)
		{
			throw new RuntimeException(t);
		}
	}

	public XRFSCANProgram()
	{
		super(PROGRAM_NAME);
	}

	public ProgramResult execute(ProgramParameters parameters, ICommunicationPort port) throws CommunicationPortException
	{
		try
		{
			this.mca = MCAFactory.getMCA(portNumber, baudrate, numberOfChannels);

			try
			{
				ProgramResult result = super.execute(parameters, port);

				return result;
			}
			finally
			{
				this.mca.disconnect();
			}
		}
		catch (MCAException e)
		{
			throw new CommunicationPortException(e.getMessage());
		}
		catch (CommunicationPortException e)
		{
			throw e;
		}
		catch (Throwable t)
		{
			throw new CommunicationPortException(t);
		}
	}

	protected MeasureResult getMeasureFromDetector(ICommunicationPort port, MeasureParameters countParameters) throws CommunicationPortException
	{
		try
		{
			this.mca.clearDataAndTime();
			this.mca.doAcquisition((int) countParameters.getScanTime());
			int[] data = this.mca.getData();

			GaussianFitter fitter = new GaussianFitter(new LevenbergMarquardtOptimizer());

			for (int index = 0; index < numberOfChannels; index++)
				if (index >= xrfPeakFirstChannel && index <= xrfPeakLastChannel)
					fitter.addObservedPoint(index, data[index]);

			GaussianFunction fitFunction = fitter.fit();

			return new MeasureResult((int) (fitFunction.getA() + fitFunction.getB())); // shift + height
		}
		catch (MCAException e)
		{
			throw new CommunicationPortException(e.getMessage());
		}
		catch (Throwable t)
		{
			throw new CommunicationPortException(t);
		}
	}

	protected void openShutter() throws IOException, InterruptedException
	{
		GuiUtilities.showMessagePopup("Warning: Counts Refers to XRF Detector and MCA spectrum automatic fit procedure", null);
	}

	protected void closeShutter() throws IOException
	{
	}

}
