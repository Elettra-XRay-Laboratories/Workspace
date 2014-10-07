package com.elettra.controller.driver.programs;

import com.elettra.controller.driver.common.KindOfMovement;
import com.elettra.controller.driver.common.Sign;
import com.elettra.controller.driver.listeners.IDriverListener;

public class MoveParameters extends ProgramParameters
{
	private Sign           sign;
	private double         position;
	private KindOfMovement kindOfMovement;
	
	public MoveParameters(int axis, IDriverListener listener)
	{
		super(axis, listener);
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
