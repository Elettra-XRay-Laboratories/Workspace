package com.elettra.lab.metrology.lpt;

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
