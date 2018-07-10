package com.elettra.controller.driver.programs;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JOptionPane;

import com.elettra.common.utilities.FileIni;
import com.elettra.controller.driver.common.DriverUtilities;

abstract class ShutterActivatorProgram extends AbstractProgram
{
	public ShutterActivatorProgram(String programName)
	{
		super(programName);

	}

	protected void openShutter() throws IOException, InterruptedException
	{
		if (DriverUtilities.isRemoteShutter())
		{
			this.writeShutterFile("1");

			Thread.sleep(11000);
		}
		else
			JOptionPane.showMessageDialog(null, "Open the Shutter", "Message", JOptionPane.INFORMATION_MESSAGE);

	}

	protected void closeShutter() throws IOException
	{
		if (DriverUtilities.isRemoteShutter())
			this.writeShutterFile("0");
		else
			JOptionPane.showMessageDialog(null, "Close the Shutter", "Message", JOptionPane.INFORMATION_MESSAGE);
	}

	protected void writeShutterFile(String order) throws IOException
	{
		BufferedWriter writer = new BufferedWriter(new FileWriter(FileIni.getInstance().getProperty("ShutterFilePath")));

		try
		{
			writer.write(order);
			writer.flush();
		}
		finally
		{
			writer.close();
		}
	}

}
