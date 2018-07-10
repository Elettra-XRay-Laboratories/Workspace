package com.elettra.common.io;

import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;

import com.elettra.common.utilities.StringUtilities;

class SerialPort64Wrapper implements ICommunicationPort
{

	private String	   portName;
	private SerialPort	port;

	// private static final int TIMEOUT = 5000;

	public SerialPort64Wrapper(String portName, String applicationName) throws CommunicationPortException
	{
		StringUtilities.checkString(portName, "portName");
		StringUtilities.checkString(applicationName, "applicationName");

		//
		// Get an enumeration of all ports known to JavaComm
		//
		String[] portNames = SerialPortList.getPortNames();

		//
		// Check each port identifier if
		// (a) it indicates a serial (not a parallel) port, and
		// (b) matches the desired name.
		//

		boolean found = false;

		for (int index = 0; index < portNames.length && !found; index++)
		{
			if (portNames[index].equalsIgnoreCase(portName))
			{
				try
				{
					this.port = new SerialPort(portNames[index]);
					this.port.openPort();

					this.portName = portNames[index];

					found = true;
				}
				catch (SerialPortException e)
				{
					throw new CommunicationPortException(e);
				}
			}
		}

		if (!found)
			throw new CommunicationPortException("Serial Port " + portName + " not existing!");
	}

	public synchronized String getName()
	{
		return this.portName;
	}

	public synchronized void initialize(CommunicationPortParameters parameters) throws CommunicationPortException
	{
		try
		{
			this.port.setParams(((SerialPortParameters) parameters).getBaudrate(), ((SerialPortParameters) parameters).getDatabits(),
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
			this.port.writeBytes(buffer);
		}
		catch (Throwable t)
		{
			throw new CommunicationPortException(t);
		}
	}

	public byte[] readBytes() throws CommunicationPortException
	{
		try
		{
			return this.port.readBytes();
		}
		catch (Throwable t)
		{
			throw new CommunicationPortException(t);
		}
	}

	public byte[] readBytes(int bytes) throws CommunicationPortException
	{
		try
		{
			return this.port.readBytes(bytes);
		}
		catch (Throwable t)
		{
			throw new CommunicationPortException(t);
		}
	}

	public synchronized String read() throws CommunicationPortException
	{
		try
		{
			byte[] array = this.readBytes();

			if (array == null)
				return "";
			else
				return new String(array);
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
			byte[] array = this.readBytes(bytes);

			if (array == null)
				return null;
			else
				return new String(array);
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
				this.port.closePort();
			}
			catch (SerialPortException e)
			{
			}
		}
	}
}
