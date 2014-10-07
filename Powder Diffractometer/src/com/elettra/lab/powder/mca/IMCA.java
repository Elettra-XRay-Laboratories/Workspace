package com.elettra.lab.powder.mca;

public interface IMCA
{
	public void clearDataAndTime() throws MCAException;

	public void doAcquisition(int timeInSeconds) throws MCAException;

	public int[] getData() throws MCAException;

	public void disconnect() throws MCAException;
}
