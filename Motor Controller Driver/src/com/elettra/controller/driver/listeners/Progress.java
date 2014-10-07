package com.elettra.controller.driver.listeners;

public class Progress
{
  private int progress;
  
  public Progress()
  {
  	this.progress = 0;
  }

	public int getProgress()
  {
  	return this.progress;
  }

	public synchronized void setProgress(int progress)
  {
		if (progress < 0 || progress > 100)
			throw new IllegalArgumentException("Progress is a number between 0 and 100");
		
  	this.progress = progress;
  }
}
