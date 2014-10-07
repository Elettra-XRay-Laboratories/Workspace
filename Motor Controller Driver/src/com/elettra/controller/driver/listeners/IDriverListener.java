package com.elettra.controller.driver.listeners;

import com.elettra.common.io.ICommunicationPort;
import com.elettra.common.io.CommunicationPortException;

public interface IDriverListener
{
	public void signalXAxisRange(int axis, double startPosition, double stopPosition) throws CommunicationPortException;

	public void signalMeasure(int axis, MeasurePoint point, Progress progress, ICommunicationPort port) throws CommunicationPortException;

	public void signalAxisMovement(int axis, ICommunicationPort port) throws CommunicationPortException;

	public void signalScanStart();

	public void signalScanStop();

	public boolean isStopScanActivated(int axis);
}
