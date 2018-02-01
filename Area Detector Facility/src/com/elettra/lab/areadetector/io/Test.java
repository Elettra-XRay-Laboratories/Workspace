package com.elettra.lab.areadetector.io;

import java.io.IOException;

import com.elettra.common.io.CommunicationPortFactory;
import com.elettra.common.io.CommunicationPortUtilies;
import com.elettra.common.io.ICommunicationPort;
import com.elettra.common.io.KindOfPort;
import com.elettra.common.io.SerialPortParameters;
import com.elettra.controller.driver.commands.CommandsFacade;
import com.elettra.controller.driver.commands.CommandsFacade.Commands;
import com.elettra.controller.gui.common.GuiUtilities;

public class Test
{

	public static void main(String[] args) throws InterruptedException
	{
    try
    {
	    ICommunicationPort port = Test.initializeCommunicationPort();
	    
	    System.out.println(port.getName());
	    
	    CommandsFacade.executeCommand(Commands.LOCAL, null, port);

	    CommandsFacade.executeCommand(Commands.REMOTE, null, port);

	    System.out.println(CommandsFacade.executeAction(CommandsFacade.Actions.REQUEST_SOFTWARE_VERSION, null, port));
	    
	    port.release();
    }
    catch (Exception e)
    {
	    e.printStackTrace();
    }
	}

	
	static ICommunicationPort initializeCommunicationPort() throws IOException
	{
		CommunicationPortFactory.setApplicationName("TEST");

		ICommunicationPort port = null;
		KindOfPort kindOfPort = GuiUtilities.getKindOfPort();

		if (kindOfPort.equals(CommunicationPortUtilies.getSerialPortRemote()) || kindOfPort.equals(CommunicationPortUtilies.getSerialPort()))
		{
			String portName = GuiUtilities.getPortNames().listIterator().next();

			port = CommunicationPortFactory.getPort(portName, kindOfPort);

			SerialPortParameters parameters = new SerialPortParameters();
			parameters.deserialize(GuiUtilities.getPortConfFileName(portName));

			port.initialize(parameters);
		}

		return port;
	}	
	
}
