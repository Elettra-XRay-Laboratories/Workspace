package com.elettra.common.io;

import java.util.Enumeration;

import javax.comm.CommPortIdentifier;
import javax.comm.SerialPort;

import com.elettra.common.utilities.StringUtilities;

class SerialPortWrapper implements ICommunicationPort
{

	private String	         portName;
	private SerialPort	     port;

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
		
		this.port = null;
		this.portName = null;
				
		while (portIdentifiers.hasMoreElements() && this.port == null)
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
		
		if (this.port == null) throw new CommunicationPortException("Com Port " + portName + " Not Found");
	}

	public synchronized String getName()
	{
		return this.portName;
	}

	public synchronized void initialize(CommunicationPortParameters parameters) throws CommunicationPortException
	{
		try
		{
			this.port.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
			
			this.port.setSerialPortParams(
					((SerialPortParameters) parameters).getBaudrate(), 
					((SerialPortParameters) parameters).getDatabits(),
			    ((SerialPortParameters) parameters).getStopbits(), 
			    ((SerialPortParameters) parameters).getParity());
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
			
			this.port.getOutputStream().write(buffer); 
			this.port.getOutputStream().flush();
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

  	  StringBuffer buffer = new StringBuffer();
  	  do{
  	  	buffer.append((char) this.port.getInputStream().read());
  	  	
  	  } while (this.port.getInputStream().available() > 0);
  	  
  	  return buffer.toString();
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
			byte[] cbuf = new byte[bytes];

			this.port.getInputStream().read(cbuf, 0, bytes);

			return new String(cbuf);
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
			this.port.close();
		}
	}
}
