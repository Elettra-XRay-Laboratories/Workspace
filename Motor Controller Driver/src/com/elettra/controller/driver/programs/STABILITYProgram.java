package com.elettra.controller.driver.programs;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import com.elettra.common.io.CommunicationPortException;
import com.elettra.common.io.ICommunicationPort;
import com.elettra.controller.driver.listeners.MeasurePoint;
import com.elettra.controller.driver.listeners.Progress;
import com.elettra.controller.driver.programs.ProgramsFacade.Programs;

public class STABILITYProgram extends ShutterActivatorProgram
{
	protected HashMap<String, StabilityScan> scanConfiguration;

	public STABILITYProgram(String programName)
	{
		super(programName);

		scanConfiguration = new HashMap<String, StabilityScan>();

		scanConfiguration.put(StabilityDurations.TEN_MIN, new StabilityScan(12));
		scanConfiguration.put(StabilityDurations.THIRTY_MIN, new StabilityScan(36));
		scanConfiguration.put(StabilityDurations.ONE_HOUR, new StabilityScan(72));
		scanConfiguration.put(StabilityDurations.THREE_HOURS, new StabilityScan(216));
		scanConfiguration.put(StabilityDurations.FIVE_HOURS, new StabilityScan(360));
		scanConfiguration.put(StabilityDurations.TEN_HOURS, new StabilityScan(720));
		scanConfiguration.put(StabilityDurations.TWENTYFOUR_HOURS, new StabilityScan(1730));
		scanConfiguration.put(StabilityDurations.TWO_DAYS, new StabilityScan(3450));
		scanConfiguration.put(StabilityDurations.THREE_DAYS, new StabilityScan(5180));
		scanConfiguration.put(StabilityDurations.TEN_DAYS, new StabilityScan(17280));
	}

	public STABILITYProgram()
	{
		super(Programs.STABILITY);
	}

	public ProgramResult execute(ProgramParameters parameters, ICommunicationPort port) throws CommunicationPortException
	{
		ScanResult result = new ScanResult();
		StabilityParameters scanParameters = (StabilityParameters) parameters;

		try
		{
			scanParameters.getListener().signalScanStart();

			StabilityScan stabilityScan = scanConfiguration.get(scanParameters.getScanDuration());

			scanParameters.getListener().signalXAxisRange(scanParameters.getAxis(), 0.0, stabilityScan.getTotalDuration());

			// --------------------------------------------------------------------
			// scan cycle
			// --------------------------------------------------------------------

			this.openShutter();

			MeasureParameters countParameters = new MeasureParameters(scanParameters.getAxis(), scanParameters.getListener());
			countParameters.setScanTime(stabilityScan.getStepDuration());

			Progress progress = new Progress();

			BufferedWriter dumper = new BufferedWriter(new FileWriter("./data/laststabilityscan.dump"));

			double scanActualPosition = 0.0;
			try
			{
				for (int scanIndex = 0; scanIndex < stabilityScan.getSteps() && !scanParameters.getListener().isStopScanActivated(scanParameters.getAxis()); scanIndex++)
				{
					progress.setProgress((int) ((((double) (scanIndex + 1)) / ((double) stabilityScan.getSteps())) * 100));

					this.doMeasure(port, scanParameters, result, scanActualPosition, countParameters, progress, dumper);

					scanActualPosition += stabilityScan.getStepDuration();
				}

				if (!scanParameters.getListener().isStopScanActivated(scanParameters.getAxis()))
					this.doMeasure(port, scanParameters, result, scanActualPosition, countParameters, progress, dumper);
			}
			finally
			{
				dumper.close();
			}
		}
		catch (InterruptedException e)
		{
			throw new CommunicationPortException(e);
		}
		catch (IOException e)
		{
			throw new CommunicationPortException(e);
		}
		finally
		{
			try
			{
				this.closeShutter();
			}
			catch (IOException e)
			{
				throw new CommunicationPortException(e);
			}
			finally
			{
				scanParameters.getListener().signalScanStop();
			}
		}

		return result;
	}

	// --------------------------------------------------------------------
	//
	// PRIVATE METHODS
	//
	// --------------------------------------------------------------------

	private void doMeasure(ICommunicationPort port, StabilityParameters scanParameters, ScanResult result, double scanActualPosition, MeasureParameters countParameters, Progress progress, BufferedWriter dumper) throws IOException
	{
		MeasureResult countResult = getCountsFromDetector(port, countParameters);

		double measureXCoordinate = scanActualPosition;

		MeasurePoint measurePoint = new MeasurePoint(measureXCoordinate, countResult.getMeasure(), countResult.getAdditionalInformation1(), countResult.getAdditionalInformation2());
		result.addMeasurePoint(measurePoint);

		dumper.write(String.format("%7.4f", measurePoint.getX()).trim() + " " + measurePoint.getMeasure() + " " + measurePoint.getAdditionalInformation1() + " " + measurePoint.getAdditionalInformation2());
		dumper.newLine();
		dumper.flush();

		scanParameters.getListener().signalMeasure(scanParameters.getAxis(), measurePoint, progress, port);
	}

	protected MeasureResult getCountsFromDetector(ICommunicationPort port, MeasureParameters countParameters) throws CommunicationPortException
	{
		return (MeasureResult) ProgramsFacade.executeProgram(ProgramsFacade.Programs.COUNT, countParameters, port);
	}
}
