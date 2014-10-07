package com.elettra.common.io;

public interface ICommunicationPort
{
	public String getName();

	public void initialize(CommunicationPortParameters parameters) throws CommunicationPortException;

	public void write(String buffer) throws CommunicationPortException;

	public String read() throws CommunicationPortException;

	public void release();
}
