package com.elettra.lab.powder.diffractometer.panels;

import com.elettra.common.io.CommunicationPortException;
import com.elettra.common.io.ICommunicationPort;
import com.elettra.controller.gui.panels.ScanPanel;
import com.elettra.lab.powder.diffractometer.programs.I0SCANProgram;

public class I0ScanPanel extends ScanPanel
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5230852571043508646L;

	public I0ScanPanel(int axis, ICommunicationPort port,
			boolean sendDataEnabled) throws CommunicationPortException
	{
		super(axis, port, sendDataEnabled);
	}

	public I0ScanPanel(int axis, ICommunicationPort port,
			boolean sendDataEnabled, int sendDataAxis1, int sendDataAxis2)
			throws CommunicationPortException
	{
		super(axis, port, sendDataEnabled, sendDataAxis1, sendDataAxis2);
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

	protected Thread getScanThread()
	{
		return new I0ScanThread(this);
	}

	class I0ScanThread extends ScanPanel.ScanThread
	{
		public I0ScanThread(I0ScanPanel panel)
		{
			super(panel);
		}

		protected String getScanProgramName()
		{
			return I0SCANProgram.PROGRAM_NAME;
		}
	}
}
