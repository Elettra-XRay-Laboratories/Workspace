package com.elettra.lab.metrology.lpt.programs;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import com.elettra.common.io.CommunicationPortException;
import com.elettra.common.io.ICommunicationPort;
import com.elettra.controller.driver.programs.MeasureParameters;
import com.elettra.controller.driver.programs.MeasureResult;
import com.elettra.controller.driver.programs.MoveParameters;
import com.elettra.controller.driver.programs.ProgramParameters;
import com.elettra.controller.driver.programs.ProgramResult;
import com.elettra.controller.driver.programs.ProgramsFacade;
import com.elettra.controller.driver.programs.SCANProgram;
import com.elettra.controller.gui.common.GuiUtilities;
import com.elettra.idsccd.driver.IDSCCDColorModes;
import com.elettra.idsccd.driver.IDSCCDDisplayModes;
import com.elettra.idsccd.driver.IDSCCDException;
import com.elettra.idsccd.driver.IDSCCDFactory;
import com.elettra.idsccd.driver.IIDSCCD;
import com.elettra.lab.metrology.lpt.Axis;
import com.elettra.lab.metrology.lpt.encoder.EncoderReaderFactory;

public class LPTScanProgram extends SCANProgram
{
	public static final String	ENCODER_POSITION	   = "ENCODER_POSITION";
	public static final String	LAST_IMAGE	         = "LAST_IMAGE";
	public static final String	Y_STANDARD_DEVIATION	= "Y_STANDARD_DEVIATION";
	public static final String	X_STANDARD_DEVIATION	= "X_STANDARD_DEVIATION";
	public static final String	PROGRAM_NAME	       = "LPT_SCAN";

	public static final String	COLOR_MODE	         = "COLOR_MODE";
	public static final String	DIM_X	               = "DIM_X";
	public static final String	DIM_Y	               = "DIM_Y";
	public static final String	NUMBER_OF_CAPTURES	 = "NUMBER_OF_CAPTURES";

	private IIDSCCD	           ccd;
	private int	               numberOfCaptures;
	private int	               dimx;
	private int	               dimy;
	private IDSCCDColorModes	 mode;

	static
	{
		try
		{
			System.setProperty("jna.library.path", System.getProperty("user.dir") + File.pathSeparator + "lib");
		}
		catch (Throwable t)
		{
			throw new RuntimeException(t);
		}
	}

	public LPTScanProgram() throws IDSCCDException
	{
		super(PROGRAM_NAME);

		this.ccd = IDSCCDFactory.getIDSCCD();
	}

	public ProgramResult execute(ProgramParameters parameters, ICommunicationPort port) throws CommunicationPortException
	{
		try
		{
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

	protected MeasureResult getMeasureFromDetector(ICommunicationPort port, MeasureParameters measureParameters) throws CommunicationPortException
	{
		try
		{
			BufferedImage capture = null;

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
				centroid = this.ccd.getCentroid(buffer, this.dimx, this.dimy, false);

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

			x_standard_deviation = x_standard_deviation / (this.numberOfCaptures - 1);
			y_standard_deviation = y_standard_deviation / (this.numberOfCaptures - 1);

			// save last image

			capture = ccd.buildImage(buffer, this.dimx, this.dimy);

			Graphics2D g = capture.createGraphics();
			g.setColor(Color.YELLOW);
			g.fillOval((int) average_x_position - 10, (int) average_y_position - 10, 20, 20);

			MeasureResult result = new MeasureResult(this.calculateSlopeError(average_x_position, average_y_position));
			result.setAdditionalInformation1(average_x_position * IIDSCCD.PIXEL_SIZE);
			result.setAdditionalInformation2(average_y_position * IIDSCCD.PIXEL_SIZE);

			if (measureParameters.getAxis() == Axis.MOTOR5)
				result.addCustomData(ENCODER_POSITION, Double.valueOf(EncoderReaderFactory.getEncoderReader().readPosition()));

			result.addCustomData(X_STANDARD_DEVIATION, Double.valueOf(x_standard_deviation * IIDSCCD.PIXEL_SIZE));
			result.addCustomData(Y_STANDARD_DEVIATION, Double.valueOf(y_standard_deviation * IIDSCCD.PIXEL_SIZE));
			result.addCustomData(LAST_IMAGE, capture);

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
	}

	private double calculateSlopeError(double average_x_position, double average_y_position)
	{
		return 0;
	}

	protected void openShutter() throws IOException, InterruptedException
	{
	}

	protected void closeShutter() throws IOException
	{
		GuiUtilities.showMessagePopup("Scan Completed!", this.panel);
	}

	protected void executeMoveProgram(ICommunicationPort port, MoveParameters axisMoveParameters) throws CommunicationPortException
	{
		ProgramsFacade.executeProgram(LPTMOVEProgram.PROGRAM_NAME, axisMoveParameters, port);
	}
}
