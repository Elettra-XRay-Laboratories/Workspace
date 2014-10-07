package com.elettra.lab.powder.mca;

import java.io.IOException;

public class MCAException extends IOException
{

	/**
   * 
   */
  private static final long serialVersionUID = -7427689408064759344L;

	public MCAException()
	{
	}

	public MCAException(String message)
	{
		super(message);

	}

	public MCAException(Throwable cause)
	{
		super(cause);

	}

	public MCAException(String message, Throwable cause)
	{
		super(message, cause);

	}

}
