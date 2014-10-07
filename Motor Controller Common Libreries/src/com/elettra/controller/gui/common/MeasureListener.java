package com.elettra.controller.gui.common;

import javax.swing.JPanel;

import com.elettra.common.io.ICommunicationPort;
import com.elettra.common.io.CommunicationPortException;
import com.elettra.controller.driver.listeners.IDriverListener;

public abstract class MeasureListener extends JPanel implements IDriverListener
{
	/**
   * 
   */
  private static final long serialVersionUID = 9187642919204574519L;

	public final void signalAxisMovement(int axis, ICommunicationPort port) throws CommunicationPortException
	{
	}

	public void signalXAxisRange(int axis, double startPosition, double stopPosition) throws CommunicationPortException
  {
  }

	public void signalScanStart()
  {
  }

	public void signalScanStop()
  {
  }

	public boolean isStopScanActivated(int axis)
  {
	  return false;
  }
}
