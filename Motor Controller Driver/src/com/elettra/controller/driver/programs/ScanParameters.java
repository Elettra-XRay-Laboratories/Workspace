package com.elettra.controller.driver.programs;

import javax.swing.JPanel;

import com.elettra.controller.driver.common.KindOfMovement;
import com.elettra.controller.driver.common.Sign;
import com.elettra.controller.driver.listeners.IDriverListener;

public class ScanParameters extends MeasureParameters
{
	private KindOfMovement kindOfMovement;
	private Sign           startSign;
	private double         startPosition;
	private Sign           stopSign;
	private double         stopPosition;
	private int            numberOfSteps;
	private double         stepValue;
	private JPanel         panel;

	public ScanParameters(int axis, IDriverListener listener)
	{
		super(axis, listener);
	}

	public KindOfMovement getKindOfMovement()
	{
		return this.kindOfMovement;
	}

	public Sign getStartSign()
	{
		return this.startSign;
	}

	public double getStartPosition()
	{
		return this.startPosition;
	}

	public Sign getStopSign()
	{
		return this.stopSign;
	}

	public double getStopPosition()
	{
		return this.stopPosition;
	}

	public int getNumberOfSteps()
	{
		return this.numberOfSteps;
	}

	public double getStepValue()
	{
		return this.stepValue;
	}

	public JPanel getPanel()
	{
		return this.panel;
	}

	public synchronized void setKindOfMovement(KindOfMovement kindOfMovement)
	{
		this.kindOfMovement = kindOfMovement;
	}

	public synchronized void setStartSign(Sign startSign)
	{
		this.startSign = startSign;
	}

	public synchronized void setStartPosition(double startPosition)
	{
		this.startPosition = startPosition;
	}

	public synchronized void setStopSign(Sign stopSign)
	{
		this.stopSign = stopSign;
	}

	public synchronized void setStopPosition(double stopPosition)
	{
		this.stopPosition = stopPosition;
	}

	public synchronized void setNumberOfSteps(int numberOfSteps)
	{
		this.numberOfSteps = numberOfSteps;
	}

	public synchronized void setStepValue(double stepValue)
	{
		this.stepValue = stepValue;
	}

	public synchronized void setPanel(JPanel panel)
	{
		this.panel = panel;
	}
}
