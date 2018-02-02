package com.elettra.lab.areadetector.io;

import java.io.IOException;

import com.elettra.common.io.CommunicationPortFactory;
import com.elettra.common.io.CommunicationPortUtilies;
import com.elettra.common.io.ICommunicationPort;
import com.elettra.common.io.KindOfPort;
import com.elettra.common.io.SerialPortParameters;
import com.elettra.controller.driver.commands.CommandsFacade;
import com.elettra.controller.driver.commands.CommandsFacade.Commands;
import com.elettra.controller.driver.common.AxisConfiguration;
import com.elettra.controller.driver.common.DriverUtilities;
import com.elettra.controller.driver.common.IAxisConfigurationMap;
import com.elettra.controller.driver.common.MultipleAxis;
import com.elettra.controller.driver.common.RelativeMovement;
import com.elettra.controller.driver.programs.DefaultAxisConfigurationMap;
import com.elettra.controller.driver.programs.MoveParameters;
import com.elettra.controller.driver.programs.ProgramsFacade;
import com.elettra.controller.driver.programs.TwoMotorsMoveParameters;
import com.elettra.controller.gui.common.GuiUtilities;
import com.elettra.lab.areadetector.facility.Axis;

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
	
			DriverUtilities.initialiazeAxisConfigurationMap(Test.getAxisConf());
			
			TwoMotorsMoveParameters moveParameters = new TwoMotorsMoveParameters(3, 4,  GuiUtilities.getNullListener());

			moveParameters.setKindOfMovement(DriverUtilities.parseKindOfMovement(RelativeMovement.KIND_OF_MOVEMENT));
			moveParameters.setSign(DriverUtilities.parseSign("-"));
			moveParameters.setPosition(Double.parseDouble("5.000"));

			ProgramsFacade.executeProgram(ProgramsFacade.Programs.TMMOVE, moveParameters, port);
			
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
	
	static IAxisConfigurationMap getAxisConf()
	{
		DefaultAxisConfigurationMap map = new DefaultAxisConfigurationMap();

		map.setAxisConfiguration(Axis.ROTATION, new AxisConfiguration(DriverUtilities.getDecimalGrades(), 1000, 2000, 33, 0, DriverUtilities.getPlus(), false, false, 1, "ROTATION", 0.0, 0.0));
		map.setAxisConfiguration(Axis.X, new AxisConfiguration(DriverUtilities.getMillimeters(), 1000, 2000, 33, 0, DriverUtilities.getPlus(), false, false, 1, "X", 0.0, 0.0));
		map.setAxisConfiguration(Axis.Z1, new AxisConfiguration(DriverUtilities.getMillimeters(), 1000, 2000, 33, 0, DriverUtilities.getPlus(), false, false, 1, "Z1", 0.0, 0.0));
		map.setAxisConfiguration(Axis.Z2, new AxisConfiguration(DriverUtilities.getDecimalGrades(), 1000, 2000, 20000, 0.00025, DriverUtilities.getPlus(), false, false, 1, "Z2", 0.0, 0.0));
		map.setAxisConfiguration(Axis.Z, new AxisConfiguration(DriverUtilities.getMillimeters(), new MultipleAxis(Axis.Z1, Axis.Z2, DriverUtilities.getPlus(), 2), "Z (Z1,Z2)"));
		
		return map;
	}

}
