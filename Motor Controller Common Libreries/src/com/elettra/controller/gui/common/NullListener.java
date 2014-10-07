package com.elettra.controller.gui.common;

import com.elettra.common.io.ICommunicationPort;
import com.elettra.common.io.CommunicationPortException;
import com.elettra.controller.driver.listeners.IDriverListener;
import com.elettra.controller.driver.listeners.MeasurePoint;
import com.elettra.controller.driver.listeners.Progress;

final class NullListener implements IDriverListener
{

	public void signalMeasure(int axis, MeasurePoint point, Progress progress, ICommunicationPort port) throws CommunicationPortException
	{
	}

	public void signalAxisMovement(int axis, ICommunicationPort port) throws CommunicationPortException
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

	public void signalXAxisRange(int axis, double startPosition, double stopPosition) throws CommunicationPortException
  {
  }

}
