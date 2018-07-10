package com.elettra.controller.driver.programs;

import com.elettra.controller.driver.common.KindOfMovement;
import com.elettra.controller.driver.common.Sign;
import com.elettra.controller.driver.listeners.IDriverListener;

public class TwoMotorsMoveParameters extends ProgramParameters
{
	private Sign           sign;
	private double         position;
	private KindOfMovement kindOfMovement;
	private int            axis2;

	public TwoMotorsMoveParameters(int axis1, int axis2, IDriverListener listener)
	{
		super(axis1, listener);
		
		this.axis2 = axis2;
	}

	public final int getAxis1()
	{
		return this.getAxis();
	}

	public final int getAxis2()
	{
		return this.axis2;
	}

	public Sign getSign()
	{
		return this.sign;
	}

	public double getPosition()
	{
		return this.position;
	}

	public KindOfMovement getKindOfMovement()
	{
		return this.kindOfMovement;
	}

	public synchronized void setSign(Sign sign)
	{
		this.sign = sign;
	}

	public synchronized void setPosition(double position)
	{
		this.position = position;
	}

	public synchronized void setKindOfMovement(KindOfMovement kindOfMovement)
	{
		this.kindOfMovement = kindOfMovement;
	}

}
