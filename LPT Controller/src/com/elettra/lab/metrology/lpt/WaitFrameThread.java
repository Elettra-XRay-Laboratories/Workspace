package com.elettra.lab.metrology.lpt;

import java.io.IOException;

public class WaitFrameThread extends Thread
{
	public WaitFrameThread()
	{
	}

	public void run()
	{
		try {
			WaitFrame.getInstance().setVisible(true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
