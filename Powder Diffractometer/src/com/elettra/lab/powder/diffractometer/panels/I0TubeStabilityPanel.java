package com.elettra.lab.powder.diffractometer.panels;

import com.elettra.common.io.CommunicationPortException;
import com.elettra.common.io.ICommunicationPort;
import com.elettra.controller.gui.panels.TubeStabilityPanel;
import com.elettra.lab.powder.diffractometer.programs.I0STABILITYProgram;

public class I0TubeStabilityPanel extends TubeStabilityPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6079284865563083219L;

	public I0TubeStabilityPanel(ICommunicationPort port)
			throws CommunicationPortException
	{
		super(port);
	}

	protected Thread getScanThread()
	{
		return new I0StabilityThread(this);
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
		return "I0 Monochromatic";
	}

	protected String getAdditionaInfo2TabName()
	{
		return "I0 White Beam";
	}

	protected String getAdditionalInfo1Name()
	{
		return "I0 Monochromatic (pA)";
	}

	protected String getAdditionalInfo2Name()
	{
		return "I0 White Beam (pA)";
	}

	class I0StabilityThread extends TubeStabilityPanel.ScanThread
	{
		public I0StabilityThread(I0TubeStabilityPanel panel)
		{
			super(panel);
		}

		protected String getScanProgramName()
		{
			return I0STABILITYProgram.PROGRAM_NAME;
		}
	}
}
