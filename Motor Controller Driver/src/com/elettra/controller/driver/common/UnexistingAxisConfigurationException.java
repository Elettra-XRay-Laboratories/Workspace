package com.elettra.controller.driver.common;

public class UnexistingAxisConfigurationException extends IllegalArgumentException
{

	/**
   * 
   */
	private static final long serialVersionUID = -2047857518810720019L;

	public UnexistingAxisConfigurationException(int axis)
	{
		super("Configuration of Axis " + Integer.toString(axis) + " not existing");
	}

	public UnexistingAxisConfigurationException(int axis, Throwable cause)
	{
		super("Configuration of Axis " + Integer.toString(axis) + " not existing", cause);
	}
}
