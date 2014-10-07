package com.elettra.common.io;

import java.io.IOException;

public class CommunicationPortException extends IOException
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1144320769567769077L;

	public CommunicationPortException()
	{
	}

	public CommunicationPortException(String message)
	{
		super(message);
	}

	public CommunicationPortException(Throwable cause)
	{
		super(cause);
	}

	public CommunicationPortException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
