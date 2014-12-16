package com.elettra.lab.metrology.lpt;

import java.io.IOException;

import javax.swing.UIManager;

import com.elettra.common.io.ICommunicationPort;
import com.elettra.controller.driver.common.DriverUtilities;
import com.elettra.controller.gui.common.GuiUtilities;
import com.elettra.controller.gui.common.ListenerRegister;

public class MainFrameThread extends Thread {

	public void run() {
		boolean isError = false;

		try {
			Thread.sleep(1000);

			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

			Main.customizeDriver();
			ICommunicationPort port = Main.initializeCommunicationPort();
			DriverUtilities.initialiazeAxisConfigurationMap(Main.getAxisConf());
			Main.restoreSavedAxisPosition(port);
			Main.changeAxisMotorConfiguration(port);

			ListenerRegister.getInstance().reset();

			Main frame = new Main(port);

			frame.setVisible(true);
		} catch (Exception e) {
			isError = true;

			GuiUtilities.showErrorPopup("Exception Occurred:"
					+ e.getClass().getName() + " - " + e.getMessage(), null);
		} finally {
			try {
				WaitFrame.getInstance().setVisible(false);
			} catch (IOException e) {
				isError = true;

				GuiUtilities
						.showErrorPopup(
								"Exception Occurred:" + e.getClass().getName()
										+ " - " + e.getMessage(), null);
			}

			if (isError)
				System.exit(1);
		}
	}

}
