package com.elettra.lab.metrology.lpt.panels;

import com.elettra.common.io.CommunicationPortException;
import com.elettra.common.io.ICommunicationPort;
import com.elettra.controller.driver.programs.ProgramsFacade;
import com.elettra.controller.driver.programs.StabilityParameters;
import com.elettra.controller.gui.common.GuiUtilities;
import com.elettra.controller.gui.panels.TubeStabilityPanel;
import com.elettra.idsccd.driver.IDSCCDColorModes;
import com.elettra.idsccd.driver.IIDSCCD;
import com.elettra.lab.metrology.lpt.programs.LPTSTABILITYProgram;

public class LPTStabilityPanel extends TubeStabilityPanel
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= -6697640136632434040L;

	public LPTStabilityPanel(ICommunicationPort port) throws CommunicationPortException
	{
		super(port);
		
		this.scanGraphTabbedPane.setSelectedIndex(1);
	}

	protected boolean isAdditionalInformation1Visible()
	{
		return true;
	}

	protected boolean isAdditionalInformation2Visible()
	{
		return true;
	}

	protected String getAdditionaInfo1TabName()
	{
		return "Centroid X Position";
	}

	protected String getAdditionalInfo1Name()
	{
		return "Centroid X Position (um)";
	}

	protected String getAdditionaInfo2TabName()
	{
		return "Centroid Y Position";
	}

	protected String getAdditionalInfo2Name()
	{
		return "Centroid Y Position (um)";
	}

	protected String getAdditionalInfo1Format()
	{
		return "%9.1f";
	}

	protected String getAdditionalInfo2Format()
	{
		return "%9.1f";
	}

	protected Thread getScanThread()
	{
		return new LTPScanThread(this);
	}

	protected class LTPScanThread extends TubeStabilityPanel.ScanThread
	{

		public LTPScanThread(LPTStabilityPanel panel)
		{
			super(panel);
		}

		public void run()
		{
			try
			{
				((LPTSTABILITYProgram) ProgramsFacade.getProgram(LPTSTABILITYProgram.PROGRAM_NAME)).initialize();

				try
				{
					super.run();
				}
				finally
				{
					((LPTSTABILITYProgram) ProgramsFacade.getProgram(LPTSTABILITYProgram.PROGRAM_NAME)).terminate();
				}
			}
			catch (Exception exception)
			{
				GuiUtilities.showErrorPopup(exception.getMessage(), super.panel);
			}
		}

		protected String getScanProgramName()
		{
			return LPTSTABILITYProgram.PROGRAM_NAME;
		}

		protected void addCustomParameters(StabilityParameters scanParameters)
		{
			scanParameters.addCustomParameter(LPTSTABILITYProgram.COLOR_MODE, IDSCCDColorModes.IS_CM_MONO8);
			scanParameters.addCustomParameter(LPTSTABILITYProgram.DIM_X, IIDSCCD.DIM_X);
			scanParameters.addCustomParameter(LPTSTABILITYProgram.DIM_Y, IIDSCCD.DIM_Y);
			scanParameters.addCustomParameter(LPTSTABILITYProgram.NUMBER_OF_CAPTURES, 1);
			scanParameters.addCustomParameter(LPTSTABILITYProgram.STEP_DURATION, 1);
		}

	}

}
