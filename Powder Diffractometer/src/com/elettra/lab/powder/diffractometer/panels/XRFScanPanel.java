package com.elettra.lab.powder.diffractometer.panels;

import com.elettra.common.io.CommunicationPortException;
import com.elettra.common.io.ICommunicationPort;
import com.elettra.controller.gui.panels.ScanPanel;
import com.elettra.lab.powder.diffractometer.programs.XRFSCANProgram;

public class XRFScanPanel extends ScanPanel
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7968660580624556065L;

	public XRFScanPanel(int axis, ICommunicationPort port, boolean sendDataEnabled) throws CommunicationPortException
	{
		super(axis, port, sendDataEnabled);
	}

	public XRFScanPanel(int axis, ICommunicationPort port, boolean sendDataEnabled, int sendDataAxis1, int sendDataAxis2) throws CommunicationPortException
	{
		super(axis, port, sendDataEnabled, sendDataAxis1, sendDataAxis2);
	}

	protected Thread getScanThread()
	{
		return new XRFScanThread(this);
	}

	class XRFScanThread extends ScanPanel.ScanThread
	{
		public XRFScanThread(XRFScanPanel panel)
		{
			super(panel);
		}

		protected String getScanProgramName()
		{
			return XRFSCANProgram.PROGRAM_NAME;
		}
	}
}
