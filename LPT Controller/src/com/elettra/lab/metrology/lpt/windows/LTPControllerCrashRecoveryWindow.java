package com.elettra.lab.metrology.lpt.windows;

import java.awt.HeadlessException;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.elettra.common.io.ICommunicationPort;
import com.elettra.controller.gui.windows.ControllerCrashRecoveryWindow;

public class LTPControllerCrashRecoveryWindow extends ControllerCrashRecoveryWindow
{

	public static synchronized LTPControllerCrashRecoveryWindow getInstance(ICommunicationPort port) throws HeadlessException, IOException
	{
		return new LTPControllerCrashRecoveryWindow(port);
	}
	
	/**
	 * 
	 */
  private static final long serialVersionUID = 8676104425574821240L;

	public LTPControllerCrashRecoveryWindow(ICommunicationPort port) throws HeadlessException, IOException
	{
		super(port);
		
		
		this.setIconImage(ImageIO.read(new File("ltpcontroller.jpg")));
	}

}
