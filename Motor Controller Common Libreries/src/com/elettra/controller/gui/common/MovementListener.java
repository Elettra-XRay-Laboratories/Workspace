package com.elettra.controller.gui.common;

import javax.swing.JPanel;

import com.elettra.common.io.ICommunicationPort;
import com.elettra.common.io.CommunicationPortException;
import com.elettra.controller.driver.listeners.IDriverListener;
import com.elettra.controller.driver.listeners.MeasurePoint;
import com.elettra.controller.driver.listeners.Progress;

public abstract class MovementListener extends JPanel implements IDriverListener
{
	/**
   * 
   */
	private static final long serialVersionUID = 326508518510277833L;

	public final void signalMeasure(int axis, MeasurePoint point, Progress progress, ICommunicationPort port) throws CommunicationPortException
	{
	}

	
	public void signalXAxisRange(int axis, double startPosition, double stopPosition) throws CommunicationPortException
  {
  }


	public boolean isStopScanActivated(int axis)
	{
		return false;
	}
}
