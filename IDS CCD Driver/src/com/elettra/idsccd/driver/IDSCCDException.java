package com.elettra.idsccd.driver;

public class IDSCCDException extends Exception
{

	/**
	 * 
	 */
  private static final long serialVersionUID = 1L;

	public IDSCCDException()
	{
	}

	public IDSCCDException(String message)
	{
		super(message);

	}

	public IDSCCDException(Throwable cause)
	{
		super(cause);

	}

	public IDSCCDException(String message, Throwable cause)
	{
		super(message, cause);

	}

	public IDSCCDException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);

	}

}
