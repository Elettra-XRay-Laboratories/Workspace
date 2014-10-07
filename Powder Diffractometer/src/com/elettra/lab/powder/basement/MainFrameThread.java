package com.elettra.lab.powder.basement;

import javax.swing.UIManager;

import com.elettra.common.io.ICommunicationPort;
import com.elettra.controller.driver.common.DriverUtilities;
import com.elettra.controller.gui.common.GuiUtilities;
import com.elettra.controller.gui.common.ListenerRegister;

public class MainFrameThread extends Thread
{

	public void run()
	{
		boolean isError = false;

		try
		{
			Thread.sleep(1000);

			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

			ICommunicationPort port = MainBasement.initializeCommunicationPort();
			
			DriverUtilities.initialiazeAxisConfigurationMap(MainBasement.getAxisConf());
			
			MainBasement.restoreSavedAxisPosition(port);
			
			ListenerRegister.getInstance().reset();

			MainBasement frame = new MainBasement(port);
			frame.setVisible(true);
		}
		catch (Throwable t)
		{
			isError = true;

			GuiUtilities.showErrorPopup("Exception Occurred:" + t.getClass().getName() + " - " + t.getMessage(), null);
		}
		finally
		{
			WaitFrame.getInstance().setVisible(false);

			if (isError)
				System.exit(1);
		}
	}

}
