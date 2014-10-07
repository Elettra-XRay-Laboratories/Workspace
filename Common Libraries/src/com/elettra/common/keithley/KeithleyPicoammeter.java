package com.elettra.common.keithley;

import java.io.IOException;
import java.util.Enumeration;

import be.ac.ulb.gpib.*;

public class KeithleyPicoammeter implements IPicoammeter
{
	private GPIBDevice          device;

	private static IPicoammeter uniqueInstance = null;

	static
	{
		GPIBDeviceIdentifier.initialize("be.ac.ulb.gpib.WindowsGPIBDriver", false);
	}

	private KeithleyPicoammeter() throws IOException
	{
		super();

		Enumeration<?> devicesList = GPIBDeviceIdentifier.getDevices();

		while (devicesList.hasMoreElements())
		{
			GPIBDeviceIdentifier deviceIdentifier = (GPIBDeviceIdentifier) devicesList.nextElement();

			if (deviceIdentifier.getAddress() == 22)
			{
				GPIBDriver gpibDriver = deviceIdentifier.getDriver();
				System.out.println("Found GPIB device with address '" + 22);

				this.device = new GPIBDevice(22, gpibDriver);

				this.device.open(1.0f);

				// filter enabled (digital+analog)
				this.device.sendCommand("P3");			
				// set range (3 = 200 nA; 0 = Autorange) - Integration Period (1 = line cycle integration)  
				this.device.sendCommand("R3S1X");			
				// Zero check disabled
				this.device.sendCommand("C0X");
			}
		}
	}

	public static synchronized IPicoammeter getInstance() throws IOException
	{
		if (uniqueInstance == null)
			uniqueInstance = new KeithleyPicoammeter();

		return uniqueInstance;
	}

	public double readCurrent() throws IOException
	{
		return 1E12 * Double.parseDouble(this.device.sendCommand("ID").substring(4));
	}
}
