package com.elettra.lab.areadetector.io;

import com.elettra.common.io.SerialPortRemoteWrapperServer;
import com.elettra.controller.gui.common.GuiUtilities;

public class SerialPortServer
{

	public static void main(String[] args)
	{
		try
		{
		 	String portName = GuiUtilities.getPortNames().listIterator().next();

		  SerialPortRemoteWrapperServer server = new SerialPortRemoteWrapperServer(portName, "SERIALPORT_SERVER");
	
			server.runServer();
		}
		catch (Exception exception)
		{
			exception.printStackTrace();
		}
	}
}
