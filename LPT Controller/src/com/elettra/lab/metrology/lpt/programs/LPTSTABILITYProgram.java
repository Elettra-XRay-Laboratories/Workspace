package com.elettra.lab.metrology.lpt.programs;

import java.io.IOException;

import com.elettra.common.io.CommunicationPortException;
import com.elettra.common.io.ICommunicationPort;
import com.elettra.controller.driver.programs.MeasureParameters;
import com.elettra.controller.driver.programs.MeasureResult;
import com.elettra.controller.driver.programs.ProgramParameters;
import com.elettra.controller.driver.programs.ProgramResult;
import com.elettra.controller.driver.programs.STABILITYProgram;
import com.elettra.controller.driver.programs.StabilityDurations;
import com.elettra.controller.driver.programs.StabilityScan;
import com.elettra.controller.gui.common.GuiUtilities;
import com.elettra.idsccd.driver.IDSCCDColorModes;
import com.elettra.idsccd.driver.IDSCCDDisplayModes;
import com.elettra.idsccd.driver.IDSCCDException;
import com.elettra.idsccd.driver.IDSCCDFactory;
import com.elettra.idsccd.driver.IIDSCCD;
import com.elettra.idsccd.driver.Point;
import com.elettra.lab.metrology.lpt.panels.References;

public class LPTSTABILITYProgram extends STABILITYProgram
{
	public static final String	PROGRAM_NAME	        = "LTP_STABILITY";

	public static final String	Y	                    = "Y";
	public static final String	X                    	= "X";
	public static final String	Y_STANDARD_DEVIATION	= "Y_STANDARD_DEVIATION";
	public static final String	X_STANDARD_DEVIATION	= "X_STANDARD_DEVIATION";

	public static final String	STEP_DURATION	       = "STEP_DURATION";
	public static final String	COLOR_MODE	         = "COLOR_MODE";
	public static final String	DIM_X	               = "DIM_X";
	public static final String	DIM_Y	               = "DIM_Y";
	public static final String	NUMBER_OF_CAPTURES	 = "NUMBER_OF_CAPTURES";
  
	private IIDSCCD	           ccd;
	private int	               numberOfCaptures;
	private int	               dimx;
	private int	               dimy;
	private IDSCCDColorModes	 mode;
	private double	           X0;
	private double	           focalDistance;

	public LPTSTABILITYProgram()
	{
		super(PROGRAM_NAME);
		
		this.ccd = IDSCCDFactory.getIDSCCD();
	}
	
	public ProgramResult execute(ProgramParameters parameters, ICommunicationPort port) throws CommunicationPortException
	{
		int stepDuration = ((Integer) parameters.getCustomParameter(STEP_DURATION)).intValue();

		scanConfiguration.put(StabilityDurations.TEN_MIN, new StabilityScan(12*50/stepDuration, stepDuration));
		scanConfiguration.put(StabilityDurations.THIRTY_MIN, new StabilityScan(36*50/stepDuration, stepDuration));
		scanConfiguration.put(StabilityDurations.ONE_HOUR, new StabilityScan(72*50/stepDuration, stepDuration));
		scanConfiguration.put(StabilityDurations.THREE_HOURS, new StabilityScan(216*50/stepDuration, stepDuration));
		scanConfiguration.put(StabilityDurations.FIVE_HOURS, new StabilityScan(360*50/stepDuration, stepDuration));
		scanConfiguration.put(StabilityDurations.TEN_HOURS, new StabilityScan(720*50/stepDuration, stepDuration));
		scanConfiguration.put(StabilityDurations.TWENTYFOUR_HOURS, new StabilityScan(1730*50/stepDuration, stepDuration));
		scanConfiguration.put(StabilityDurations.TWO_DAYS, new StabilityScan(3450*50/stepDuration, stepDuration));
		scanConfiguration.put(StabilityDurations.THREE_DAYS, new StabilityScan(5180*50/stepDuration, stepDuration));
		scanConfiguration.put(StabilityDurations.TEN_DAYS, new StabilityScan(17280*50/stepDuration, stepDuration));

		try
		{
			try
			{
				this.X0 = Double.parseDouble(References.getInstance().get(References.LTP_X_0));
				this.focalDistance = Double.parseDouble(References.getInstance().get(References.FOCAL_DISTANCE));
			}
			catch (Exception e)
			{
				throw new IllegalArgumentException("Reference values X0 or Focal Distance not defined! Please configure them");
			}

			this.mode = (IDSCCDColorModes) parameters.getCustomParameter(COLOR_MODE);

			this.dimx = ((Integer) parameters.getCustomParameter(DIM_X)).intValue();
			this.dimy = ((Integer) parameters.getCustomParameter(DIM_Y)).intValue();
			this.numberOfCaptures = ((Integer) parameters.getCustomParameter(NUMBER_OF_CAPTURES)).intValue();
			
			this.ccd.setColorMode(mode);
			this.ccd.setDisplayMode(IDSCCDDisplayModes.IS_SET_DM_DIB);

			return super.execute(parameters, port);
		}
		catch (IDSCCDException e)
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

	public synchronized void initialize() throws IDSCCDException, IllegalAccessException
	{
		this.ccd.init();
	}

	public synchronized void terminate() throws IDSCCDException, IllegalAccessException
	{
		this.ccd.exit();
	}

	protected MeasureResult getCountsFromDetector(ICommunicationPort port, MeasureParameters measureParameters) throws CommunicationPortException
	{
		try
		{
			Thread.sleep((long) measureParameters.getScanTime()*1000);
			
			double[] x_positions = new double[this.numberOfCaptures];
			double[] y_positions = new double[this.numberOfCaptures];

			double average_x_position = 0.0;
			double average_y_position = 0.0;
			double x_standard_deviation = 0.0;
			double y_standard_deviation = 0.0;

			int[] buffer = null;
			Point centroid = null;

			for (int index = 0; index < this.numberOfCaptures; index++)
			{
				buffer = this.ccd.getImageByMemory(this.dimx, this.dimy, mode);

				try
				{
					centroid = this.ccd.getCentroid(buffer, this.dimx, this.dimy, false);
				}
				catch (Exception e)
				{
					throw new RuntimeException("Centroid not calculated, measurement interrupted");
				}

				x_positions[index] = centroid.x;
				average_x_position += centroid.x;

				y_positions[index] = centroid.y;
				average_y_position += centroid.y;
			}
					
			average_x_position = average_x_position / this.numberOfCaptures;
			average_y_position = average_y_position / this.numberOfCaptures;

			for (int index = 0; index < this.numberOfCaptures; index++)
			{
				x_standard_deviation += Math.pow(x_positions[index] - average_x_position, 2);
				y_standard_deviation += Math.pow(y_positions[index] - average_y_position, 2);
			}

			x_standard_deviation = this.numberOfCaptures > 1 ? x_standard_deviation / (this.numberOfCaptures - 1) : 0.0;
			y_standard_deviation = this.numberOfCaptures > 1 ? y_standard_deviation / (this.numberOfCaptures - 1) : 0.0;

			buffer = null;

			// Refer to the center of the image

			average_x_position = (average_x_position - this.dimx / 2);
			average_y_position = -(average_y_position - this.dimy / 2);
			average_x_position *= IIDSCCD.PIXEL_SIZE;
			average_y_position *= IIDSCCD.PIXEL_SIZE;
						
			MeasureResult result = new MeasureResult(this.calculateSlopeError(average_x_position));
			
			result.setAdditionalInformation1(Double.valueOf(average_x_position));
			result.setAdditionalInformation2(Double.valueOf(average_y_position));
			result.addCustomData(X, Double.valueOf(average_x_position));
			result.addCustomData(Y, Double.valueOf(average_y_position));
			
			return result;
		}
		catch (IDSCCDException e)
		{
			throw new CommunicationPortException(e.getMessage());
		}
		catch (Throwable t)
		{
			throw new CommunicationPortException(t);
		}
		finally
		{
			System.gc();
		}
	}

	private double calculateSlopeError(double average_x_position)
	{
		return 0.5 * Math.atan((average_x_position-this.X0) / (this.focalDistance*1000));
	}

	protected void openShutter() throws IOException, InterruptedException
	{
	}

	protected void closeShutter() throws IOException
	{
		GuiUtilities.showMessagePopup("Scan Completed!", null);
	}

}
