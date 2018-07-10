package com.elettra.lab.metrology.lpt.programs;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
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
import com.elettra.idsccd.driver.Point;
import com.elettra.lab.metrology.lpt.Axis;
import com.elettra.lab.metrology.lpt.encoder.EncoderReaderFactory;
import com.elettra.lab.metrology.lpt.panels.References;

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
	public static final String	PREVIOUS_X0	         = "PREVIOUS_X0";
  public static final String  DRAW_IMAGE           = "DRAW_IMAGE";
  
	private IIDSCCD	           ccd;
	private int	               numberOfCaptures;
	private int	               dimx;
	private int	               dimy;
	private IDSCCDColorModes	 mode;
	private double	           X0;
	private double	           focalDistance;
	private boolean            renderImage;

	public LPTScanProgram() throws IDSCCDException
	{
		super(PROGRAM_NAME);
		
		this.dump_measure = DumpMeasure.DUMP_AT_END;

		this.ccd = IDSCCDFactory.getIDSCCD();
	}

	public ProgramResult execute(ProgramParameters parameters, ICommunicationPort port) throws CommunicationPortException
	{
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
			this.renderImage = ((Boolean) parameters.getCustomParameter(DRAW_IMAGE)).booleanValue();
			
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

			// save last image

			if (this.renderImage)
			{
				capture = ccd.buildImage(buffer, this.dimx, this.dimy);
				
				Graphics2D g = capture.createGraphics();
				g.setColor(Color.YELLOW);
				g.fillOval((int) average_x_position - 10, (int) average_y_position - 10, 20, 20);
	
				g.setColor(Color.WHITE);
				g.setStroke(new BasicStroke(7, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 9 }, 0));
				g.drawLine(0, this.dimy / 2, this.dimx, this.dimy / 2);
				g.drawLine(this.dimx / 2, 0, this.dimx / 2, this.dimy);
			}

			buffer = null;

			// Refer to the center of the image

			average_x_position = (average_x_position - this.dimx / 2);
			average_y_position = -(average_y_position - this.dimy / 2);
			average_x_position *= IIDSCCD.PIXEL_SIZE;
			average_y_position *= IIDSCCD.PIXEL_SIZE;
						
			MeasureResult result = new MeasureResult(this.calculateSlopeError(average_x_position));

			result.setAdditionalInformation1(average_x_position);
			result.setAdditionalInformation2(average_y_position);

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
		GuiUtilities.showMessagePopup("Scan Completed!", this.panel);
	}

	protected void executeMoveProgram(ICommunicationPort port, MoveParameters axisMoveParameters) throws CommunicationPortException
	{
		axisMoveParameters.addCustomParameter("PRECISION", 0.005);

		ProgramsFacade.executeProgram(LPTMOVEProgram.PROGRAM_NAME, axisMoveParameters, port);
	}
	
	
	public static void main(String args[])
	{
		System.out.println(0.5 * Math.atan((-650+449)/(249.5*1000)));
	}
}
