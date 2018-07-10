package com.elettra.common.io;

public interface ICommunicationPort 
{
	public String getName();

	public void initialize(CommunicationPortParameters parameters) throws CommunicationPortException;

	public void write(String buffer) throws CommunicationPortException;

	public void write(byte[] buffer) throws CommunicationPortException;

	public byte[] readBytes() throws CommunicationPortException;

	public byte[] readBytes(int bytes) throws CommunicationPortException;

	public String read() throws CommunicationPortException;

	public String read(int bytes) throws CommunicationPortException;

	public void release();
}
