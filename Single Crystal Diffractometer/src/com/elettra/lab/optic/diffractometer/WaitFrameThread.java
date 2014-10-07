package com.elettra.lab.optic.diffractometer;

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
