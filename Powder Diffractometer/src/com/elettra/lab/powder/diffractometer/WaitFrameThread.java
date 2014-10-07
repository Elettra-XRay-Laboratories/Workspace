package com.elettra.lab.powder.diffractometer;

public class WaitFrameThread extends Thread
{
	public WaitFrameThread()
	{
	}

	public void run()
	{
		WaitFrame.getInstance().setVisible(true);
	}
}
