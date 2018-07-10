package com.elettra.lab.areadetector.facility;

import javax.swing.UIManager;

import com.elettra.common.io.ICommunicationPort;
import com.elettra.controller.driver.commands.CommandsFacade;
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

			ICommunicationPort port = MainAreaDetector.initializeCommunicationPort();
			
			CommandsFacade.executeCommand(CommandsFacade.Commands.REMOTE, null, port);
			
			DriverUtilities.initialiazeAxisConfigurationMap(MainAreaDetector.getAxisConf());
			
			MainAreaDetector.restoreSavedAxisPosition(port);
			
			ListenerRegister.getInstance().reset();
			
			System.out.println("SW: " + CommandsFacade.executeAction(CommandsFacade.Actions.REQUEST_SOFTWARE_VERSION, null, port));
			
			MainAreaDetector frame = new MainAreaDetector(port);
			frame.setVisible(true);
		}
		catch (Throwable t)
		{
			isError = true;

			GuiUtilities.showErrorPopup("Exception Occurred:" + t.getClass().getName() + " - " + t.getMessage(), null);
		}
		finally
		{
			if (isError)
				System.exit(1);
		}
	}

}
