package com.elettra.lab.metrology.lpt.programs;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.elettra.common.io.CommunicationPortException;
import com.elettra.common.io.ICommunicationPort;
import com.elettra.controller.driver.listeners.MeasurePoint;
import com.elettra.controller.driver.programs.AbstractProgram;
import com.elettra.controller.driver.programs.MeasureResult;
import com.elettra.controller.driver.programs.ProgramParameters;
import com.elettra.controller.driver.programs.ProgramResult;
import com.elettra.controller.gui.common.GuiUtilities;
import com.elettra.idsccd.driver.IDSCCDColorModes;
import com.elettra.idsccd.driver.IDSCCDDisplayModes;
import com.elettra.idsccd.driver.IDSCCDException;
import com.elettra.idsccd.driver.IDSCCDFactory;
import com.elettra.idsccd.driver.IIDSCCD;
import com.elettra.idsccd.driver.Point;

public class LPTLiveCCDProgram extends AbstractProgram
{
	public static final String	Y	                  = "Y";
	public static final String	X                  	= "X";
	public static final String	Y_FWHM	            = "Y_FWHM";
	public static final String	X_FWHM	            = "X_FWHM";
	public static final String	LAST_IMAGE	        = "LAST_IMAGE";
	public static final String	PROGRAM_NAME	      = "LPT_CCD_LIVE";

	public static final String	COLOR_MODE	        = "COLOR_MODE";
	public static final String	GAIN      	        = "GAIN";	
	public static final String	DIM_X	              = "DIM_X";
	public static final String	DIM_Y	              = "DIM_Y";
	public static final String	DRAW_MAIN_GRID	    = "DRAW_MAIN_GRID";
	public static final String	DRAW_SECONDARY_GRID	= "DRAW_SECONDARY_GRID";

	private IIDSCCD	           ccd;
	private int	               dimx;
	private int	               dimy;
	private IDSCCDColorModes	 mode;
	private int                gain;
	private boolean	           drawMainGrid;
	private boolean	           drawSecondaryGrid;

	public LPTLiveCCDProgram() throws IDSCCDException
	{
		super(PROGRAM_NAME);

		this.ccd = IDSCCDFactory.getIDSCCD();
	}

	public ProgramResult execute(ProgramParameters parameters, ICommunicationPort port) throws CommunicationPortException
	{
		try
		{
			this.mode = (IDSCCDColorModes) parameters.getCustomParameter(COLOR_MODE);
			this.gain = ((Integer) parameters.getCustomParameter(GAIN)).intValue();
			this.dimx = ((Integer) parameters.getCustomParameter(DIM_X)).intValue();
			this.dimy = ((Integer) parameters.getCustomParameter(DIM_Y)).intValue();

			this.drawMainGrid = ((Boolean) parameters.getCustomParameter(DRAW_MAIN_GRID)).booleanValue();
			this.drawSecondaryGrid = ((Boolean) parameters.getCustomParameter(DRAW_SECONDARY_GRID)).booleanValue();

			this.ccd.setColorMode(mode);
			this.ccd.setHardwareGain(gain);
			this.ccd.setDisplayMode(IDSCCDDisplayModes.IS_SET_DM_DIB);

			MeasureResult measureResult = this.getMeasureFromDetector();

			MeasurePoint measurePoint = new MeasurePoint(0.0, measureResult.getMeasure(), measureResult.getAdditionalInformation1(), measureResult.getAdditionalInformation2());
			measurePoint.setCustomData(measureResult.getCustomData());

			parameters.getListener().signalMeasure(parameters.getAxis(), measurePoint, null, port);

			return measureResult;
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

	protected MeasureResult getMeasureFromDetector() throws CommunicationPortException
	{
		MeasureResult result = new MeasureResult(0.0);

		try
		{
			int[] buffer = this.ccd.getImageByMemory(this.dimx, this.dimy, mode);

			BufferedImage capture = ccd.buildImage(buffer, this.dimx, this.dimy);
			Graphics2D g = capture.createGraphics();
			g.setColor(Color.YELLOW);

			try
			{
				Point centroid = this.ccd.getCentroid(buffer, this.dimx, this.dimy, false);

				g.drawLine((int) centroid.x - 10, (int) centroid.y, (int) centroid.x + 10, (int) centroid.y);
				g.drawLine((int) centroid.x , (int) centroid.y - 10, (int) centroid.x, (int) centroid.y + 10);
				
				double centroid_x = (centroid.x - this.dimx / 2) * IIDSCCD.PIXEL_SIZE;
				double centroid_y = -(centroid.y - this.dimy / 2) * IIDSCCD.PIXEL_SIZE;
				double centroid_fwhm_x = centroid.fwhm_x * IIDSCCD.PIXEL_SIZE;
				double centroid_fwhm_y = centroid.fwhm_y * IIDSCCD.PIXEL_SIZE;
				
				result.setAdditionalInformation1(Double.valueOf(centroid_x));
				result.setAdditionalInformation2(Double.valueOf(centroid_y));
				result.addCustomData(X, centroid_x);
				result.addCustomData(Y, centroid_y);
				result.addCustomData(X_FWHM, centroid_fwhm_x);
				result.addCustomData(Y_FWHM, centroid_fwhm_y);
			
				g.setFont(new Font("Verdana", Font.BOLD, 40));
				g.drawString("(" + GuiUtilities.parseDouble(centroid_x, 1, true) + "," + GuiUtilities.parseDouble(centroid_y, 1, true) + ")", (int) centroid.x + 50,
				    (int) centroid.y - 50);
			}
			catch (Exception e)
			{
				g.setFont(new Font("Verdana", Font.BOLD, 40));
				g.drawString("Centroid not calculated", 50, 50);

				result.setAdditionalInformation1(Double.valueOf(0.0));
				result.setAdditionalInformation2(Double.valueOf(0.0));
				result.addCustomData(X, 0.0);
				result.addCustomData(Y, 0.0);
				result.addCustomData(X_FWHM, 0.0);
				result.addCustomData(Y_FWHM, 0.0);
			}

			if (this.drawMainGrid)
			{
				g.setColor(Color.WHITE);
				g.setStroke(new BasicStroke(5, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 9 }, 0));
				g.drawLine(0, this.dimy / 2, this.dimx, this.dimy / 2);
				g.drawLine(this.dimx / 2, 0, this.dimx / 2, this.dimy);
			}

			if (this.drawSecondaryGrid)
			{
				g.setColor(Color.LIGHT_GRAY);
				g.setStroke(new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 9 }, 0));
				g.drawLine(0, this.dimy / 4, this.dimx, this.dimy / 4);
				g.drawLine(0, 3 * this.dimy / 4, this.dimx, 3 * this.dimy / 4);
				g.drawLine(this.dimx / 4, 0, this.dimx / 4, this.dimy);
				g.drawLine(3 * this.dimx / 4, 0, 3 * this.dimx / 4, this.dimy);
			}

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
}
