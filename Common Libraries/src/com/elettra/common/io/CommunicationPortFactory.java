package com.elettra.common.io;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.HashMap;

import javax.comm.CommDriver;

public class CommunicationPortFactory
{
	private static HashMap<String, ICommunicationPort> serialPortsMap   = new HashMap<String, ICommunicationPort>();
	private static HashMap<String, ICommunicationPort> ethernetPortsMap = new HashMap<String, ICommunicationPort>();
	private static String                              applicationName;
	private static ICommunicationPort                  emergencyPort    = null;

	static
	{
		try
		{
			if ("32".equals(System.getProperty("sun.arch.data.model")))
			{
				CommDriver commDriver = (CommDriver) Class.forName("com.sun.comm.Win32Driver").newInstance();
				commDriver.initialize();
			}
		}
		catch (Throwable t)
		{
			try
			{
				t.printStackTrace(new PrintStream(new FileOutputStream(".\\error.log", false)));
			}
			catch (FileNotFoundException exception)
			{
			}

			System.exit(1);
		}
	}

	public static synchronized void setApplicationName(String applicationName)
	{
		if (CommunicationPortFactory.applicationName == null)
			CommunicationPortFactory.applicationName = applicationName;
		else
			throw new IllegalAccessError("ApplicationName already initialized");
	}

	public static synchronized ICommunicationPort getEmergencyPort()
	{
		return emergencyPort;
	}

	public static synchronized ICommunicationPort getPort(String portName, KindOfPort kindOfPort) throws CommunicationPortException
	{
		if (kindOfPort.equals(CommunicationPortUtilies.getSerialPort()))
			return getSerialPort(portName);
		else if (kindOfPort.equals(CommunicationPortUtilies.getEthernetPort()))
		{
			emergencyPort = new EthernetPortWrapper(portName, CommunicationPortFactory.applicationName);

			return getEthernetPort(portName);
		}
		else
			return null;
	}

	private static synchronized ICommunicationPort getSerialPort(String portName) throws CommunicationPortException
	{
		if (serialPortsMap.containsKey(portName))
			return serialPortsMap.get(portName);
		else
		{
			ICommunicationPort newPort = new SerialPortWrapper(portName, CommunicationPortFactory.applicationName);
			serialPortsMap.put(portName, newPort);

			return newPort;
		}
	}

	private static synchronized ICommunicationPort getEthernetPort(String portName) throws CommunicationPortException
	{
		if (ethernetPortsMap.containsKey(portName))
			return ethernetPortsMap.get(portName);
		else
		{
			ICommunicationPort newPort = new EthernetPortWrapper(portName, CommunicationPortFactory.applicationName);
			ethernetPortsMap.put(portName, newPort);

			return newPort;
		}
	}

	public static synchronized void releasePort(ICommunicationPort port) throws CommunicationPortException
	{
		if (port == null)
			throw new IllegalArgumentException("port = null");

		port.release();

		if (port instanceof SerialPortWrapper)
			serialPortsMap.remove(port.getName());
		else if (port instanceof EthernetPortWrapper)
			ethernetPortsMap.remove(port.getName());
	}

}
