package com.elettra.lab.powder.basement;

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
