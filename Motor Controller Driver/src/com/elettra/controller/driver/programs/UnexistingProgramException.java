package com.elettra.controller.driver.programs;

public class UnexistingProgramException extends IllegalArgumentException
{

	/**
   * 
   */
	private static final long serialVersionUID = -973058415682658499L;

	public UnexistingProgramException(String programName)
	{
		super("Program " + programName + " not existing");

	}

	public UnexistingProgramException(String programName, Throwable cause)
	{
		super("Program " + programName + " not existing", cause);

	}

}
