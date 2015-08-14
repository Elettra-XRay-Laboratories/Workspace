package com.elettra.common.io;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Enumeration;

import javax.comm.CommPortIdentifier;
import javax.comm.SerialPort;

import com.elettra.common.utilities.StringUtilities;

class SerialPortWrapper implements ICommunicationPort
{

	private String	         portName;
	private SerialPort	     port;

	private PrintStream	     outputStream;
	private BufferedReader	 inputStream;

	private static final int	TIMEOUT	= 5000;

	@SuppressWarnings("rawtypes")
	public SerialPortWrapper(String portName, String applicationName) throws CommunicationPortException
	{
		StringUtilities.checkString(portName, "portName");
		StringUtilities.checkString(applicationName, "applicationName");

		//
		// Get an enumeration of all ports known to JavaComm
		//
		Enumeration portIdentifiers = CommPortIdentifier.getPortIdentifiers();
		//
		// Check each port identifier if
		// (a) it indicates a serial (not a parallel) port, and
		// (b) matches the desired name.
		//

		while (portIdentifiers.hasMoreElements())
		{
			CommPortIdentifier pid = (CommPortIdentifier) portIdentifiers.nextElement();
			if (pid.getPortType() == CommPortIdentifier.PORT_SERIAL && portName.equals(pid.getName()))
			{
				try
				{
					this.port = (SerialPort) pid.open(applicationName, SerialPortWrapper.TIMEOUT);

					this.portName = portName;

				}
				catch (Throwable e)
				{
					throw new CommunicationPortException(e);
				}
			}
		}
	}

	public synchronized String getName()
	{
		return this.portName;
	}

	public synchronized void initialize(CommunicationPortParameters parameters) throws CommunicationPortException
	{
		try
		{
			this.port.setSerialPortParams(((SerialPortParameters) parameters).getBaudrate(), ((SerialPortParameters) parameters).getDatabits(),
			    ((SerialPortParameters) parameters).getStopbits(), ((SerialPortParameters) parameters).getParity());

		}
		catch (Throwable e)
		{
			throw new CommunicationPortException(e);
		}
	}

	public synchronized void write(String buffer) throws CommunicationPortException
	{
		this.write(buffer.getBytes());
	}

	public synchronized void write(byte[] buffer) throws CommunicationPortException
	{
		try
		{
			if (this.outputStream == null)
				this.outputStream = new PrintStream(this.port.getOutputStream());

			this.outputStream.print(buffer);
			this.outputStream.flush();

		}
		catch (Throwable t)
		{
			throw new CommunicationPortException(t);
		}
	}

	public byte[] readBytes() throws CommunicationPortException
	{
		throw new UnsupportedOperationException();
	}

	public byte[] readBytes(int bytes) throws CommunicationPortException
	{
		throw new UnsupportedOperationException();
	}

	public synchronized String read() throws CommunicationPortException
	{
		try
		{
			Thread.sleep(1000);
			
			if (this.inputStream == null)
				this.inputStream = new BufferedReader(new InputStreamReader(this.port.getInputStream()));

			return this.inputStream.readLine();
		}
		catch (Throwable t)
		{
			throw new CommunicationPortException(t);
		}
	}

	public synchronized String read(int bytes) throws CommunicationPortException
	{
		try
		{
			if (this.inputStream == null)
				this.inputStream = new BufferedReader(new InputStreamReader(this.port.getInputStream()));

			char[] cbuf = new char[bytes];

			this.inputStream.read(cbuf, 0, bytes);

			return String.copyValueOf(cbuf);
		}
		catch (Throwable t)
		{
			throw new CommunicationPortException(t);
		}
	}

	public synchronized void release()
	{
		if (this.port != null)
		{
			try
			{
				this.inputStream.close();
			}
			catch (Throwable t)
			{
			}

			try
			{
				this.outputStream.close();
			}
			catch (Throwable t)
			{
			}

			this.port.close();
		}
	}
}
