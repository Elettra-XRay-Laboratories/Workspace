package com.elettra.controller.driver.commands;

import com.elettra.common.utilities.ObjectUtilities;
import com.elettra.controller.driver.common.Sign;
import com.elettra.controller.driver.listeners.IDriverListener;

public class CommandParameters
{
	private int    axis;
	private Sign   sign;
	private double position;
	
	private IDriverListener listener;

	public CommandParameters(IDriverListener listener)
	{
		this(0, null, 0, listener);
	}

	public CommandParameters(int axis, IDriverListener listener)
	{
		this(axis, null, 0, listener);
	}

	public CommandParameters(int axis, Sign sign, double position, IDriverListener listener)
	{
		ObjectUtilities.checkObject(listener, "listener");
		
		this.axis = axis;
		this.sign = sign;
		this.position = position;
		this.listener = listener;
	}

	public int getAxis()
  {
  	return this.axis;
  }

	public Sign getSign()
  {
  	return this.sign;
  }

	public double getPosition()
  {
  	return this.position;
  }

	public IDriverListener getListener()
  {
  	return this.listener;
  }
}
